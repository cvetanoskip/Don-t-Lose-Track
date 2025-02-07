package com.example.caloriecounter.LoginSignup

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.core.view.WindowCompat
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_EMAIL
import com.example.caloriecounter.MainActivity
import com.example.caloriecounter.R
import com.example.caloriecounter.User.UserDashboard
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.security.Key

private const val Key_email="email_key"
private const val Key_pass="pass_key"

class LoginActivity : AppCompatActivity() {
    lateinit var email:TextInputEditText
    lateinit var pass:TextInputEditText
    lateinit var loginbtn:Button
    lateinit var createacc:Button
    lateinit var backBtn: ImageView
    lateinit var forget:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        forget=findViewById(R.id.button)
        email=findViewById<TextInputEditText>(R.id.emailEdit)
        pass=findViewById<TextInputEditText>(R.id.passwordEdit)
        loginbtn=findViewById<Button>(R.id.loginbtn)
        email.addTextChangedListener(loginTextWatcher)
        backBtn = findViewById(R.id.signup_back_button)
        createacc=findViewById<Button>(R.id.create_acc_btn)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        loginbtn.setOnClickListener{

            Log.d("Debug", "Retrieved Email: ${email.text.toString()}, Password: ${pass.text.toString()}")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Debug", "Login Successful")

                        // Sign-in success, navigate to the UserDashboard
                        val intent = Intent(this, UserDashboard::class.java)
                        Log.d("AuthDebug", "Navigating to UserDashboard")
                        startActivity(intent)
                    } else {
                        // Handle login failure (e.g., wrong credentials)
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
//                    val user = FirebaseAuth.getInstance().currentUser
//
//                    if (user != null) {
//                        // If the user is signed in, navigate to the UserDashboard
//                        val intent = Intent(this, UserDashboard::class.java)
//                        startActivity(intent)
//                    } else {
//                        // If the user is not signed in, show a toast or error message
//                        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
//                    }
                }
        }
        forget.setOnClickListener{
            val intent= Intent(this,ForgetPassword::class.java)
            startActivity(intent)
        }
        createacc.setOnClickListener {
            val intent= Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
        backBtn.setOnClickListener {
            finish()
        }
        pass.addTextChangedListener(loginTextWatcher)
        if(savedInstanceState!=null)
        {
            var savedEmails:String
            var savedPass:String
            savedEmails=savedInstanceState.getString(Key_email).toString()
            savedPass=savedInstanceState.getString(Key_pass).toString()
            email.setText(savedEmails)
            pass.setText(savedPass)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString(Key_email,email.text.toString())
        savedInstanceState.putString(Key_pass,pass.text.toString())



        super.onSaveInstanceState(savedInstanceState)
    }

    private val loginTextWatcher= object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val emailInput=email.getText().toString().trim();
            val passInput=pass.getText().toString().trim();
            if(!emailInput.isEmpty()&&!passInput.isEmpty()) {
                loginbtn.setEnabled(true)
                loginbtn.setBackgroundColor(Color.parseColor("#FF000000"))
            }
            else{
                loginbtn.setEnabled(false)
                loginbtn.setBackgroundColor(Color.parseColor("#6C6C6C"))

            }
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val emailInput=email.getText().toString().trim();
            val passInput=pass.getText().toString().trim();
            if(!emailInput.isEmpty()&&!passInput.isEmpty()) {
                loginbtn.setEnabled(true)
                loginbtn.setBackgroundColor(Color.parseColor("#FF000000"))
            }
            else{
                loginbtn.setEnabled(false)
                loginbtn.setBackgroundColor(Color.parseColor("#6C6C6C"))

            }
        }

        override fun afterTextChanged(p0: Editable?) {
            val emailInput=email.getText().toString().trim();
            val passInput=pass.getText().toString().trim();
            if(!emailInput.isEmpty()&&!passInput.isEmpty()) {
                loginbtn.setEnabled(true)
                loginbtn.setBackgroundColor(Color.parseColor("#FF000000"))
            }
            else{
                loginbtn.setEnabled(false)
                loginbtn.setBackgroundColor(Color.parseColor("#6C6C6C"))

            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }
}