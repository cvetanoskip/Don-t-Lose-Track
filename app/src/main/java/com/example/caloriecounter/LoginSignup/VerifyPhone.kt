package com.example.caloriecounter.LoginSignup

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowCompat
import com.chaos.view.PinView
import com.example.caloriecounter.R
import com.google.android.material.textfield.TextInputEditText

import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class VerifyPhone : AppCompatActivity() {
    lateinit var closeBtn: ImageView
    lateinit var description: TextView
    lateinit var verification:PinView
    lateinit var verify:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        closeBtn = findViewById(R.id.verify_close_button)
        description=findViewById(R.id.description_code)
        verify=findViewById(R.id.verify_btn)
        verification=findViewById(R.id.pin_view)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        var _phoneNo=intent.getStringExtra("phoneNo")
        var description1:String
        description1=description.text.toString()
        if(SignUp3rdClass.Phone.PhoneNumber!="Phone Number")
        {
            description1+=SignUp3rdClass.Phone.PhoneNumber
            description.setText(description1)
        }
        closeBtn.setOnClickListener{
            onBackPressed()
        }
        verify.setOnClickListener{
            if(MakeSelection.Sms.BtnClicked==true)
            {
                var intent=Intent(this,SetNewPassword::class.java)
                startActivity(intent)
            }
            else {
                var intent = Intent(this, SignUp4thClass::class.java)
                startActivity(intent)
            }
        }

    }

}