package com.example.caloriecounter.User

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriecounter.Databases.SessionManager
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_ACTIVITY
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_DATE
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_EMAIL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_FULLNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GENDER
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_HEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PASSWORD
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PHONENUMBER
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PHONEVERIFICATION
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_UNIT_PREFERENCE
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_USERNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEEK_GOAL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import com.example.caloriecounter.LoginSignup.ForgetPassword
import com.example.caloriecounter.LoginSignup.MakeSelection
import com.example.caloriecounter.MacrosAdapter
import com.example.caloriecounter.MainActivity
import com.example.caloriecounter.R
import com.example.caloriecounter.User.PopUps.AddMacros
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class UserDashboard : AppCompatActivity() {
    lateinit var add: Button
    lateinit var calorie: TextView
    lateinit var sessionManager: SessionManager
    lateinit var menuBtn:ImageView
    private lateinit var signOutButton: Button
    private lateinit var mAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var progressBar: ProgressBar
    private lateinit var burnedCaloriesTextView: TextView
    private lateinit var burnedCaloriesSection: LinearLayout
    private var userWeight: Double = 0.0
    private var isMetric: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)
        fetchMacros()
        signOutButton = findViewById(R.id.sign_out_button)
        mAuth = FirebaseAuth.getInstance()
        // Set an OnClickListener for the Sign Out button
        signOutButton.setOnClickListener {
            signOutUser()
        }
        var baseCalorieGoal: Float = 0f
        calorie=findViewById(R.id.goal_value)
         menuBtn=findViewById(R.id.menu_icon_button)
        menuBtn.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)  // Start MenuActivity
        }
        burnedCaloriesTextView = findViewById(R.id.burned_calories_value)
        burnedCaloriesSection = findViewById(R.id.burned_calories_section)

        burnedCaloriesSection.setOnClickListener {
            val intent = Intent(this, StepTrackerActivity::class.java)
            startActivity(intent)
        }


        fetchUserData()
        fetchAndUpdateMacroList()
        fun fetchAndDisplayUserData(userId: String) {
            val docRef = db.collection("users").document(userId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val height = document.getString("height")?.toFloat() ?: 0f
                        val weight = document.getString("weight")?.toFloat() ?: 0f
                        val activity = document.getString("activity") ?: ""
                        val goal = document.getString("goal") ?: ""
                        val unitPreference = document.getString("unitPreference") ?: "metric"
                        val age = document.getString("age")?.toInt() ?: 18
                        val gender = document.getString("gender") ?: ""
                        val speed = document.getString("speed") ?: ""
                        val modified = document.getBoolean("modified") ?: false
                        val calorieGoal = document.getDouble("calorieGoal")?.toFloat() ?: 0f
                        Log.d("Firestore", "User data successfully added! $height,$weight")
                        if(!modified) {

                            baseCalorieGoal = calculateBaseCalorieGoal(
                                height, weight, activity, goal, unitPreference, age, gender, speed
                            )
                            updateDashboard(baseCalorieGoal)
                        }
                        else if(modified)
                        {

                            updateDashboard(calorieGoal)
                        }
                    } else {
                        Log.w("Firestore", "Document does not exist.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching user data", exception)
                }
        }

        sessionManager = SessionManager(this)
        val userDetails = sessionManager.getUsersDetailFromSession()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        progressBar = findViewById(R.id.progress_calories)
        if (currentUser != null) {

            val docRef = db.collection("users").document(currentUser.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val height = document.getString("height")?.toFloat() ?: 0f
                        val weight = document.getString("weight")?.toFloat() ?: 0f
                        val activity = document.getString("activity") ?: ""
                        val goal = document.getString("goal") ?: ""
                        val unitPreference = document.getString("unitPreference") ?: "metric"
                        val age = document.getString("age")?.toInt() ?: 18
                        val gender = document.getString("gender") ?: ""
                        val speed = document.getString("speed") ?: ""
                        val modified = document.getBoolean("modified") ?: false
                        val calorieGoal = document.get("calorieGoal")?.let {
                            Log.d("Firestore", "calorieGoal type: ${it::class.java.simpleName}")
                            when (it) {
                                is Double -> it.toFloat()
                                is Long -> it.toFloat()
                                is Int -> it.toFloat()
                                else -> 0f
                            }
                        } ?: 0f

                        Log.d("Firestore", "calorieGoal: $calorieGoal")
                        fetchAndDisplayUserData(currentUser.uid)
                        //   Calculate base calorie goal using user's data
                      //  Log.w("Debug", "Calories Reseted. $calorieGoal")
                        if (!modified) {
                            // Calculate base calorie goal if 'modified' is false
                            val baseCalorieGoal = calculateBaseCalorieGoal(
                                height,
                                weight,
                                activity,
                                goal,
                                unitPreference,
                                age,
                                gender,
                                speed
                            )

                            // Safely update calorie text and save it to Firestore
                            calorie.text = baseCalorieGoal.toInt().toString()
                            saveCalorieGoalToFirestore(
                                currentUser.uid,
                                baseCalorieGoal
                            )
                            updateDashboard(baseCalorieGoal)
                        } else {
                            // Use saved calorieGoal from Firestore if 'modified' is true
                            calorie.text = calorieGoal.toInt().toString()
                            updateDashboard(calorieGoal)
                        }

                        // Fetch consumed calories

                        fetchConsumedCalories(currentUser.uid)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching user data", exception)
                }
            val resetCalorieButton: Button = findViewById(R.id.ResetCalorie)

            resetCalorieButton.setOnClickListener {
                // Get the user's ID (assuming you have the current user's UID available)
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null) {
                    val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
                    val macrosRef = userRef.collection("macros")

                    // Get all documents in the macros subcollection and delete them
                    macrosRef.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            // For each document in the macros subcollection, delete it
                            macrosRef.document(document.id).delete()
                                .addOnSuccessListener {
                                    Log.d("Firebase", "Macro data deleted successfully!")
                                    // Optionally, show a success message like Snackbar
                                    fetchAndUpdateMacroList()
                                    val docRef = db.collection("users").document(currentUser.uid)
                                    docRef.get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                val height = document.getString("height")?.toFloat() ?: 0f
                                                val weight = document.getString("weight")?.toFloat() ?: 0f
                                                val activity = document.getString("activity") ?: ""
                                                val goal = document.getString("goal") ?: ""
                                                val unitPreference = document.getString("unitPreference") ?: "metric"
                                                val age = document.getString("age")?.toInt() ?: 18
                                                val gender = document.getString("gender") ?: ""
                                                val speed = document.getString("speed") ?: ""
                                                fetchAndDisplayUserData(currentUser.uid)
                                                // Calculate base calorie goal using user's data



                                                calorie.text=document.getDouble("calorieGoal").toString()
                                                saveCalorieGoalToFirestore(currentUser.uid,calorie.text.toString().toFloat())
                                                updateDashboard(calorie.text.toString().toFloat())
                                                fetchConsumedCalories(currentUser.uid)
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("Firestore", "Error fetching user data", exception)
                                        }

                                    calorie.text=baseCalorieGoal.toString()
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Firebase", "Error deleting macro data", exception)
                                }
                        }
                    }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Error retrieving macros subcollection", exception)
                        }
                }
            }
        }


        add = findViewById(R.id.AddMacro)
        add.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_add_macros)

            val proteinInput = dialog.findViewById<EditText>(R.id.protein_input)
            val carbsInput = dialog.findViewById<EditText>(R.id.carbs_input)
            val fatsInput = dialog.findViewById<EditText>(R.id.fats_input)
            val servingSizeInput = dialog.findViewById<EditText>(R.id.serving_size_input)
            val addMacrosButton = dialog.findViewById<Button>(R.id.btn_add_macros)

            addMacrosButton.setOnClickListener {
                val protein = proteinInput.text.toString().toFloatOrNull() ?: 0f
                val carbs = carbsInput.text.toString().toFloatOrNull() ?: 0f
                val fats = fatsInput.text.toString().toFloatOrNull() ?: 0f
                val servingSize = servingSizeInput.text.toString().toFloatOrNull() ?: 1f
                // Calculate total calories
                val adjustedProtein = protein * servingSize
                val adjustedCarbs = carbs * servingSize
                val adjustedFats = fats * servingSize

                // Calculate total calories
                val totalCalories = (adjustedProtein * 4) + (adjustedCarbs * 4) + (adjustedFats * 9)

                // Save to Firebase
                addMacrosToFirebase(adjustedProtein, adjustedCarbs, adjustedFats, totalCalories)
                // Update dashboard with the consumed calories
                updateConsumedCalories(totalCalories)
                dialog.dismiss()
            }

            dialog.show()
        }
    }
    private fun fetchUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = userId?.let { FirebaseFirestore.getInstance().collection("users").document(it) }

        if (userRef != null) {
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val weight = document.getString("weight")?.toDouble() ?: 70.0 // Default 70kg
                    val unitPreference = document.getString("unitPreference") ?: "metric"
                    val firestoreSteps = if (document.contains("steps")) {
                        document.getLong("steps")?.toInt() ?: 0
                    } else {
                        0
                    }


                    userWeight = weight
                    isMetric = unitPreference == "metric"

                    calculateBurnedCalories(firestoreSteps)
                }
            }
        }
    }

    private fun calculateBurnedCalories(stepCount: Int) {
        val burnedCalories = if (isMetric) {
            stepCount * userWeight * 0.000602 // Approx kcal burned per step per kg
        } else {
            stepCount * (userWeight / 2.205) * 0.000602 // Convert pounds to kg
        }

        burnedCaloriesTextView.text = String.format("%.2f kcal", burnedCalories)

    }
    fun saveCalorieGoalToFirestore(userId: String, calorieGoal: Float) {
        val userRef = db.collection("users").document(userId)

        val data = hashMapOf(
            "calorieGoal" to calorieGoal,

        ) as MutableMap<String, Any>


        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .update(data)
                .addOnSuccessListener {
                    Log.d("Firebase", "Macro data added successfully!")
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding macro data", exception)
                }
        }
    }
    private fun signOutUser() {
        // Firebase sign out
        mAuth.signOut()

        // Redirect to login or main activity after sign out
        val intent = Intent(this, MainActivity::class.java) // or the appropriate login activity
        startActivity(intent)
        finish() // Optional: finish current activity to prevent going back to it
    }
    private fun fetchConsumedCalories(userId: String) {
        var consumedCalories = 0f

        // Access the 'macros' subcollection for the current user
        val macroRef = db.collection("users").document(userId).collection("macros")

        macroRef.get().addOnSuccessListener { result ->
            for (document in result) {
                val protein = document.getDouble("protein")?.toFloat() ?: 0f
                val carbs = document.getDouble("carbs")?.toFloat() ?: 0f
                val fats = document.getDouble("fats")?.toFloat() ?: 0f

                // Calculate total calories for each macro entry
                val totalCalories = (protein * 4) + (carbs * 4) + (fats * 9)
                consumedCalories += totalCalories
            }

            // Call updateConsumedCalories with the total consumed calories from the database
            updateConsumedCalories(consumedCalories)
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error fetching macros", exception)
        }
    }
    fun addMacrosToFirebase(protein: Float, carbs: Float, fats: Float, totalCalories: Float) {
        val macroId = UUID.randomUUID().toString()
        val macroData = hashMapOf(
            "macroId" to macroId,
            "protein" to protein,
            "carbs" to carbs,
            "fats" to fats,
            "calories" to totalCalories,
            "timestamp" to System.currentTimeMillis()
        )

        currentUser?.let { user ->
            // Generate a unique ID using UUID


            db.collection("users").document(user.uid)
                .collection("macros")
                .document(macroId) // Use the generated UUID as the document ID
                .set(macroData) // Use set() to create the document with the specified ID
                .addOnSuccessListener {
                    Log.d("Firebase", "Macro data added successfully with ID: $macroId")
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding macro data", exception)
                }
        }
    }


    fun updateConsumedCalories(newCalories: Float) {
        val caloriesRemainingText = findViewById<TextView>(R.id.calories_remaining_text)
        val consumedCaloriesValue = findViewById<TextView>(R.id.consumed_calories_value)

        // Fetch existing calories and update UI once the data is fetched
        getExistingCalories { existingCalories ->
            // Add the new calories to the existing total
            val updatedCaloriesConsumed = existingCalories

            // Get the base calorie goal from the TextView
            val baseCalorieGoal = calorie.text.toString().toFloat()
            val burnedCaloriesText = burnedCaloriesTextView.text.toString().replace(" kcal", "")
            val burnedCalories = burnedCaloriesText.toFloatOrNull() ?: 0f
            // Calculate remaining calories
            val updatedCaloriesRemaining = baseCalorieGoal - updatedCaloriesConsumed + burnedCalories

            // Update the TextViews with the updated values
            consumedCaloriesValue.text = "$updatedCaloriesConsumed kcal"
            caloriesRemainingText.text = "$updatedCaloriesRemaining kcal"
            val progressPercentage = calculateProgressPercentage(updatedCaloriesConsumed, baseCalorieGoal)

            // Update the ProgressBar with the calculated percentage
            progressBar.progress = progressPercentage
            // Optionally, update the RecyclerView with the new list of macros
            fetchAndUpdateMacroList()
        }
    }

    fun getExistingCalories(onComplete: (Float) -> Unit) {
        var totalCalories = 0f

        // Fetch all macros from Firebase asynchronously
        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .collection("macros")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val protein = document.getDouble("protein")?.toFloat() ?: 0f
                        val carbs = document.getDouble("carbs")?.toFloat() ?: 0f
                        val fats = document.getDouble("fats")?.toFloat() ?: 0f

                        // Calculate calories from protein, carbs, and fats
                        totalCalories += (protein * 4) + (carbs * 4) + (fats * 9)
                    }

                    // Once the data is fetched, return the totalCalories using the callback
                    onComplete(totalCalories)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching macros", exception)
                    onComplete(totalCalories) // In case of failure, return 0 as default
                }
        }
    }



    fun fetchAndUpdateMacroList() {
        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .collection("macros")
                .orderBy("timestamp") // Order by the timestamp field
                .get()
                .addOnSuccessListener { documents ->
                    val macrosList = documents.map { doc ->
                        mapOf(
                            "protein" to (doc.getDouble("protein") ?: 0.0),
                            "carbs" to (doc.getDouble("carbs") ?: 0.0),
                            "fats" to (doc.getDouble("fats") ?: 0.0),
                            "calories" to (doc.getDouble("calories") ?: 0.0),
                            "timestamp" to (doc.getLong("timestamp") ?: 0L) // Include for debugging if needed
                        )
                    }

                    // Update RecyclerView with the new macros data
                    val recyclerView = findViewById<RecyclerView>(R.id.macros_list)
                    recyclerView.layoutManager = LinearLayoutManager(this)

                    recyclerView.adapter = MacrosAdapter(macrosList.toMutableList()) // Convert to mutable if needed
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching macros", exception)
                }
        }
    }
    fun calculateProgressPercentage(consumedCalories: Float, baseCalorieGoal: Float): Int {
        return if (baseCalorieGoal > 0) {
            ((consumedCalories / baseCalorieGoal) * 100).toInt()
        } else {
            0 // Avoid division by zero, return 0% progress
        }
    }



    private fun fetchMacros() {
        val userId = currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("macros")
            .get()
            .addOnSuccessListener { documents ->
                var totalProtein = 0
                var totalCarbs = 0
                var totalFats = 0

                for (document in documents) {
                    totalProtein += document.getLong("protein")?.toInt() ?: 0
                    totalCarbs += document.getLong("carbs")?.toInt() ?: 0
                    totalFats += document.getLong("fats")?.toInt() ?: 0
                }

                updateDashboardWithMacros(totalProtein, totalCarbs, totalFats)

            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching macros", exception)
            }
    }

    fun updateDashboardWithMacros(protein: Int, carbs: Int, fats: Int) {
        val totalCaloriesFromMacros = (protein * 4) + (carbs * 4) + (fats * 9)
        val consumedCaloriesValue = findViewById<TextView>(R.id.consumed_calories_value)

        // Update consumed calories on the dashboard
        consumedCaloriesValue.text = "$totalCaloriesFromMacros kcal"
    }



    fun calculateBaseCalorieGoal(height: Float, weight: Float, activity: String, goal: String, unitPreference: String, age: Int, gender: String,speed: String): Float {
        //val heightInCm = if (unitPreference == "imperial") height * 2.54 else height
        val heightInCm =  height
        val weightInKg = if (unitPreference == "imperial") weight * 0.453592 else weight


        var bmr = 0f
        if (gender == "Male") {
            bmr = (10 * weightInKg.toInt() + 6.25 * heightInCm.toFloat() - 5 * age + 5).toFloat()
        } else if (gender == "Female") {
            bmr = (10 * weightInKg.toFloat() + 6.25 * heightInCm.toFloat() - 5 * age - 161).toFloat()
        }

        val activityMultiplier = when (activity) {
            "sedentary" -> 1.2f
            "light" -> 1.375f
            "moderate" -> 1.55f
            "very active" -> 1.725f

            else -> 1.2f
        }

        val totalCalories = bmr * activityMultiplier
        val calorieAdjustment = when (speed) {
            "slow" -> 250  // Calorie deficit for 0.25 kg/week
            "recommended" -> 500  // Calorie deficit for 0.5 kg/week
            "moderate" -> 750  // Calorie deficit for 0.75 kg/week
            "fast" -> 1000  // Calorie deficit for 1 kg/week
            else -> 0  // Default case for no speed set
        }
        return when (goal) {
            "lose" -> totalCalories - calorieAdjustment
            "gain" -> totalCalories + calorieAdjustment
            else -> totalCalories
        }
    }

    fun updateDashboard(baseCalorieGoal: Float) {
        val caloriesRemaining = baseCalorieGoal  // Assuming the user hasn't consumed any calories yet.

        val caloriesConsumed = 0f  // Placeholder for consumed calories.

        val caloriesRemainingText = findViewById<TextView>(R.id.calories_remaining_text)
        val consumedCaloriesValue = findViewById<TextView>(R.id.consumed_calories_value)

        caloriesRemainingText.text = "$caloriesRemaining kcal"
        consumedCaloriesValue.text = "$caloriesConsumed kcal"
    }
}
