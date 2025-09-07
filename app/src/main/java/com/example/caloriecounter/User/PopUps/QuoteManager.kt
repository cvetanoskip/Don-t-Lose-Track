package com.example.caloriecounter.User.PopUps

import android.content.Context
import java.util.*

object QuoteManager {
    private const val PREFS_NAME = "quote_prefs"
    private const val KEY_DATE = "quote_date"
    private const val KEY_COUNT = "quote_count"
    private const val KEY_TIMES = "quote_times"

    fun canShowQuote(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val savedDate = prefs.getInt(KEY_DATE, -1)

        // Reset if new day
        if (today != savedDate) {
            val randomTimes = generateRandomTimes()
            prefs.edit()
                .putInt(KEY_DATE, today)
                .putInt(KEY_COUNT, 0)
                .putStringSet(KEY_TIMES, randomTimes.map { it.toString() }.toSet())
                .apply()
        }

        val count = prefs.getInt(KEY_COUNT, 0)
        if (count >= 2) return false // already shown twice

        val times = prefs.getStringSet(KEY_TIMES, emptySet())!!.map { it.toInt() }
        val hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // If current hour matches one of the random times -> show
        return times.contains(hourNow)
    }

    fun markShown(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_COUNT, 0) + 1
        prefs.edit().putInt(KEY_COUNT, count).apply()
    }

    private fun generateRandomTimes(): List<Int> {
        val rand = Random()
        val time1 = 9 + rand.nextInt(6)   // between 9 and 15
        val time2 = 16 + rand.nextInt(6)  // between 16 and 21
        return listOf(time1, time2)
    }
}
