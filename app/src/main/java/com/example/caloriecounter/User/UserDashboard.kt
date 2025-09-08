package com.example.caloriecounter.User

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.lifecycle.lifecycleScope
import com.example.caloriecounter.User.PopUps.QuoteProvider
import kotlinx.coroutines.launch
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

        loadWaterFromFirebase()
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

        val today = Calendar.getInstance()
        val todayDate = String.format("%04d-%02d-%02d",
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH) + 1,
            today.get(Calendar.DAY_OF_MONTH)
        )
        fetchMacrosByDate(todayDate)
        findViewById<Button>(R.id.AddWater).setOnClickListener {
            showAddWaterDialog()
        }



//        fun fetchAndDisplayUserData(userId: String) {
//            val docRef = db.collection("users").document(userId)
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    if (document.exists()) {
//                        val height = document.getString("height")?.toFloat() ?: 0f
//                        val weight = document.getString("weight")?.toFloat() ?: 0f
//                        val activity = document.getString("activity") ?: ""
//                        val goal = document.getString("goal") ?: ""
//                        val unitPreference = document.getString("unitPreference") ?: "metric"
//                        val age = document.getString("age")?.toInt() ?: 18
//                        val gender = document.getString("gender") ?: ""
//                        val speed = document.getString("speed") ?: ""
//                        val modified = document.getBoolean("modified") ?: false
//                        val calorieGoal = document.getDouble("calorieGoal")?.toFloat() ?: 0f
//                        Log.d("Firestore", "User data successfully added! $height,$weight")
//                        if(!modified) {
//
//                            baseCalorieGoal = calculateBaseCalorieGoal(
//                                height, weight, activity, goal, unitPreference, age, gender, speed
//                            )
//                            updateDashboard(baseCalorieGoal)
//                        }
//                        else if(modified)
//                        {
//
//                            updateDashboard(calorieGoal)
//                        }
//                    } else {
//                        Log.w("Firestore", "Document does not exist.")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("Firestore", "Error fetching user data", exception)
//                }
//        }

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

        }
        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Format the date as YYYY-MM-DD
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            // Fetch macros for this date
            fetchMacrosByDate(selectedDate)
        }

        lifecycleScope.launch {

            try {
                // Use hardcoded gym quotes
                val quote = QuoteProvider.getRandomGymQuote()

                val dialogView = layoutInflater.inflate(R.layout.dialog_quote, null)
                val quoteTextView = dialogView.findViewById<TextView>(R.id.quoteText)
                val authorTextView = dialogView.findViewById<TextView>(R.id.authorText)
                val closeButton = dialogView.findViewById<Button>(R.id.closeButton)

                quoteTextView.text = "\"${quote.q}\""
                authorTextView.text = "- ${quote.a}"

                val dialog = AlertDialog.Builder(this@UserDashboard)
                    .setView(dialogView)
                    .create()

                closeButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            } catch (e: Exception) {
                e.printStackTrace()
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

    private fun showAddWaterDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_water, null)
        builder.setView(dialogView)

        val input = dialogView.findViewById<EditText>(R.id.input_water)
        val btnAdd = dialogView.findViewById<Button>(R.id.btn_add_water)

        val dialog = builder.create()

        btnAdd.setOnClickListener {
            val waterMl = input.text.toString().toIntOrNull()
            if (waterMl != null && waterMl > 0) {
                addWaterToFirebase(waterMl)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
    private fun addWaterToFirebase(amount: Int) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val waterRef = db.collection("users").document(user.uid)
            .collection("water")
            .document(today)
        waterRef.get().addOnSuccessListener { doc ->
            val currentWater = doc.getLong("waterAmount")?.toInt() ?: 0
            val newTotal = currentWater + amount

            waterRef.set(mapOf("waterAmount" to newTotal, "date" to today))
                .addOnSuccessListener {
                    Log.d("UserDashboard", "Water added for $today: $newTotal ml")
                    updateWaterUI(newTotal)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save water", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch water data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadWaterFromFirebase() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        db.collection("users").document(user.uid)
            .collection("water")
            .document(today)
            .get()
            .addOnSuccessListener { doc ->
                val water = doc.getLong("waterAmount")?.toInt() ?: 0
                Log.d("UserDashboard", "Fetched water for $today: $water ml")
                updateWaterUI(water)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching water for $today", it)
                updateWaterUI(0)
            }
    }

    private fun updateWaterUI(water: Int) {
        val progressBar = findViewById<ProgressBar>(R.id.progress_water)
        val waterText = findViewById<TextView>(R.id.water_remaining_text)

        progressBar.progress = water
        waterText.text = "$water / 3000 ml"
        Log.d("UserDashboard", "Updated water UI: $water ml")
        if (water >= progressBar.max) {
            showCongratulationsDialog()
        }
    }
    private fun showCongratulationsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Congratulations! 🎉")
            .setMessage("You reached your daily goal of 3000ml of water!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun fetchUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = userId?.let { FirebaseFirestore.getInstance().collection("users").document(it) }
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (userRef != null) {
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val weight = document.getString("weight")?.toDouble() ?: 70.0
                    val unitPreference = document.getString("unitPreference") ?: "metric"
                    userWeight = weight
                    isMetric = unitPreference == "metric"
                    Log.d("UserDashboard", "Fetched user data: weight=$weight, unitPreference=$unitPreference")

                    // Fetch today's steps from the steps subcollection
                    db.collection("users").document(userId)
                        .collection("steps")
                        .document(today)
                        .get()
                        .addOnSuccessListener { stepDoc ->
                            val firestoreSteps = if (stepDoc.exists()) {
                                stepDoc.getLong("stepCount")?.toInt() ?: 0
                            } else {
                                0
                            }
                            Log.d("UserDashboard", "Fetched steps for $today: $firestoreSteps")
                            calculateBurnedCalories(firestoreSteps, today)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Error fetching steps for $today", exception)
                            calculateBurnedCalories(0, today)
                        }
                } else {
                    Log.w("UserDashboard", "User document does not exist")
                    calculateBurnedCalories(0, today)
                }
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching user data", exception)
                calculateBurnedCalories(0, today)
            }
        } else {
            Log.e("UserDashboard", "No user ID available")
            calculateBurnedCalories(0, today)
        }
    }

    private fun calculateBurnedCalories(stepCount: Int, selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) {
        val burnedCalories = if (isMetric) {
            stepCount * userWeight * 0.000602
        } else {
            stepCount * (userWeight / 2.205) * 0.000602
        }
        burnedCaloriesTextView.text = String.format("%.2f kcal", burnedCalories)
        Log.d("UserDashboard", "Calculated burned calories: $burnedCalories for steps: $stepCount, weight: $userWeight, isMetric: $isMetric, date: $selectedDate")
        // Update dashboard to reflect burned calories for the selected date
        updateDashboard(calorie.text.toString().toFloatOrNull() ?: 0f, selectedDate)
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
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db.collection("users").document(userId)
            .collection("macros")
            .whereEqualTo("date", today)
            .get()
            .addOnSuccessListener { documents ->
                val totalCalories = documents.sumOf { doc ->
                    (doc.getDouble("calories") ?: 0.0)
                }.toFloat()
                Log.d("UserDashboard", "Fetched consumed calories for $today: $totalCalories, ${documents.size()} entries")
                updateConsumedCalories(totalCalories)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching macros for $today", exception)
                updateConsumedCalories(0f)
            }
    }

    fun addMacrosToFirebase(protein: Float, carbs: Float, fats: Float, totalCalories: Float) {
        val macroId = UUID.randomUUID().toString()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val macroData = hashMapOf(
            "protein" to protein,
            "carbs" to carbs,
            "fats" to fats,
            "calories" to totalCalories,
            "timestamp" to System.currentTimeMillis(),
            "date" to today
        )

        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .collection("macros")
                .document(macroId)
                .set(macroData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Macro data added for $today with ID: $macroId")
                    fetchMacrosByDate(today) // Refresh the list and calories for today
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding macro data", exception)
                }
        }
    }



    fun fetchMacrosByDate(selectedDate: String) {
        currentUser?.let { user ->
            Log.d("UserDashboard", "Fetching macros for date: $selectedDate")
            db.collection("users").document(user.uid)
                .collection("macros")
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener { documents ->
                    val macrosList = documents.map { doc ->
                        val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        mapOf(
                            "protein" to (doc.getDouble("protein") ?: 0.0),
                            "carbs" to (doc.getDouble("carbs") ?: 0.0),
                            "fats" to (doc.getDouble("fats") ?: 0.0),
                            "calories" to (doc.getDouble("calories") ?: 0.0),
                            "timestamp" to (doc.getLong("timestamp") ?: 0L),
                            "date" to (doc.getString("date") ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp)))
                        )
                    }.sortedBy { it["timestamp"] as Long }

                    val recyclerView = findViewById<RecyclerView>(R.id.macros_list)
                    recyclerView.layoutManager = LinearLayoutManager(this)

                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = MacrosAdapter(macrosList.toMutableList()) { docId, position ->
                            deleteMacro(docId, position)
                        }
                    } else {
                        (recyclerView.adapter as MacrosAdapter).updateData(macrosList.toMutableList())
                    }

                    // Calculate and update consumed calories for the selected date
                    val totalCalories = macrosList.sumOf { it["calories"] as Double }.toFloat()
                    Log.d("UserDashboard", "Fetched macros for $selectedDate: $totalCalories calories, ${macrosList.size} entries")
                    updateConsumedCalories(totalCalories)

                    // Fetch steps for the selected date and update burned calories
                    db.collection("users").document(user.uid)
                        .collection("steps")
                        .document(selectedDate)
                        .get()
                        .addOnSuccessListener { stepDoc ->
                            val stepCount = if (stepDoc.exists()) {
                                stepDoc.getLong("stepCount")?.toInt() ?: 0
                            } else {
                                0
                            }
                            Log.d("UserDashboard", "Fetched steps for $selectedDate: $stepCount")
                            calculateBurnedCalories(stepCount, selectedDate)

                            // Fetch water intake for the selected date
                            db.collection("users").document(user.uid)
                                .collection("water")
                                .document(selectedDate)
                                .get()
                                .addOnSuccessListener { waterDoc ->
                                    val waterAmount = if (waterDoc.exists()) {
                                        waterDoc.getLong("waterAmount")?.toInt() ?: 0
                                    } else {
                                        0
                                    }
                                    Log.d("UserDashboard", "Fetched water for $selectedDate: $waterAmount ml")
                                    updateWaterUI(waterAmount)
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Firebase", "Error fetching water for $selectedDate", exception)
                                    updateWaterUI(0)
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Error fetching steps for $selectedDate", exception)
                            calculateBurnedCalories(0, selectedDate)
                            updateWaterUI(0)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching macros for $selectedDate", exception)
                    updateConsumedCalories(0f)
                    calculateBurnedCalories(0, selectedDate)
                    updateWaterUI(0)
                }
        } ?: run {
            Log.e("UserDashboard", "No current user, setting calories, steps, and water to 0")
            updateConsumedCalories(0f)
            calculateBurnedCalories(0, selectedDate)
            updateWaterUI(0)
        }
    }




    fun updateConsumedCalories(newCalories: Float) {
        val caloriesRemainingText = findViewById<TextView>(R.id.calories_remaining_text)
        val consumedCaloriesValue = findViewById<TextView>(R.id.consumed_calories_value)
        val baseCalorieGoal = calorie.text.toString().toFloatOrNull() ?: 0f
        val burnedCaloriesText = burnedCaloriesTextView.text.toString().replace(" kcal", "")
        val burnedCalories = burnedCaloriesText.toFloatOrNull() ?: 0f

        // Update consumed and remaining calories
        consumedCaloriesValue.text = String.format("%.0f kcal", newCalories)
        val updatedCaloriesRemaining = baseCalorieGoal - newCalories + burnedCalories
        caloriesRemainingText.text = String.format("%.0f kcal", updatedCaloriesRemaining)

        // Update progress bar
        val progressPercentage = calculateProgressPercentage(newCalories, baseCalorieGoal)
        progressBar.progress = progressPercentage

        Log.d("UserDashboard", "Updating consumed: newCalories=$newCalories, baseCalorieGoal=$baseCalorieGoal, burnedCalories=$burnedCalories, remaining=$updatedCaloriesRemaining, progress=$progressPercentage")

        // Reset progress bar color before checking condition
        if (newCalories > baseCalorieGoal && baseCalorieGoal > 0) {
            progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
            showExceededCaloriesMessage()
        }
    }
    private fun showExceededCaloriesMessage() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Calorie Limit Exceeded")
            .setMessage("You exceeded your daily calorie goal!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    private fun getExistingCalories(selectedDate: String, onComplete: (Float) -> Unit) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .collection("macros")
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener { documents ->
                    val totalCalories = documents.sumOf { doc ->
                        (doc.getDouble("calories") ?: 0.0)
                    }.toFloat()
                    Log.d("UserDashboard", "Fetched existing calories for $selectedDate: $totalCalories, ${documents.size()} entries")
                    onComplete(totalCalories)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching macros for $selectedDate", exception)
                    onComplete(0f)
                }
        } ?: onComplete(0f)
    }



    //    fun fetchAndUpdateMacroList(selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) {
