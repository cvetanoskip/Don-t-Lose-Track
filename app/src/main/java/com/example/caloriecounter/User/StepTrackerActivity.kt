package com.example.caloriecounter.User

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriecounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs
import kotlin.math.sqrt

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

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Step Detection Variables
    private var lastStepTime: Long = 0
    private var lastFilteredAccel = 0.0
    private var lastGyroY = 0.0


    // Sensitivity Thresholds
    private val accelThreshold = 0.5  // Lowered for linear walking sensitivity
    private val gyroThreshold = 0.3   // Increased to filter out minor tilts
    private val stepCooldown = 250L   // Reduced for faster walking pace
    private val lowPassFilterFactor = 0.4  // Increased to smooth out tilt noise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_tracker)

        backButton = findViewById(R.id.back_button)
        stepCountText = findViewById(R.id.stepCountText)
        progressBar = findViewById(R.id.circularProgressBar)
        resetButton = findViewById(R.id.resetButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Load previous steps from Firebase
        loadStepsFromFirebase()

        backButton.setOnClickListener {
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
            finish()
        }

        resetButton.setOnClickListener {
            resetSteps()
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

        // Calculate acceleration difference and validate with gyroscope
        val accelDiff = filteredMagnitude - lastFilteredAccel
        val isGyroValid = abs(lastGyroY) > gyroThreshold && abs(lastGyroY) < 1.5  // Ensure step-like rotation
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
    }

    private fun resetSteps() {
        totalSteps = 0f
        previousSteps = 0f
        updateUI(0)
        saveStepsToFirebase(0)
    }

    private fun saveStepsToFirebase(steps: Int) {
        userId?.let {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val stepData = hashMapOf(
                "stepCount" to steps.toLong(),
                "date" to today,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("users").document(it)
                .collection("steps")
                .document(today) // Use date as document ID
                .set(stepData)
                .addOnSuccessListener {
                     }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save steps", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadStepsFromFirebase() {
        userId?.let {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            db.collection("users").document(it)
                .collection("steps")
                .document(today)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val savedSteps = document.getLong("stepCount")?.toInt() ?: 0
                        totalSteps = savedSteps.toFloat()
                        previousSteps = 0f // Reset previousSteps on load
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
