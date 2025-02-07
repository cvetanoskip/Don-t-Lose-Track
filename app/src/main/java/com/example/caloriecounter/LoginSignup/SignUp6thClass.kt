package com.example.caloriecounter.LoginSignup

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.*
import androidx.core.view.WindowCompat
import com.example.caloriecounter.Databases.SessionManager
import com.example.caloriecounter.LoginSignup.PopUps.*
import com.example.caloriecounter.R
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
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_USERNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
import com.example.caloriecounter.User.UserDashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
private const val Key_activity=1
class SignUp6thClass : AppCompatActivity() {
    lateinit var backBtn: ImageView
    lateinit var next: Button
    lateinit var login: Button
    lateinit var titleText: TextView
    lateinit var activity: RadioGroup
    lateinit var sedentary:RadioButton
    lateinit var lightly:RadioButton
    lateinit var moderately:RadioButton
    lateinit var veryactive:RadioButton
    lateinit var sessionManager: SessionManager
    lateinit var auth: FirebaseAuth
    var Kilogram = false
    var Pounds = false
    var Stones = false
    var Lose1 = false
    var Maintain1 = false
    var Gain1 = false
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up6th_class)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        backBtn = findViewById(R.id.signup_back_button)
        next = findViewById(R.id.signup_next_button)
        login = findViewById(R.id.signup_login_button)
        titleText = findViewById(R.id.signup_title_text)
        activity=findViewById(R.id.radio_activity)
        sedentary=findViewById(R.id.not_active)
        lightly=findViewById(R.id.lightly_active)
        moderately=findViewById(R.id.moderately_active)
        veryactive=findViewById(R.id.Very_active)
        sessionManager = SessionManager(this)
        val userDetails = sessionManager.getUsersDetailFromSession()
        val email = userDetails[Key_EMAIL] ?: ""
        val password = userDetails[Key_PASSWORD] ?: ""
        Kilogram = intent.getBooleanExtra("kilogram", false)
        Pounds = intent.getBooleanExtra("pounds", false)
        Stones = intent.getBooleanExtra("stones", false)
        Lose1 = intent.getBooleanExtra("lose", false)
        Maintain1 = intent.getBooleanExtra("maintain", false)
        Gain1 = intent.getBooleanExtra("gain", false)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        sedentary.setOnClickListener {
            var dialog= SedentaryDialog()
            dialog.show(supportFragmentManager,"customDialog")
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity =  "sedentary",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = userDetails[Key_Speed] ?: "",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        lightly.setOnClickListener {
            var dialog= LightlyDialog()
            dialog.show(supportFragmentManager,"customDialog")
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity =  "light",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = userDetails[Key_Speed] ?: "",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        moderately.setOnClickListener {
            var dialog= ModeratelyDialog()
            dialog.show(supportFragmentManager,"customDialog")
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity =  "moderate",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = userDetails[Key_Speed] ?: "",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        veryactive.setOnClickListener {
            var dialog= VeryActiveDialog()
            dialog.show(supportFragmentManager,"customDialog")
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity =  "very active",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = userDetails[Key_Speed] ?: "",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
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
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp6thClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        if(savedInstanceState!=null)
        {
            var savedActivityID:Int

            savedActivityID=savedInstanceState.getInt(Key_activity.toString())

            activity!!.check(savedActivityID)

        }

            next.setOnClickListener {
                if (!validateActivity())
                {
                    return@setOnClickListener
                }
                else if(Gain1==true||Lose1==true)
                {
                val intent = Intent(this, SignUp7thClass::class.java)
                    intent.putExtra("kilogram",Kilogram)
                    intent.putExtra("pounds",Pounds)
                    intent.putExtra("stones",Stones)
                    intent.putExtra("gain",Gain1)
                    intent.putExtra("maintain",Maintain1)
                    intent.putExtra("lose",Lose1)
                val pairs = arrayOf(
                    (Pair<View, String>(backBtn, "transition_back_arrow_btn")),
                    Pair<View, String>(next, "transition_next_btn"),
                    (Pair<View, String>(login, "transition_login_btn")),
                    (Pair<View, String>(titleText, "transition_title_text"))
                )
                val options: ActivityOptions
                options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp6thClass, *pairs)
                startActivity(intent, options.toBundle())
            }
                else if(Maintain1==true)
                {

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
                                    userData.forEach { (key, value) ->
                                        Log.d("Debug", "$key: $value")
                                    }
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
//                    val userId = currentUser?.uid
//                    val userData = hashMapOf(
//                        "fullname" to (userDetails[Key_FULLNAME] ?: ""),
//                        "username" to (userDetails[Key_USERNAME] ?: ""),
//                        "email" to (userDetails[Key_EMAIL] ?: ""),
//                        "phoneNo" to (userDetails[Key_PHONENUMBER] ?: ""),
//                        "password" to (userDetails[Key_PASSWORD] ?: ""),  // Avoid saving password directly in Firestore
//                        "age" to (userDetails[Key_DATE] ?: ""),
//                        "gender" to (userDetails[Key_GENDER] ?: ""),
//                        "activity" to (userDetails[Key_ACTIVITY] ?: ""),
//                        "phoneVerify" to (userDetails[Key_PHONEVERIFICATION] ?: ""),
//                        "goal" to (userDetails[Key_GOAL] ?: ""),
//                        "week_goal" to (userDetails[Key_WEEK_GOAL] ?: ""),
//                        "height" to (userDetails[Key_HEIGHT] ?: ""),
//                        "weight" to (userDetails[Key_WEIGHT] ?: ""),
//                        "goal_weight" to (userDetails[Key_GOAL_WEIGHT] ?: ""),
//                        "speed" to (userDetails[Key_Speed] ?: ""),
//                        "unitPreference" to (userDetails[Key_UNIT_PREFERENCE] ?: ""),
//                        "userId" to (userId ?: "")
//
//                    )
//                    userData.forEach { (key, value) ->
//                        Log.d("Debug", "$key: $value")
//                    }
//                    if (currentUser != null) {
//                        // Store data in Firestore under the 'users' collection with the current user's UID as the document ID
//                        db.collection("users")
//                            .document(currentUser.uid)  // Use the Firebase UID as the document ID
//                            .set(userData)  // Store the user data in Firestore
//                            .addOnSuccessListener {
//                                // Data successfully added to Firestore
//                                Log.d("Firestore", "User data successfully added!")
//                            }
//                            .addOnFailureListener { e ->
//                                // Failed to add data to Firestore
//                                Log.w("Firestore", "Error adding user data", e)
//                            }
//                    } else {
//                        Log.w("Firestore", "No current user logged in.")
//                    }
                    // Debugging the values

                }
        }
        backBtn.setOnClickListener {

            finish()
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt(Key_activity.toString(),activity!!.checkedRadioButtonId)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }
    private fun validateActivity(): Boolean {
        if (activity.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please Select An Option", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
}