//        currentUser?.let { user ->
//            db.collection("users").document(user.uid)
//                .collection("macros")
//                .whereEqualTo("date", selectedDate) // Filter by selected date
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener { documents ->
//                    val macrosList = documents.map { doc ->
//                        val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
//                        mapOf(
//                            "id" to doc.id,
//                            "protein" to (doc.getDouble("protein") ?: 0.0),
//                            "carbs" to (doc.getDouble("carbs") ?: 0.0),
//                            "fats" to (doc.getDouble("fats") ?: 0.0),
//                            "calories" to (doc.getDouble("calories") ?: 0.0),
//                            "date" to (doc.getString("date")
//                                ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp)))
//                        )
//                    }
//
//                    val recyclerView = findViewById<RecyclerView>(R.id.macros_list)
//                    recyclerView.layoutManager = LinearLayoutManager(this)
//                    recyclerView.adapter = MacrosAdapter(macrosList.toMutableList()) { docId, position ->
//                        deleteMacro(docId, position)
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("Firebase", "Error fetching macros", exception)
//                }
//        }
//    }
    private fun deleteMacro(docId: String, position: Int) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .collection("macros")
                .document(docId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Macro deleted successfully")
                    val adapter = findViewById<RecyclerView>(R.id.macros_list).adapter as MacrosAdapter
                    adapter.macrosList.removeAt(position)
                    adapter.notifyItemRemoved(position)

                    // Refresh consumed calories and macro list for the current date
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    fetchMacrosByDate(today)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error deleting macro", e)
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



    override fun onBackPressed() {
        // Prevent going back to login
        moveTaskToBack(true) // minimizes the app instead of going back
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

    fun updateDashboard(baseCalorieGoal: Float, selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) {
        val caloriesRemainingText = findViewById<TextView>(R.id.calories_remaining_text)
        val consumedCaloriesValue = findViewById<TextView>(R.id.consumed_calories_value)

        getExistingCalories(selectedDate) { consumedCalories ->
            val burnedCaloriesText = burnedCaloriesTextView.text.toString().replace(" kcal", "")
            val burnedCalories = burnedCaloriesText.toFloatOrNull() ?: 0f
            val caloriesRemaining = baseCalorieGoal - consumedCalories + burnedCalories

            caloriesRemainingText.text = String.format("%.0f kcal", caloriesRemaining)
            consumedCaloriesValue.text = String.format("%.0f kcal", consumedCalories)

            val progressPercentage = calculateProgressPercentage(consumedCalories, baseCalorieGoal)
            progressBar.progress = progressPercentage

            Log.d("UserDashboard", "Updating dashboard: baseCalorieGoal=$baseCalorieGoal, consumedCalories=$consumedCalories, burnedCalories=$burnedCalories, remaining=$caloriesRemaining, progress=$progressPercentage, date=$selectedDate")

            // Reset progress bar color
            if (consumedCalories > baseCalorieGoal && baseCalorieGoal > 0) {
                progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
                showExceededCaloriesMessage()
            }
        }
    }
}
