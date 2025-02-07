package com.example.caloriecounter.Databases

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionManager(private val context: Context) {

    private val usersSession: SharedPreferences
    private val editor: SharedPreferences.Editor

    companion object {
        const val IS_LOGIN = "IsLoggedIn"
        const val Key_FULLNAME = "Fullname"
        const val Key_USERNAME = "Username"
        const val Key_EMAIL = "EMAIL"
        const val Key_PASSWORD = "PASSWORD"
        const val Key_PHONENUMBER = "PhoneNumber"
        const val Key_PHONEVERIFICATION = "phoneVerify"
        const val Key_Speed="speed"
        const val Key_DATE = "date"
        const val Key_GENDER = "gender"
        const val Key_ACTIVITY = "activity"
        const val Key_GOAL = "goal"
        const val Key_WEEK_GOAL = "weeklyGoal"
        const val Key_HEIGHT = "height"
        const val Key_WEIGHT = "weight"
        const val Key_GOAL_WEIGHT="goal weight"
        const val Key_LAST_LOGIN = "LastLogin"
        const val Key_UNIT_PREFERENCE = "unitPreference"
    }

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        usersSession = EncryptedSharedPreferences.create(
            context,
            "userLoginSession",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        editor = usersSession.edit()
    }

    fun saveUserDetails(
        fullname: String,
        username: String,
        email: String,
        phoneNo: String,
        password: String,
        age: String,
        gender: String,
        activity: String,
        phoneVerify: String,
        goal: String,
        week_goal: String,
        speed: String,
        height: String,
        weight: String,
        goal_weight: String,
        unitPreference: String
    ) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(Key_FULLNAME, fullname)
        editor.putString(Key_USERNAME, username)
        editor.putString(Key_EMAIL, email)
        editor.putLong(Key_LAST_LOGIN, System.currentTimeMillis())

        //val hashedPassword = hashPassword(password)
        editor.putString(Key_PASSWORD, password)
        editor.putString(Key_PHONENUMBER, phoneNo)
        editor.putString(Key_PHONEVERIFICATION, phoneVerify)
        editor.putString(Key_DATE, age)
        editor.putString(Key_GENDER, gender)
        editor.putString(Key_ACTIVITY, activity)
        editor.putString(Key_GOAL, goal)
        editor.putString(Key_WEEK_GOAL, week_goal)
        editor.putString(Key_HEIGHT, height)
        editor.putString(Key_WEIGHT, weight)
        editor.putString(Key_GOAL_WEIGHT, goal_weight)
        editor.putString(Key_Speed, speed)
        editor.putString(Key_UNIT_PREFERENCE, unitPreference)
        editor.apply()
    }
    fun getLastLogin(): String {
        val timestamp = usersSession.getLong(Key_LAST_LOGIN, 0L)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return if (timestamp != 0L) sdf.format(Date(timestamp)) else "Never"
    }

//    fun hashPassword(password: String): String {
//        return MessageDigest.getInstance("SHA-256")
//            .digest(password.toByteArray())
//            .joinToString("") { "%02x".format(it) }
//    }

    fun getUsersDetailFromSession(): HashMap<String, String> {
        val userData = HashMap<String, String>()
        userData[Key_FULLNAME] = usersSession.getString(Key_FULLNAME, "") ?: ""
        userData[Key_USERNAME] = usersSession.getString(Key_USERNAME, "") ?: ""
        userData[Key_EMAIL] = usersSession.getString(Key_EMAIL, "") ?: ""
        userData[Key_PASSWORD] = usersSession.getString(Key_PASSWORD, "") ?: ""
        userData[Key_PHONENUMBER] = usersSession.getString(Key_PHONENUMBER, "") ?: ""
        userData[Key_PHONEVERIFICATION] = usersSession.getString(Key_PHONEVERIFICATION, "") ?: ""
        userData[Key_DATE] = usersSession.getString(Key_DATE, "") ?: ""
        userData[Key_GENDER] = usersSession.getString(Key_GENDER, "") ?: ""
        userData[Key_ACTIVITY] = usersSession.getString(Key_ACTIVITY, "") ?: ""
        userData[Key_GOAL] = usersSession.getString(Key_GOAL, "") ?: ""
        userData[Key_WEEK_GOAL] = usersSession.getString(Key_WEEK_GOAL, "") ?: ""
        userData[Key_Speed] = usersSession.getString(Key_Speed, "") ?: ""
        userData[Key_HEIGHT] = usersSession.getString(Key_HEIGHT, "") ?: ""
        userData[Key_WEIGHT] = usersSession.getString(Key_WEIGHT, "") ?: ""
        userData[Key_GOAL_WEIGHT] = usersSession.getString(Key_GOAL_WEIGHT, "") ?: ""
        userData[Key_UNIT_PREFERENCE] = usersSession.getString(Key_UNIT_PREFERENCE, "metric") ?: "metric"  // Default to "metric"
        return userData
    }

    fun checkLogin(): Boolean {
        return usersSession.getBoolean(IS_LOGIN, false)
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }
}
