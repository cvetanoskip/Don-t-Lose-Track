package com.example.caloriecounter.LoginSignup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.example.caloriecounter.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
class ForgetPassword : AppCompatActivity() {
    lateinit var email_input:TextInputLayout
    lateinit var next_btn:Button
    lateinit var backBtn:ImageView;
    lateinit var valueMail:String
   lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        email_input=findViewById(R.id.Email_layout)
        valueMail = email_input.editText?.text.toString().trim()
        next_btn=findViewById(R.id.next_btn)
        backBtn=findViewById(R.id.back_icon)
        mAuth=FirebaseAuth.getInstance()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        next_btn.setOnClickListener{
            if(!validateEmail())
            {
                return@setOnClickListener
            }
            ResetPassword()

        }
        backBtn.setOnClickListener {
            finish()
        }
//        loadData()
    }

    override fun onStop() {
        super.onStop()
//        saveData()
    }
//    private fun saveData(){
//        val insertedEmail:String =email_input.text.toString()
//        val sharedPreferences:SharedPreferences=getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)
//        val editor:SharedPreferences.Editor=sharedPreferences.edit()
//        editor.apply(){
//            putString("Email_key",insertedEmail)
//        }.apply()
//
//    }
//    private fun loadData(){
//        val sharedPreferences:SharedPreferences=getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)
//        val savedString: String? =sharedPreferences.getString("Email_key",null)
//        email_input.setText(savedString)
//    }
    private fun ResetPassword()
{
        next_btn.visibility=View.INVISIBLE;

    mAuth.sendPasswordResetEmail(valueMail)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Notify user that reset email has been sent
                Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish();

            } else {
                // Handle failure
                Toast.makeText(this, "Failed to send reset email. Please check the email address.", Toast.LENGTH_SHORT).show()
            }
        }


    }
private fun validateEmail(): Boolean {
    valueMail = email_input.editText?.text.toString().trim()
    email_input.editText?.setText(email_input.editText?.text.toString().replace("\\s$".toRegex(), ""))
    val checkEmail: String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    if (valueMail.isEmpty()) {
        email_input.error = "Field can not be empty"
        return false
    } else if (valueMail.length > 254) {
        email_input.error = "Email is too large"
        return false
    } else if (!valueMail.matches(checkEmail.toRegex())) {
        email_input.error = "Invalid Email"
        return false
    } else {
        email_input.error = null
        email_input.isErrorEnabled = false
        return true
    }
}
}