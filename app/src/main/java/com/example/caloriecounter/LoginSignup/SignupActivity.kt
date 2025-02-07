package com.example.caloriecounter.LoginSignup

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.example.caloriecounter.R
import android.util.Pair
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.example.caloriecounter.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.caloriecounter.Databases.SessionManager
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PHONENUMBER
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GENDER
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEEK_GOAL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_ACTIVITY
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PHONEVERIFICATION
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_HEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_UNIT_PREFERENCE
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_DATE
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_EMAIL
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_FULLNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_PASSWORD
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_USERNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import com.google.firebase.firestore.FirebaseFirestore

private const val Key_email = "email_key"
private const val Key_pass = "pass_key"
private const val Key_user = "user_key"
private const val Key_full = "full_key"

class SignupActivity : AppCompatActivity() {
    //Variables
    lateinit var backBtn: ImageView
    lateinit var next: Button
    lateinit var login: Button
    lateinit var titleText: TextView
    lateinit var email: TextInputLayout
    lateinit var pass: TextInputLayout
    lateinit var user: TextInputLayout
    lateinit var full: TextInputLayout
    lateinit var name:String
    lateinit var userN:String
    lateinit var password:String
    lateinit var emailS:String
    override fun onCreate(savedInstanceState: Bundle?) {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        //Hooks
        backBtn = findViewById(R.id.signup_back_button)
        next = findViewById(R.id.signup_next_button)
        login = findViewById(R.id.signup_login_button)
        titleText = findViewById(R.id.signup_title_text)
        email = findViewById(R.id.emailLayout)
        pass = findViewById(R.id.passwordLayout)
        user = findViewById(R.id.usernameLayout)
        full = findViewById(R.id.fullnameLayout)


        next.setOnClickListener {
            emailS = email.editText?.text.toString().trim()
            password = pass.editText?.text.toString().trim()
            userN = user.editText?.text.toString().trim()
            name = full.editText?.text.toString().trim()
            Log.d("Validation", "Email: $emailS, Password: $password")
            if (!validateFullName() or !validateUserName() or !validateEmail() or !validatePassword()) {

                return@setOnClickListener
            }
            if (emailS.isEmpty() || password.isEmpty()) {
                Log.d("Debug", "Email or Password is empty.")
                Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("email", emailS) // Check for existing email
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Email already exists, show message
                        Toast.makeText(this, "User is already registered.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Email is not registered, proceed to save user data
                        val sessionManager = SessionManager(this)
                        val userDetails = sessionManager.getUsersDetailFromSession()
                        Log.d("Debug", "Email: $emailS, Password: $password")

                        // Save user details securely in the session manager
                        sessionManager.saveUserDetails(
                            fullname = name,
                            username = userN,
                            email = emailS,
                            phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                            password = password,
                            age = userDetails[Key_DATE] ?: "",
                            gender = userDetails[Key_GENDER] ?: "",
                            activity = userDetails[Key_ACTIVITY] ?: "",
                            phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                            goal = userDetails[Key_GOAL] ?: "",
                            week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                            height = userDetails[Key_HEIGHT] ?: "",
                            weight = userDetails[SessionManager.Key_WEIGHT] ?: "", // Only update weight
                            speed = userDetails[Key_Speed] ?: "",
                            goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                            unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: "imperial"
                        )

                        // Navigate to the next activity
                        val intent = Intent(this, SignUp2ndClass::class.java)
                        val pairs = arrayOf(
                            Pair<View, String>(backBtn, "transition_back_arrow_btn"),
                            Pair<View, String>(next, "transition_next_btn"),
                            Pair<View, String>(login, "transition_login_btn"),
                            Pair<View, String>(titleText, "transition_title_text")
                        )
                        val options = ActivityOptions.makeSceneTransitionAnimation(this@SignupActivity, *pairs)
                        startActivity(intent, options.toBundle())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseError", "Error checking email: ", exception)
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }
        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        backBtn.setOnClickListener {
            finish()
        }

    }



    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun validateFullName(): Boolean {
        val valueName = full.editText?.text.toString().trim()
        full.editText?.setText(full.editText?.text.toString().replace("\\s$".toRegex(), ""))
        if (valueName.isEmpty()) {
            full.error = "Field can not be empty"
            return false
        } else {
            full.error = null
            full.isErrorEnabled = false
            return true
        }
    }

    private fun validateUserName(): Boolean {
        val valueUser = user.editText?.text.toString().trim()
        val checkspaces: String = "\\A\\w{1,20}\\z"
        user.editText?.setText(user.editText?.text.toString().replace("\\s$".toRegex(), ""))
        if (valueUser.isEmpty()) {
            user.error = "Field can not be empty"
            return false
        } else if (valueUser.length > 20) {
            user.error = "Username is too large"
            return false
        } else if (!valueUser.matches(checkspaces.toRegex())) {
            user.error = "No white spaces are allowed"
            return false
        } else {
            user.error = null
            user.isErrorEnabled = false
            return true
        }
    }
    private fun validateEmail(): Boolean {
        var valueMail = email.editText?.text.toString().trim()
        email.editText?.setText(email.editText?.text.toString().replace("\\s$".toRegex(), ""))
        val checkEmail: String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (valueMail.isEmpty()) {
            email.error = "Field can not be empty"
            return false
        } else if (valueMail.length > 254) {
            email.error = "Email is too large"
            return false
        } else if (!valueMail.matches(checkEmail.toRegex())) {
            email.error = "Invalid Email"
            return false
        } else {
            email.error = null
            email.isErrorEnabled = false
            return true
        }
    }
    private fun validatePassword(): Boolean {
        var valuePass = pass.editText?.text.toString()
        val checkPassword: String = "^"+"(?=.*[0-9])"+"(?=.*[a-z])"+"(?=.*[A-Z])"+"(?=\\S+$)"+".{8,}"+"$"
        if (valuePass.isEmpty()) {
            pass.error = "Field can not be empty"
            return false
        } else if (valuePass.length > 20) {
            pass.error = "Password is too large (max 20 characters)"
            return false
        } else if (!valuePass.matches(checkPassword.toRegex())) {
            pass.error = "Password should contain a min of 8 characters at least: 1 Digit,1 Lowercase letter, 1 Uppercase letter and no white spaces! "
            return false
        } else {
            pass.error = null
            pass.isErrorEnabled = false
            return true
        }
    }

}


