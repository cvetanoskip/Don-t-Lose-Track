package com.example.caloriecounter.User

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriecounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt
import android.app.Dialog

class StepTrackerActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var totalSteps = 0f
    private var previousSteps = 0f
    private var isSensorAvailable = false
    private lateinit var backButton: ImageButton
    private lateinit var stepCountText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var resetButton: Button
    private lateinit var addExerciseButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var userWeight: Double = 70.0 // Default weight
    private var lastStepTime: Long = 0
    private var lastFilteredAccel = 0.0
    private var lastGyroY = 0.0
    private val accelThreshold = 0.5
    private val gyroThreshold = 0.3
    private val stepCooldown = 250L
    private val lowPassFilterFactor = 0.4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_tracker)

        backButton = findViewById(R.id.back_button)
        stepCountText = findViewById(R.id.stepCountText)
        progressBar = findViewById(R.id.circularProgressBar)
        resetButton = findViewById(R.id.resetButton)
        addExerciseButton = findViewById(R.id.addExerciseButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Fetch user weight for calorie calculations
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { doc ->
                    userWeight = doc.getString("weight")?.toDouble() ?: 70.0
                    Log.d("StepTracker", "Fetched user weight: $userWeight kg")
                }
                .addOnFailureListener {
                    Log.e("StepTracker", "Error fetching user weight", it)
                }
        }

        loadStepsFromFirebase()

        backButton.setOnClickListener {
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
            finish()
        }

        resetButton.setOnClickListener {
            resetSteps()
        }

        addExerciseButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_add_exercise)
            val exerciseType = dialog.findViewById<Spinner>(R.id.exercise_type)
            val durationInput = dialog.findViewById<EditText>(R.id.duration_input)
            val addButton = dialog.findViewById<Button>(R.id.btn_add_exercise)
            val cancelButton = dialog.findViewById<Button>(R.id.btn_cancel_exercise)
            val exercises = arrayOf("Running", "Cycling", "Weightlifting", "Walking", "Yoga", "Swimming", "HIIT")
            exerciseType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exercises)
            addButton.setOnClickListener {
                val duration = durationInput.text.toString().toFloatOrNull() ?: 0f
                val type = exerciseType.selectedItem.toString()
                if (duration > 0) {
                    val metValue = when (type) {
                        "Running" -> 10.0f
                        "Cycling" -> 8.0f
                        "Weightlifting" -> 6.0f
                        "Walking" -> 3.0f
                        "Yoga" -> 3.0f
                        "Swimming" -> 7.0f
                        "HIIT" -> 8.0f
                        else -> 0f
                    }
                    val calories = metValue * userWeight * (duration / 60) // MET × weight (kg) × hours
                    saveExerciseToFirebase(type, duration, calories.toFloat())
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Enter valid duration", Toast.LENGTH_SHORT).show()
                }
            }
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            val window = dialog.window
            val params = window?.attributes
            params?.width = (resources.displayMetrics.widthPixels * 0.9).toInt() // 90% of screen width
            window?.attributes = params
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 100)
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (accelerometer == null || gyroscope == null) {
            isSensorAvailable = false
            Toast.makeText(this, "Required sensors (Accelerometer or Gyroscope) unavailable!", Toast.LENGTH_LONG).show()
            Log.e("StepTracker", "Sensor unavailable: accelerometer=$accelerometer, gyroscope=$gyroscope")
        } else {
            isSensorAvailable = true
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepTracker", "Sensors registered: accelerometer and gyroscope available")
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when (event.sensor.type) {
                Sensor.TYPE_GYROSCOPE -> handleGyroscopeData(event.values)
                Sensor.TYPE_ACCELEROMETER -> handleAccelerometerData(event.values)
            }
        }
    }

    private fun handleAccelerometerData(values: FloatArray) {
        val rawMagnitude = sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2])
        val filteredMagnitude = lowPass(rawMagnitude.toDouble(), lastFilteredAccel)
        val currentTime = System.currentTimeMillis()
        val accelDiff = filteredMagnitude - lastFilteredAccel
        val isGyroValid = abs(lastGyroY) > gyroThreshold && abs(lastGyroY) < 1.5
        if (accelDiff > accelThreshold && isGyroValid && (currentTime - lastStepTime) > stepCooldown) {
            totalSteps++
            lastStepTime = currentTime
            updateUI(totalSteps.toInt())
            saveStepsToFirebase(totalSteps.toInt())
            Log.d("StepTracker", "Step detected: accelDiff=$accelDiff, gyroY=$lastGyroY, steps=$totalSteps")
        } else {
            Log.d("StepTracker", "No step: accelDiff=$accelDiff, gyroY=$lastGyroY, timeDiff=${currentTime - lastStepTime}")
        }
        lastFilteredAccel = filteredMagnitude
    }

    private fun handleGyroscopeData(values: FloatArray) {
        lastGyroY = values[1].toDouble()
        Log.d("StepTracker", "Gyroscope Y: $lastGyroY")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this use case
    }

    private fun lowPass(input: Double, output: Double): Double {
        return output + lowPassFilterFactor * (input - output)
    }

    private fun updateUI(steps: Int) {
        stepCountText.text = "$steps Steps"
        progressBar.progress = steps.coerceAtMost(10000)
        Log.d("StepTracker", "Updated UI with steps: $steps")
    }

    private fun resetSteps() {
        totalSteps = 0f
        previousSteps = 0f
        updateUI(0)
        saveStepsToFirebase(0)
    }

    private fun saveStepsToFirebase(steps: Int) {
        userId?.let {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val stepData = hashMapOf(
                "stepCount" to steps.toLong(),
                "date" to today,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("users").document(it)
                .collection("steps")
                .document(today)
                .set(stepData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Steps saved for $today!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save steps", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveExerciseToFirebase(type: String, duration: Float, calories: Float) {
        userId?.let {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val exerciseData = hashMapOf(
                "type" to type,
                "duration" to duration,
                "calories" to calories,
                "date" to today,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("users")
                .document(it)
                .collection("exercises")  // Main collection
                .document(today)          // Document for the day
                .collection("entries")    // Subcollection for multiple exercises
                .add(exerciseData)        // Add new exercise (auto-ID)
                .addOnSuccessListener {
                    Toast.makeText(this, "Exercise saved for $today!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save exercise", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun loadStepsFromFirebase() {
        userId?.let {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            db.collection("users").document(it)
                .collection("steps")
                .document(today)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val savedSteps = document.getLong("stepCount")?.toInt() ?: 0
                        totalSteps = savedSteps.toFloat()
                        previousSteps = 0f
                        updateUI(savedSteps)
                    } else {
                        totalSteps = 0f
                        previousSteps = 0f
                        updateUI(0)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load steps", Toast.LENGTH_SHORT).show()
                    totalSteps = 0f
                    previousSteps = 0f
                    updateUI(0)
                }
        }
    }

    override fun onDestroy() {
        if (isSensorAvailable) {
            sensorManager.unregisterListener(this)
        }
        super.onDestroy()
    }
}