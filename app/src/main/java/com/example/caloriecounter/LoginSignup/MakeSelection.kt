package com.example.caloriecounter.LoginSignup

import android.app.ActivityOptions
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.WindowCompat
import com.example.caloriecounter.R

class MakeSelection : AppCompatActivity() {
    class Sms {
        companion object {
            var BtnClicked =false
        }
    }
    class Email {
        companion object {
            var BtnClicked =false
        }
    }
    lateinit var sms_button:Button
    lateinit var email_button:Button
    lateinit var back_button:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_selection)
        sms_button=findViewById(R.id.sms_btn)
        email_button=findViewById(R.id.email_btn)
        back_button=findViewById(R.id.selection_back_button)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        sms_button.setOnClickListener{
            Sms.BtnClicked=true
            Email.BtnClicked=false
            val intent = Intent(this, SignUp3rdClass::class.java)
            startActivity(intent)
        }
        email_button.setOnClickListener{
            Email.BtnClicked=true
            Sms.BtnClicked=false
            val intent=Intent(this,ForgetPassword::class.java)
            startActivity(intent)
        }
        back_button.setOnClickListener{
            onBackPressed()
        }
    }
}