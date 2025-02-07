package com.example.caloriecounter.LoginSignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.example.caloriecounter.R
import com.google.android.material.textfield.TextInputLayout

class SetNewPassword : AppCompatActivity() {
    lateinit var confirm:Button
    lateinit var back_btn:ImageView
    lateinit var newPassword:TextInputLayout
    lateinit var confirmPassword:TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_password)
        confirm=findViewById(R.id.confirm_btn)
        back_btn=findViewById(R.id.back_button)
        newPassword=findViewById(R.id.new_password)
        confirmPassword=findViewById(R.id.confirm_new_password)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        confirm.setOnClickListener{
           if(!validatePassword()or!validateconfirmPassword()) {

               return@setOnClickListener
            }
            if(newPassword.editText?.text.toString().trim()==confirmPassword.editText?.text.toString().trim()){
                var intent = Intent(this, ForgetPasswordSuccessMessage::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this,"Passwords don't match",Toast.LENGTH_SHORT).show()
            }
        }
        back_btn.setOnClickListener{
            onBackPressed()
        }

    }
    private fun validatePassword():Boolean{
        var valuePass = newPassword.editText?.text.toString()
        val checkPassword: String = "^"+"(?=.*[0-9])"+"(?=.*[a-z])"+"(?=.*[A-Z])"+"(?=\\S+$)"+".{8,}"+"$"
        if (valuePass.isEmpty()) {
            newPassword.error = "Field can not be empty"
            return false
        } else if (valuePass.length > 20) {
            newPassword.error = "Password is too large (max 20 characters)"
            return false
        } else if (!valuePass.matches(checkPassword.toRegex())) {
            newPassword.error = "Password should contain a min of 8 characters at least: 1 Digit,1 Lowercase letter, 1 Uppercase letter and no white spaces! "
            return false
        } else {
            newPassword.error = null
            newPassword.isErrorEnabled = false
            return true
        }
    }
    private fun validateconfirmPassword():Boolean{
        var valuePass = confirmPassword.editText?.text.toString()
        val checkPassword: String = "^"+"(?=.*[0-9])"+"(?=.*[a-z])"+"(?=.*[A-Z])"+"(?=\\S+$)"+".{8,}"+"$"
        if (valuePass.isEmpty()) {
            confirmPassword.error = "Field can not be empty"
            return false
        } else if (valuePass.length > 20) {
            confirmPassword.error = "Password is too large (max 20 characters)"
            return false
        } else if (!valuePass.matches(checkPassword.toRegex())) {
            confirmPassword.error = "Password should contain a min of 8 characters at least: 1 Digit,1 Lowercase letter, 1 Uppercase letter and no white spaces! "
            return false
        } else {
            confirmPassword.error = null
            confirmPassword.isErrorEnabled = false
            return true
        }
    }
}