package com.example.caloriecounter.LoginSignup

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.RadioGroup
import android.widget.*
import androidx.core.view.WindowCompat
import com.example.caloriecounter.Databases.SessionManager
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
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import com.example.caloriecounter.MainActivity
import com.example.caloriecounter.MyApp
import com.example.caloriecounter.R
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import javax.security.auth.callback.Callback

private const val Key_gender = 1
private const val Key_day = "day"
private const val Key_month = "Month"
private const val Key_year = "year"

class SignUp2ndClass : AppCompatActivity() {
    lateinit var backBtn: ImageView
    lateinit var next: Button
    lateinit var login: Button
    lateinit var titleText: TextView
    lateinit var gender: RadioGroup
    lateinit var selectedGender: RadioButton
    lateinit var date: DatePicker
    private lateinit var sessionManager: SessionManager
    //val myapp=application as MyApp
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        sessionManager = SessionManager(this)
        val userDetails = sessionManager.getUsersDetailFromSession()
        Log.d("Debug", "Retrieved Email: ${userDetails[Key_EMAIL]}")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2nd_class)
        backBtn = findViewById(R.id.signup_back_button)
        next = findViewById(R.id.signup_next_button)
        login = findViewById(R.id.signup_login_button)
        titleText = findViewById(R.id.signup_title_text)
        gender = findViewById(R.id.radio_group)
        date = findViewById(R.id.date_Picker)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        next.setOnClickListener {

            if(!validateGender()or!validateAge())
            {
                return@setOnClickListener
            }
            selectedGender = findViewById(gender.checkedRadioButtonId)
            val genderS: String = selectedGender.text.toString()

            // Get selected date from DatePicker
            val day = date.dayOfMonth
            val month = date.month
            val year = date.year

            // Calculate age
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Adjust for zero-based months
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            var userAge = currentYear - year

            // Adjust the age if the birthday hasn't occurred yet this year
            if (currentMonth < month + 1 || (currentMonth == month + 1 && currentDay < day)) {
                userAge--
            }

            // Update SessionManager with gender and age

            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age =  userAge.toString(),
                gender =  genderS,
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal = userDetails[Key_GOAL] ?: "",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                speed = userDetails[Key_Speed] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[SessionManager.Key_WEIGHT] ?:"", // Only update weight
                goal_weight = userDetails[SessionManager.Key_GOAL_WEIGHT] ?:"", // Only update weight
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
            val intent = Intent(this, SignUp4thClass::class.java)
          //  myapp.Name=_fullname

            intent.putExtra("gender",genderS)
            intent.putExtra("day",day)
            intent.putExtra("month",month)
            intent.putExtra("year",year)
       //     intent.putExtra("full",_fullname)
        //    intent.putExtra("user",_username)
         //   intent.putExtra("pass",_password)
         //   intent.putExtra("email",_email)
            val pairs = arrayOf(
                (Pair<View, String>(backBtn, "transition_back_arrow_btn")),
                Pair<View, String>(next, "transition_next_btn"),
                (Pair<View, String>(login, "transition_login_btn")),
                (Pair<View, String>(titleText, "transition_title_text"))
            )
            val options: ActivityOptions
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp2ndClass, *pairs)
            startActivity(intent, options.toBundle())
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
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp2ndClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        backBtn.setOnClickListener {
            finish()
        }
//        if(savedInstanceState!=null)
//        {
//            var savedGenderID:Int
//            var savedDay:String
//            var savedMonth:String
//            var savedYear:String
//            savedGenderID=savedInstanceState.getInt(Key_gender.toString())
//            savedDay=savedInstanceState.getString(Key_day).toString()
//            savedMonth=savedInstanceState.getString(Key_month).toString()
//            savedYear=savedInstanceState.getString(Key_year).toString()
//            gender!!.check(savedGenderID)
//            date.updateDate(savedYear.toInt(), savedMonth.toInt(), savedDay.toInt())
//        }
    }

    //    override fun onSaveInstanceState(savedInstanceState: Bundle) {
//        savedInstanceState.putInt(Key_gender.toString(),gender!!.checkedRadioButtonId)
//        savedInstanceState.putString(Key_day,date.dayOfMonth.toString())
//        savedInstanceState.putString(Key_month,date.month.toString())
//        savedInstanceState.putString(Key_year,date.year.toString())
//        super.onSaveInstanceState(savedInstanceState)
//    }
    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    //    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        gender!!.check(Key_gender)
//        date.updateDate(Key_year.toInt(), Key_month.toInt(), Key_day.toInt())
//        super.onRestoreInstanceState(savedInstanceState)
//    }
    private fun validateGender(): Boolean {
        if (gender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
    private fun validateAge(): Boolean {
//        var currentYear:Int = Calendar.getInstance().get(Calendar.YEAR)
//        var userAge:Int = date.year
//        var isAgeValid:Int =currentYear- userAge
//        myapp.age=isAgeValid
//        if(isAgeValid<18)
//        {
//            Toast.makeText(this,"You must be 18 years old to be eligible",Toast.LENGTH_SHORT).show()
//            return false
//        }
//        else {
//            return true
//        }
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Adjust for zero-based months
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        var userAge = currentYear - date.year

        // Adjust the age if the birthday hasn't occurred yet this year
        if (currentMonth < date.month + 1 || (currentMonth == date.month + 1 && currentDay < date.dayOfMonth)) {
            userAge--
        }

        if (userAge < 18) {
            Toast.makeText(this, "You must be 18 years old to be eligible", Toast.LENGTH_SHORT).show()
            return false
        }
        //myapp.age = userAge
        return true
    }

}