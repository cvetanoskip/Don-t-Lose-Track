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
import com.example.caloriecounter.LoginSignup.PopUps.PopUpHeightSet
import com.example.caloriecounter.LoginSignup.PopUps.PopUpWeightSet
import com.example.caloriecounter.R
private const val Key_height="Country Code"
private const val Key_weight="Country name"

class SignUp4thClass : AppCompatActivity() {
     lateinit var height:TextView
     lateinit var weight:TextView
     lateinit var backBtn: ImageView
     lateinit var next: Button
     lateinit var login: Button
     lateinit var titleText: TextView
     var Kilogram=false
     var Pounds=false
     var Stones=false
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up4th_class)
        backBtn = findViewById(R.id.signup_back_button)
        next = findViewById(R.id.signup_next_button)
        login = findViewById(R.id.signup_login_button)
        titleText = findViewById(R.id.signup_title_text)
        height=findViewById(R.id.insert_height)
        weight=findViewById(R.id.insert_weight)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        weight.setOnClickListener{
            var dialog1=PopUpWeightSet()
            dialog1.show(supportFragmentManager,"customDialog1")
        }

        height.setOnClickListener {
            var dialog=PopUpHeightSet()
            dialog.show(supportFragmentManager,"customDialog")

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
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp4thClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        next.setOnClickListener {
            if(!validateHeight()or!validateWeight())
            {
                return@setOnClickListener
            }
            val intent = Intent(this, SignUp5thClass::class.java)
            intent.putExtra("kilogram",Kilogram)
            intent.putExtra("pounds",Pounds)
            intent.putExtra("stones",Stones)
            val pairs = arrayOf(
                (Pair<View, String>(backBtn, "transition_back_arrow_btn")),
                Pair<View, String>(next, "transition_next_btn"),
                (Pair<View, String>(login, "transition_login_btn")),
                (Pair<View, String>(titleText, "transition_title_text"))
            )
            val options: ActivityOptions
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp4thClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        backBtn.setOnClickListener {
            finish()
        }
        if(savedInstanceState!=null)
        {
            var savedHeight:String
            var savedWeight:String


            savedHeight=savedInstanceState.getString(Key_height).toString()
            savedWeight=savedInstanceState.getString(Key_weight).toString()

           height.setText(savedHeight)
           weight.setText(savedWeight)

        }
    }
    private fun validateHeight(): Boolean {

        var valueHeight = height.text.toString()
        val checkPhone: String = "^[0-9]{1,20}$"
        if (valueHeight.isEmpty()) {
            height.error = "Field can not be empty"
            return false
        }  else {
            height.error = null
            return true
        }
    }
    private fun validateWeight(): Boolean {

        var validateWeight = weight.text.toString()
        if (validateWeight.isEmpty()) {
            weight.error = "Field can not be empty"
            return false
        }  else {
            weight.error = null
            return true
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString(Key_height,height.text.toString())
        savedInstanceState.putString(Key_weight,weight.text.toString())

        super.onSaveInstanceState(savedInstanceState)
    }
    override fun onPause() {
        super.onPause()
        weight.setOnClickListener{
            var dialog1=PopUpWeightSet()
            dialog1.show(supportFragmentManager,"customDialog1")
        }
        overridePendingTransition(0,0)
    }

    override fun onRestart() {
        super.onRestart()
        weight.setOnClickListener{
            var dialog1=PopUpWeightSet()
            dialog1.show(supportFragmentManager,"customDialog1")
        }
    }

    override fun onResume() {
        super.onResume()
        weight.setOnClickListener{
            var dialog1=PopUpWeightSet()
            dialog1.show(supportFragmentManager,"customDialog1")
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        weight.setOnClickListener{
            var dialog1=PopUpWeightSet()
            dialog1.show(supportFragmentManager,"customDialog1")
        }
    }
    override fun onStop() {
        super.onStop()
        weight.setOnClickListener{
            var dialog1=PopUpWeightSet()
            dialog1.show(supportFragmentManager,"customDialog1")
        }
    }
}