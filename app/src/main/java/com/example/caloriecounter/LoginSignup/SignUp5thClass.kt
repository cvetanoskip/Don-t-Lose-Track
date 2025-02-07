package com.example.caloriecounter.LoginSignup

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.*
import androidx.core.view.WindowCompat
import com.example.caloriecounter.Databases.SessionManager
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
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_USERNAME
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
private const val Key_goal = 1

class SignUp5thClass : AppCompatActivity() {
    var Lose1 = false
    var Maintain1 = false
    var Gain1 = false
    lateinit var backBtn: ImageView
    lateinit var next: Button
    lateinit var login: Button
    lateinit var titleText: TextView
    lateinit var goal: RadioGroup
    lateinit var Lose: RadioButton
    lateinit var Maintain: RadioButton
    lateinit var Gain: RadioButton
    lateinit var sessionManager: SessionManager
    var Kilogram = false
    var Pounds = false
    var Stones = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up5th_class)
        backBtn = findViewById(R.id.signup_back_button)
        next = findViewById(R.id.signup_next_button)
        login = findViewById(R.id.signup_login_button)
        titleText = findViewById(R.id.signup_title_text)
        goal = findViewById(R.id.radio_goal)
        Lose = findViewById(R.id.lose_weight)
        Gain = findViewById(R.id.gain_weight)
        Maintain = findViewById(R.id.maintain_weight)
        sessionManager = SessionManager(this)
        val userDetails = sessionManager.getUsersDetailFromSession()
        Kilogram = intent.getBooleanExtra("kilogram", false)
        Pounds = intent.getBooleanExtra("pounds", false)
        Stones = intent.getBooleanExtra("stones", false)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = this.resources.getColor(R.color.transparent)
        Lose.setOnClickListener {
            Lose1 = true
            Maintain1 = false
            Gain1 = false
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal =  "lose",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = userDetails[Key_Speed] ?: "",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        Maintain.setOnClickListener {
            Lose1 = false
            Maintain1 = true
            Gain1 = false
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal =  "maintain",
                week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                height = userDetails[Key_HEIGHT] ?: "",
                weight = userDetails[Key_WEIGHT] ?: "",
                goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                speed = userDetails[Key_Speed] ?: "",
                unitPreference = userDetails[Key_UNIT_PREFERENCE] ?: ""
            )
        }
        Gain.setOnClickListener {
            Lose1 = false
            Maintain1 = false
            Gain1 = true
            sessionManager.saveUserDetails(
                fullname = userDetails[Key_FULLNAME] ?: "",
                username = userDetails[Key_USERNAME] ?: "",
                email = userDetails[Key_EMAIL] ?: "",
                phoneNo = userDetails[Key_PHONENUMBER] ?: "",
                password = userDetails[Key_PASSWORD] ?: "",
                age = userDetails[Key_DATE] ?: "",
                gender = userDetails[Key_GENDER] ?: "",
                activity = userDetails[Key_ACTIVITY] ?: "",
                phoneVerify = userDetails[Key_PHONEVERIFICATION] ?: "",
                goal =  "gain",
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
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp5thClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        next.setOnClickListener {
            if (!validateGoal()) {
                return@setOnClickListener
            }
            val intent = Intent(this, SignUp6thClass::class.java)
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
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp5thClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        if (savedInstanceState != null) {
            var savedGoalID: Int

            savedGoalID = savedInstanceState.getInt(Key_goal.toString())

            goal!!.check(savedGoalID)

        }
        backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt(Key_goal.toString(), goal!!.checkedRadioButtonId)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun validateGoal(): Boolean {
        if (goal.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please Select An Option", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
}