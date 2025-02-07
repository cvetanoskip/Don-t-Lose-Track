package com.example.caloriecounter.LoginSignup

import android.app.ActivityOptions
import android.content.Intent


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.*
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PHONENUMBER
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GENDER
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEEK_GOAL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_ACTIVITY
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PHONEVERIFICATION
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_HEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_UNIT_PREFERENCE
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_DATE
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_EMAIL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_FULLNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PASSWORD
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_USERNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import androidx.core.view.WindowCompat
import com.example.caloriecounter.Databases.SessionManager
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
import com.example.caloriecounter.LoginSignup.PopUps.PopUpGoalWeightSet
import com.example.caloriecounter.LoginSignup.PopUps.PopUpWeightSet
import com.example.caloriecounter.R
import com.example.caloriecounter.User.UserDashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
private const val Key_goal_week=1
class SignUp7thClass : AppCompatActivity() {
    lateinit var week_goal: RadioGroup
    lateinit var Recommended:RadioButton
    lateinit var backBtn: ImageView
    lateinit var next: Button
    lateinit var login: Button
    lateinit var titleText: TextView
    lateinit var lose_slow:RadioButton
    lateinit var lose:RadioButton
    lateinit var lose_fast:RadioButton
    lateinit var weight_goal:TextView
    lateinit var sessionManager: SessionManager
    lateinit var auth: FirebaseAuth
    var Kilogram = false
    var Pounds = false
    var Stones = false
    var Lose1 = false
    var Maintain1 = false
    var Gain1 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up7th_class)
        val db = FirebaseFirestore.getInstance()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        backBtn = findViewById(R.id.signup_back_button)
        next = findViewById(R.id.signup_next_button)
        login = findViewById(R.id.signup_login_button)
        auth = FirebaseAuth.getInstance()


        titleText = findViewById(R.id.signup_title_text)
        Recommended=findViewById(R.id.lose_recommended)
        lose_slow=findViewById(R.id.lose_slow)
        lose=findViewById(R.id.lose)
        lose_fast=findViewById(R.id.lose_fast)
        week_goal=findViewById(R.id.radio_week_goal)
        sessionManager = SessionManager(this)
        val userDetails = sessionManager.getUsersDetailFromSession()
        val email = userDetails[Key_EMAIL] ?: ""
        val password = userDetails[Key_PASSWORD] ?: ""
        weight_goal=findViewById(R.id.insert_goal_weight)
        Kilogram = intent.getBooleanExtra("kilogram", false)
        Pounds = intent.getBooleanExtra("pounds", false)
        Stones = intent.getBooleanExtra("stones", false)
        Lose1 = intent.getBooleanExtra("lose", false)
        Maintain1 = intent.getBooleanExtra("maintain", false)
        Gain1 = intent.getBooleanExtra("gain", false)
        val currentUser = FirebaseAuth.getInstance().currentUser

//        auth.addAuthStateListener(object : FirebaseAuth.AuthStateListener {
//            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
//                val user = firebaseAuth.currentUser
//                if (user != null) {
//                    // User is logged in, proceed to the dashboard
//                    val intent = Intent(this@SignUp7thClass, UserDashboard::class.java)
//                    startActivity(intent)
//                }
//            }
//        })
        next.setOnClickListener {
            val email = userDetails[Key_EMAIL]?.toString() ?: ""
            val password = userDetails[Key_PASSWORD]?.toString() ?: ""
            if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.w("Debug", "Auth failed")
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("AuthDebug", "User registered: ${auth.currentUser?.uid}")
                            val userId = auth.currentUser?.uid
                            val userData = hashMapOf(
                                "fullname" to (userDetails[Key_FULLNAME] ?: ""),
                                "username" to (userDetails[Key_USERNAME] ?: ""),
                                "email" to (userDetails[Key_EMAIL] ?: ""),
                                "phoneNo" to (userDetails[Key_PHONENUMBER] ?: ""),
                                "password" to (userDetails[Key_PASSWORD] ?: ""),  // Avoid saving password directly in Firestore
                                "age" to (userDetails[Key_DATE] ?: ""),
                                "gender" to (userDetails[Key_GENDER] ?: ""),
                                "activity" to (userDetails[Key_ACTIVITY] ?: ""),
                                "phoneVerify" to (userDetails[Key_PHONEVERIFICATION] ?: ""),
                                "goal" to (userDetails[Key_GOAL] ?: ""),
                                "week_goal" to (userDetails[Key_WEEK_GOAL] ?: ""),
                                "height" to (userDetails[Key_HEIGHT] ?: ""),
                                "weight" to (userDetails[Key_WEIGHT] ?: ""),
                                "goal_weight" to (userDetails[Key_GOAL_WEIGHT] ?: ""),
                                "speed" to (userDetails[Key_Speed] ?: ""),
                                "unitPreference" to (userDetails[Key_UNIT_PREFERENCE] ?: ""),
                                "userId" to (userId ?: "")

                            )

                            if (userId != null) {
                                // Store data in Firestore under the 'users' collection with the current user's UID as the document ID
                                db.collection("users")
                                    .document(userId)  // Use the Firebase UID as the document ID
                                    .set(userData)  // Store the user data in Firestore
                                    .addOnSuccessListener {
                                        // Data successfully added to Firestore
                                        Log.d("Firestore", "User data successfully added!")
                                    }
                                    .addOnFailureListener { e ->
                                        // Failed to add data to Firestore
                                        Log.w("Firestore", "Error adding user data", e)
                                    }
                                val intent = Intent(this, UserDashboard::class.java)
                                startActivity(intent)
                            } else {
                                Log.w("Firestore", "No current user logged in.")
                            }
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("AuthDebug", "Registration failed: ${task.exception?.message}")
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show()
            }

