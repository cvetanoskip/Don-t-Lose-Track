package com.example.caloriecounter.User

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.caloriecounter.R
import com.google.firebase.firestore.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.absoluteValue

class MenuActivity : AppCompatActivity() {
    private lateinit var calorieGoalInput: EditText
    private lateinit var backButton: ImageButton
    private lateinit var setButton: Button
    private lateinit var setRatioButton: Button
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var proteinTextView: TextView
    private lateinit var carbsTextView: TextView
    private lateinit var fatsTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var proteinPercentage:EditText
    private lateinit var carbPercentage:EditText
    private lateinit var fatPercentage:EditText
    private lateinit var proteinGram: TextView
    private lateinit var carbGram: TextView
    private lateinit var fatGram: TextView
    private lateinit var proteinProgressBar: ProgressBar
    private lateinit var carbProgressBar: ProgressBar
    private lateinit var fatProgressBar: ProgressBar
    private lateinit var proteinProgressLabel: TextView
    private lateinit var carbProgressLabel: TextView
    private lateinit var fatProgressLabel: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var calendarView: CalendarView
    private var selectedDate: String = ""

    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String // Current user's ID
    private var calorieGoal: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        val todayDate = java.util.Calendar.getInstance().let { cal ->
            String.format("%04d-%02d-%02d", cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH))
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)  // Replace with the layout for MenuActivity
         backButton = findViewById(R.id.back_button)
        calorieGoalInput = findViewById(R.id.calorie_goal_input)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        proteinPercentage=findViewById(R.id.proteinPercentage)
        carbPercentage=findViewById(R.id.carbPercentage)
        fatPercentage=findViewById(R.id.fatPercentage)
        proteinGram=findViewById(R.id.proteingram)
        carbGram=findViewById(R.id.carbgram)
        fatGram=findViewById(R.id.fatgram)
        proteinTextView = findViewById(R.id.protein_total)
        carbsTextView = findViewById(R.id.carbs_total)
        fatsTextView = findViewById(R.id.fats_total)
        caloriesTextView = findViewById(R.id.calories_total)
        fetchCalorieGoal();
        loadRatiosAndCalculateGrams()

            fetchAndCalculateMacros(todayDate)

        setButton = findViewById(R.id.set_button)
        setRatioButton = findViewById(R.id.set_ratio_button)
        proteinProgressBar = findViewById(R.id.proteinProgressBar)
        carbProgressBar = findViewById(R.id.carbProgressBar)
        fatProgressBar = findViewById(R.id.fatProgressBar)
        proteinProgressLabel = findViewById(R.id.proteinProgressLabel)
        carbProgressLabel = findViewById(R.id.carbProgressLabel)
        fatProgressLabel = findViewById(R.id.fatProgressLabel)
        ratingTextView = findViewById(R.id.ratingTextView)
        calendarView = findViewById(R.id.calendarView)


        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // month is 0-based in CalendarView, so add 1
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            fetchAndCalculateMacros(selectedDate)
        }


        setRatioButton.setOnClickListener{
            saveRatiosToFirebase()
        }
        setButton.setOnClickListener {
            saveCalorieGoal()  // Call the save function when the "Set" button is clicked
        }

        backButton.setOnClickListener {
            // Navigate back to UserDashboardActivity
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
            finish()  // Optional: Call finish() if you don't want to keep this activity in the back stack
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, UserDashboard::class.java)
        startActivity(intent)
        finish()  // Optional: Call finish() if you don't want to keep this activity in the back stack
    }

    private fun fetchAndCalculateMacros(selectedDate: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val macrosRef = firestore.collection("users")
                .document(userId)
                .collection("macros")
                .whereEqualTo("date", selectedDate) // <-- filter by date

            macrosRef.get().addOnSuccessListener { querySnapshot ->
                var totalProtein = 0.0
                var totalCarbs = 0.0
                var totalFats = 0.0
                var totalCalories = 0.0

                for (document in querySnapshot) {
                    totalProtein += document.getDouble("protein") ?: 0.0
                    totalCarbs += document.getDouble("carbs") ?: 0.0
                    totalFats += document.getDouble("fats") ?: 0.0
                    totalCalories += document.getDouble("calories") ?: 0.0
                }

                updateMacroProgress(totalProtein, totalCarbs, totalFats)

                proteinTextView.text = "Total Protein: ${totalProtein.toInt()}g"
                carbsTextView.text = "Total Carbs: ${totalCarbs.toInt()}g"
                fatsTextView.text = "Total Fats: ${totalFats.toInt()}g"
                caloriesTextView.text = "Total Calories: ${totalCalories.toInt()}"
            }.addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error fetching macros: ", exception)
                Toast.makeText(this, "Error fetching data.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCalorieGoal() {
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.contains("calorieGoal")) {
                    calorieGoal = document.getLong("calorieGoal")?.toInt() ?: 0
                    calorieGoalInput.hint=calorieGoal.toString();
                    if (selectedDate.isNotEmpty()) {
                        fetchAndCalculateMacros(selectedDate)
                    }else {
                        val todayDate = java.util.Calendar.getInstance().let { cal ->
                            String.format("%04d-%02d-%02d", cal.get(java.util.Calendar.YEAR),
                                cal.get(java.util.Calendar.MONTH) + 1,
                                cal.get(java.util.Calendar.DAY_OF_MONTH))
                        }
                        fetchAndCalculateMacros(todayDate)
                    }
                } else {
                    Toast.makeText(this, "Calorie goal not found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching calorie goal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateMacroProgress(totalProtein: Double, totalCarbs: Double, totalFats: Double) {
        val proteinRatio = proteinPercentage.text.toString().toDoubleOrNull() ?: 0.0
        val carbRatio = carbPercentage.text.toString().toDoubleOrNull() ?: 0.0
        val fatRatio = fatPercentage.text.toString().toDoubleOrNull() ?: 0.0

        if (calorieGoal <= 0) return

        val targetProteinGrams = (calorieGoal * proteinRatio / 100) / 4
        val targetCarbGrams = (calorieGoal * carbRatio / 100) / 4
        val targetFatGrams = (calorieGoal * fatRatio / 100) / 9

        // Calculate percentage (current intake / target)
        val proteinProgress = ((totalProtein / targetProteinGrams) * 100).toInt()
        val carbProgress = ((totalCarbs / targetCarbGrams) * 100).toInt()
        val fatProgress = ((totalFats / targetFatGrams) * 100).toInt()
        // Protein progress
        proteinProgressBar.max = maxOf(100, proteinProgress) // allow bar to grow beyond 100%
        proteinProgressBar.progress = proteinProgress
        proteinProgressBar.progressTintList = if (proteinProgress > 100)
            ColorStateList.valueOf(Color.RED)  // red if over goal
        else
            ColorStateList.valueOf(getColor(R.color.proteinColor))  // default color

// Carbs progress
        carbProgressBar.max = maxOf(100, carbProgress)
        carbProgressBar.progress = carbProgress
        carbProgressBar.progressTintList = if (carbProgress > 100)
            ColorStateList.valueOf(Color.RED)
        else
            ColorStateList.valueOf(getColor(R.color.carbColor))

// Fats progress
        fatProgressBar.max = maxOf(100, fatProgress)
        fatProgressBar.progress = fatProgress
        fatProgressBar.progressTintList = if (fatProgress > 100)
            ColorStateList.valueOf(Color.RED)
        else
            ColorStateList.valueOf(getColor(R.color.fatColor))

        proteinProgressBar.progress = proteinProgress
        carbProgressBar.progress = carbProgress
        fatProgressBar.progress = fatProgress

        // Optional: update label text
        proteinProgressLabel.text = "Protein: $proteinProgress%"
        carbProgressLabel.text = "Carbs: $carbProgress%"
        fatProgressLabel.text = "Fats: $fatProgress%"

        // Optional: update rating
        val overallRating = calculateRating(proteinProgress, carbProgress, fatProgress)
        ratingTextView.text = "Daily Rating: $overallRating"
    }
    private fun calculateRating(proteinProgress: Int, carbProgress: Int, fatProgress: Int): String {
        // Calculate difference from 100% (perfect)
        val avgDeviation = listOf(
            (proteinProgress - 100).absoluteValue,
            (carbProgress - 100).absoluteValue,
            (fatProgress - 100).absoluteValue
        ).average()

        return when {
            avgDeviation <= 5 -> "S"
            avgDeviation <= 10 -> "A"
            avgDeviation <= 25 -> "B"
            avgDeviation <= 35 -> "C"
            avgDeviation <= 50 -> "D"
            else -> "F"
        }
    }

    private fun saveRatiosToFirebase() {
        val proteinPercentStr = proteinPercentage.text.toString()
        val carbPercentStr = carbPercentage.text.toString()
        val fatPercentStr = fatPercentage.text.toString()

//        if (proteinPercentStr.isEmpty() || carbPercentStr.isEmpty() || fatPercentStr.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
//            return
//        }

        val proteinRatio = proteinPercentStr.toInt()
        val carbRatio = carbPercentStr.toInt()
        val fatRatio = fatPercentStr.toInt()

        // Validate percentages
        if (proteinRatio + carbRatio + fatRatio != 100) {
            Toast.makeText(this, "The ratios must add up to 100%!", Toast.LENGTH_SHORT).show()
            return
        }

        val ratioData = mapOf(
            "proteinRatio" to proteinRatio,
            "carbRatio" to carbRatio,
            "fatRatio" to fatRatio
        )

        db.collection("users").document(userId).update(ratioData)
            .addOnSuccessListener {
                Toast.makeText(this, "Ratios saved successfully!", Toast.LENGTH_SHORT).show()
                calculateMacros(proteinRatio, carbRatio, fatRatio) // Calculate grams after saving
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save ratios: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadRatiosAndCalculateGrams() {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.contains("proteinRatio") &&
                    document.contains("carbRatio") &&
                    document.contains("fatRatio") &&
                    document.contains("calorieGoal")) {

                    // Retrieve ratios and calorieGoal
                    val proteinRatio = document.getLong("proteinRatio")?.toInt() ?: 0
                    val carbRatio = document.getLong("carbRatio")?.toInt() ?: 0
                    val fatRatio = document.getLong("fatRatio")?.toInt() ?: 0
                    calorieGoal = document.getLong("calorieGoal")?.toInt() ?: 0

                    // Update EditTexts
                    proteinPercentage.setText(proteinRatio.toString())
                    carbPercentage.setText(carbRatio.toString())
                    fatPercentage.setText(fatRatio.toString())

                    // Calculate and display grams
                    calculateMacros(proteinRatio, carbRatio, fatRatio)
                } else {
                    Toast.makeText(this, "No saved ratios found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateMacros(proteinRatio: Int, carbRatio: Int, fatRatio: Int) {
        if (calorieGoal <= 0) {
            Toast.makeText(this, "Calorie goal is not set!", Toast.LENGTH_SHORT).show()
            return
        }

        val proteinGrams = (calorieGoal * proteinRatio / 100) / 4
        val carbGrams = (calorieGoal * carbRatio / 100) / 4
        val fatGrams = (calorieGoal * fatRatio / 100) / 9

        // Update TextViews
        proteinGram.text = "${proteinGrams}g"
        carbGram.text = "${carbGrams}g"
        fatGram.text = "${fatGrams}g"



        // Update progress bars
        updateMacroProgress(
            proteinGrams.toDouble(),
            carbGrams.toDouble(),
            fatGrams.toDouble()
        )
        Toast.makeText(this, "Macros calculated!", Toast.LENGTH_SHORT).show()
    }

    private fun saveCalorieGoal() {
        // Get the calorie goal input as string
        val calorieGoal = calorieGoalInput.text.toString().toDouble()
        val modified = true
        // Check if the calorie goal is not empty
        if (calorieGoal.toInt() !=0) {
            // Get the current user's UID
            val userId = firebaseAuth.currentUser?.uid

            if (userId != null) {
                // Create a map to store the calorie goal

                val userUpdates = hashMapOf(
                    "calorieGoal" to calorieGoal,
                    "modified" to modified
                )

                // Save the data in Firestore
                firestore.collection("users")
                    .document(userId)  // Use current user's UID
                    .update(userUpdates as Map<String, Any>)
                    .addOnSuccessListener {
                        // Success, inform the user
                        Toast.makeText(this, "Calorie goal updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Failure, inform the user
                        Toast.makeText(this, "Error updating calorie goal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // If the user is not authenticated, show an error
                Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
            }
        } else {
            // If the input field is empty, show an error
            Toast.makeText(this, "Please enter a calorie goal", Toast.LENGTH_SHORT).show()
        }
    }
}
