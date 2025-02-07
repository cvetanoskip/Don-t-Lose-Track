package com.example.caloriecounter

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.caloriecounter.LoginSignup.LoginActivity
import com.example.caloriecounter.LoginSignup.SignupActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

     lateinit var  name: TextView
      lateinit var  login:Button
      lateinit var  signup:Button
      lateinit var bottomAnim:Animation
      lateinit var buttonAnim: Animation
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        name=findViewById<TextView>(R.id.Name)
        login=findViewById<Button>(R.id.Login_button)
        login.setOnClickListener{
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        signup=findViewById<Button>(R.id.Signup_button)
        signup.setOnClickListener {
            val intent=Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_anim)
        buttonAnim=AnimationUtils.loadAnimation(this,R.anim.button_anim)
        name.setAnimation(bottomAnim)
        login.visibility= View.GONE
        signup.visibility= View.GONE

        Handler().postDelayed({
            login.visibility= View.VISIBLE
            signup.visibility= View.VISIBLE
                login.setAnimation(buttonAnim)
                signup.setAnimation(buttonAnim)
        },2000);


    }

}