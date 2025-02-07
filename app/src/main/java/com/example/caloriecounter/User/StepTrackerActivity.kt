package com.example.caloriecounter.User

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
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
    private val accelThreshold = 1.0  // Detects acceleration change (lowered to avoid false steps)
    private val gyroThreshold = 0.75   // Detects foot tilt movement
    private val stepCooldown = 550L   // Prevents false double-counting (600ms per step)
    private val lowPassFilterFactor = 0.2  // Reduces noise from small movements

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
    }

    override fun onResume() {
        super.onResume()
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometer == null || gyroscope == null) {
            Toast.makeText(this, "No Accelerometer or Gyroscope!", Toast.LENGTH_SHORT).show()
        } else {
            isSensorAvailable = true
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> handleAccelerometerData(event.values)
                Sensor.TYPE_GYROSCOPE -> handleGyroscopeData(event.values)
            }
        }
    }

    private fun handleAccelerometerData(values: FloatArray) {
        val rawMagnitude = sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2])

        // Apply low-pass filter to smooth out sudden changes
        val filteredMagnitude = lowPass(rawMagnitude.toDouble(), lastFilteredAccel)

        val currentTime = System.currentTimeMillis()
        if (filteredMagnitude - lastFilteredAccel > accelThreshold && (currentTime - lastStepTime) > stepCooldown) {
            if (abs(lastGyroY) > gyroThreshold) {  // Ensure gyroscope confirms a step
                totalSteps++
                lastStepTime = currentTime
                updateUI(totalSteps.toInt())
                saveStepsToFirebase(totalSteps.toInt())
            }
        }
        lastFilteredAccel = filteredMagnitude
    }

    private fun handleGyroscopeData(values: FloatArray) {
        lastGyroY = values[1].toDouble() // Capture Y-axis rotation (helps confirm step movement)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this use case
    }

    private fun lowPass(input: Double, output: Double): Double {
        return output + lowPassFilterFactor * (input - output)
    }

    private fun updateUI(steps: Int) {
        stepCountText.text = "$steps Steps"
        progressBar.progress = steps.coerceAtMost(10000) // Limits max progress to 10,000
    }

    private fun resetSteps() {
        previousSteps += totalSteps
        totalSteps = 0f
        updateUI(0)
        saveStepsToFirebase(0)
    }

    private fun saveStepsToFirebase(steps: Int) {
        userId?.let {
            db.collection("users").document(it)
                .update("steps", steps)
                .addOnSuccessListener {
                    Toast.makeText(this, "Steps saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save steps", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadStepsFromFirebase() {
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("steps")) {
                        val savedSteps = document.getLong("steps")?.toInt() ?: 0
                        totalSteps = savedSteps.toFloat()
                        updateUI(savedSteps)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load steps", Toast.LENGTH_SHORT).show()
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