//            val intent= Intent(this, UserDashboard::class.java)
//            startActivity(intent)
        }
        lose_slow.setOnClickListener {
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = "slow",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }

        lose.setOnClickListener {
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = "moderate",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        Recommended.setOnClickListener{
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = "recommended",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        lose_fast.setOnClickListener {
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = "fast",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        weight_goal.setOnClickListener{

            var dialog1=PopUpGoalWeightSet()
            dialog1.show(supportFragmentManager,"customDialog1")

        }
        if(Lose1==true)
        {
            if(Kilogram==true)
            {
                weight_goal.setHint("0 kg")
                lose_slow.setText("Lose 0.25 kg Per Week")
                Recommended.setText("Lose 0.5 kg Per Week\nRecommended")
                lose.setText("Lose 0.75 kg Per Week")
                lose_fast.setText("Lose 1 kg Per Week")

            }
            if(Kilogram==false)
            {
                if(Pounds==true)
                    weight_goal.setHint("0 lbs")
                else if(Stones==true)
                    weight_goal.setHint("0 st 0 lbs")
                lose_slow.setText("Lose 0.5 lbs Per Week")
                Recommended.setText("Lose 1 lbs Per Week\nRecommended")
                lose.setText("Lose 1.5 lbs Per Week")
                lose_fast.setText("Lose 2 lbs Per Week")

            }
        }
       else if(Gain1==true)
        {
            if(Kilogram==true)
            {
                weight_goal.setHint("0 kg")
                lose_slow.setText("Gain 0.5 kg Per Week")
                lose.setText("Gain 1 kg Per Week")
                Recommended.visibility=View.GONE
                lose_fast.visibility=View.GONE

            }
            if(Kilogram==false)
            {
                if(Pounds==true)
                    weight_goal.setHint("0 lbs")
                else if(Stones==true)
                    weight_goal.setHint("0 st 0 lbs")
                lose_slow.setText("Gain 1 lbs Per Week")
                lose.setText("Gain 2 lbs Per Week")
                Recommended.visibility=View.GONE
                lose_fast.visibility=View.GONE
            }
        }
        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            val pairs = arrayOf(
                (Pair<View, String>(backBtn, "transition_back_arrow_btn")),
                Pair<View, String>(next, "transition_next_btn"),
                (Pair<View, String>(login, "transition_login_btn")),
                (Pair<View, String>(titleText, "transition_title_text"))
            )
            val options: ActivityOptions
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp7thClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        if(savedInstanceState!=null)
        {
            var savedWeeklyID:Int

            savedWeeklyID=savedInstanceState.getInt(Key_goal_week.toString())

            week_goal!!.check(savedWeeklyID)

        }
        backBtn.setOnClickListener {
            finish()
        }

    }
    private fun validateWeight(): Boolean {

        var validateWeight = weight_goal.text.toString()

        if (validateWeight.isEmpty()) {
            weight_goal.error = "Field can not be empty"
            return false
        }  else {
            weight_goal.error = null
            return true
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt(Key_goal_week.toString(),week_goal!!.checkedRadioButtonId)
        super.onSaveInstanceState(savedInstanceState)
    }
    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}