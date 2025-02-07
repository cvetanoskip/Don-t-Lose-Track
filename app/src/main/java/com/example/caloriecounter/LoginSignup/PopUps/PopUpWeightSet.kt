package com.example.caloriecounter.LoginSignup.PopUps

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
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
import com.example.caloriecounter.LoginSignup.SignUp4thClass
import com.example.caloriecounter.LoginSignup.SignUp7thClass
import com.example.caloriecounter.MyApp
import com.example.caloriecounter.R
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.round

private const val Key_weight_left = "Kg"
private const val Key_weight_right = "Lbs"
private const val Key_weight_choice = "Pounds"

class PopUpWeightSet : DialogFragment() {
    lateinit var spinner: Spinner
    lateinit var stones: TextView
    lateinit var weight1: String
    lateinit var pounds: EditText
    lateinit var lbs: TextView
    lateinit var lbs_edit: EditText
    lateinit var sessionManager: SessionManager
    val df = DecimalFormat("#.#")
    val df1 = DecimalFormat("#")
    var Kilogram = false
    var Kilogram1 = false
    var KilogramValue = "0"
    var KilogramValue1 = "0"
    var PoundValue = "0"
    var PoundValue1 = "0"
    var Pounds = false
    var Pounds1 = false
    var Stones = false
    var Stones1 = false

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_weight, container, false)
        spinner = rootView.findViewById(R.id.spinner)
        stones = rootView.findViewById(R.id.textView2)
        pounds = rootView.findViewById(R.id.editTextNumber)
        lbs_edit = rootView.findViewById(R.id.editTextNumber2)
        pounds.setOnFocusChangeListener { view, b -> pounds.hint = "" }
        lbs_edit.setOnFocusChangeListener { view, b -> lbs_edit.hint = "" }
        val myapp= activity?.application as MyApp
        lbs_edit.text.toString().replace(',', '.')
        lbs = rootView.findViewById(R.id.lbs_text_view)

        sessionManager = SessionManager(requireContext())
        val userDetails = sessionManager.getUsersDetailFromSession()
        if (savedInstanceState != null) {
            var savedWeightChoice: Int
            var savedWeightLeft: String
            var savedWeightRight: String
            savedWeightChoice = savedInstanceState.getInt(Key_weight_choice)
            savedWeightLeft = savedInstanceState.getInt(Key_weight_left).toString()
            savedWeightRight = savedInstanceState.getInt(Key_weight_right).toString()
            spinner.setSelection(savedWeightChoice)
            KilogramValue1 = savedWeightLeft
            PoundValue1 = savedWeightRight
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                weight1 = spinner.selectedItem.toString()
                if (weight1 == "Pounds") {
                    stones.setText("lbs")
                    lbs.visibility = View.INVISIBLE
                    lbs_edit.visibility = View.INVISIBLE

                    if (Stones1 == true) {
                        if (!pounds.text.toString().isEmpty() && !lbs_edit.text.toString()
                                .isEmpty()
                        ) {
                            var StToLbs: Double
                            StToLbs =
                                pounds.text.toString().replace(',', '.').toDouble() * 14 + lbs_edit.text.toString()
                                    .toDouble()
                            pounds.setText(df.format(StToLbs).toString())
                            KilogramValue1 =
                                (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()

                        }
                    }
                    if (Kilogram1 == true) {
                        if (!pounds.text.toString().isEmpty()) {
                            var KgToLbs: Double
                            KgToLbs = (pounds.text.toString().replace(',', '.').toDouble() * 2.20462262)
                            pounds.setText(df.format(KgToLbs).toString())
                            KilogramValue1 =
                                (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()

                        }
                    }
                   Kilogram1 = false
                   Pounds1 = true
                    Stones1 = false

                }
                if (weight1 == "Kilograms") {
                    stones.setText("kg")
                    lbs.visibility = View.INVISIBLE
                    lbs_edit.visibility = View.INVISIBLE

                    if (Pounds1 == true) {
                        if (!pounds.text.toString().isEmpty()) {
                            var lbsToKg: Double
                            lbsToKg = pounds.text.toString().replace(',', '.').toDouble() * 0.45359237
                            pounds.setText(df.format(lbsToKg).toString())
                            KilogramValue1 =
                                (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()

                        }
                    }
                    if (Stones1 == true) {
                        if (!pounds.text.toString().isEmpty() && !lbs_edit.text.toString()
                                .isEmpty()
                        ) {
                            var StToKg: Double
                            StToKg = (pounds.text.toString().replace(',', '.')
                                .toDouble() * 14 + lbs_edit.text.toString()
                                .toDouble()) * 0.45359237
                            pounds.setText(df.format(StToKg).toString())
                            KilogramValue1 =
                                (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()

                        }
                    }
                    Kilogram1 = true
                    Pounds1 = false
                    Stones1 = false
                }
                if (weight1 == "Stones") {
                    stones.setText("st")
                    lbs.visibility = View.VISIBLE
                    lbs_edit.visibility = View.VISIBLE
                    if (Pounds1 == true) {
                        if (!pounds.text.toString().isEmpty()) {
                            var lbsToSt: Int
                            lbsToSt = df1.format(pounds.text.toString().replace(',', '.').toDouble()).toInt() / 14
                            var Remainder: Int
                            Remainder =
                                df1.format(pounds.text.toString().replace(',', '.').toDouble()).toInt() % 14
                            pounds.setText(lbsToSt.toString())
                            lbs_edit.setText(Remainder.toString())
                            KilogramValue1 =
                                (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()

                            PoundValue1 =
                                (df.format(lbs_edit.text.toString().replace(',', '.').toDouble())).toString()

                        }
                    }
                    if (Kilogram1 == true) {
                        if (!pounds.text.toString().isEmpty()) {
                            var KgToSt: Int
                            var KgToLbs: Int
                            KgToSt =
                                df1.format((pounds.text.toString().replace(',', '.').toDouble() * 2.20462262))
                                    .toInt() / 14
                            KgToLbs =
                                df1.format((pounds.text.toString().replace(',', '.').toDouble() * 2.20462262))
                                    .toInt() % 14
                            pounds.setText(KgToSt.toString())
                            lbs_edit.setText(KgToLbs.toString())
                            KilogramValue1 =
                                (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()

                            PoundValue1 =
                                (df.format(lbs_edit.text.toString().replace(',', '.').toDouble())).toString()

                        }
                    }
                    Kilogram1 = false
                    Pounds1 = false
                    Stones1 = true
                }
            }

        }
        rootView.findViewById<Button>(R.id.weight_set_button).setOnClickListener {
            weight1 = spinner.selectedItem.toString()

            // Handling Pounds
            if (weight1 == "Pounds") {
                if (pounds.text.toString().isEmpty()) {
                    pounds.setText("0")
                }

                try {
                    // Replace comma with a period and parse as Double
                    val poundsText = pounds.text.toString().replace(',', '.')

                    // Validate if the number is a valid decimal
                    val poundsValue = poundsText.toDoubleOrNull()

                    if (poundsValue != null && poundsValue >= 44.0) {
                        // Format to 1 decimal place if needed
                        KilogramValue = df.format(poundsValue).toString()
                        myapp.weightlbs = KilogramValue.toInt()
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
                            goal = userDetails[Key_GOAL] ?: "",
                            week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                            height = userDetails[Key_HEIGHT] ?: "",
                            weight = KilogramValue, // Only update weight
                            goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                            speed = userDetails[Key_Speed] ?: "",
                            unitPreference =  "imperial"
                        )
                        val message = "$KilogramValue lbs"
                        (activity as SignUp4thClass?)!!.weight.setText(message)

                        Pounds = true
                        Kilogram = false
                        Stones = false

                        (activity as SignUp4thClass?)!!.Pounds = true
                        (activity as SignUp4thClass?)!!.Kilogram = false
                        (activity as SignUp4thClass?)!!.Stones = false

                        dialog?.dismiss()
                    } else {
                        Toast.makeText(context, "Enter Valid Weight (greater than 44 lbs)", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    // This should catch any unexpected exceptions (NumberFormatException won't trigger)
                    Toast.makeText(context, "Invalid weight value. Please enter a valid number.", Toast.LENGTH_LONG).show()
                }
            }

            // Handling Kilograms
            if (weight1 == "Kilograms") {
                if (pounds.text.toString().isEmpty()) {
                    pounds.setText("0")
                }

                try {
                    // Replace comma with a period and parse as Double
                    val poundsText = pounds.text.toString().replace(',', '.')

                    // Validate if the number is a valid decimal
                    val poundsValue = poundsText.toDoubleOrNull()

                    if (poundsValue != null && poundsValue >= 20.0) {
                        // Format to 1 decimal place if needed
                        KilogramValue = df.format(poundsValue).toString()
                        myapp.weightkg = KilogramValue.toInt()
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
                            goal = userDetails[Key_GOAL] ?: "",
                            week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                            height = userDetails[Key_HEIGHT] ?: "",
                            weight = KilogramValue, // Only update weight
                            goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                            speed = userDetails[Key_Speed] ?: "",
                            unitPreference =  "metric"

                        )
                        val message = "$KilogramValue kg"
                        (activity as SignUp4thClass?)!!.weight.setText(message)

                        Pounds = false
                        Kilogram = true
                        Stones = false

                        (activity as SignUp4thClass?)!!.Pounds = false
                        (activity as SignUp4thClass?)!!.Kilogram = true
                        (activity as SignUp4thClass?)!!.Stones = false

                        dialog?.dismiss()
                    } else {
                        Toast.makeText(context, "Enter Valid Weight (greater than 20 kg)", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    // This should catch any unexpected exceptions (NumberFormatException won't trigger)
                    Toast.makeText(context, "Invalid weight value. Please enter a valid number.", Toast.LENGTH_LONG).show()
                }
            }

            // Handling Stones
            if (weight1 == "Stones") {
                if (pounds.text.toString().isEmpty()) {
                    pounds.setText("0")
                }
                if (lbs_edit.text.toString().isEmpty()) {
                    lbs_edit.setText("0")
                }

                try {
                    // Replace comma with a period and parse as Double for pounds and Int for lbs
                    val poundsText = pounds.text.toString().replace(',', '.')
                    val lbsEditValue = lbs_edit.text.toString().replace(',', '.').toInt()

                    // Validate pounds and lbsEdit for Stones
                    val poundsValue = poundsText.toDoubleOrNull()

                    if (poundsValue != null && poundsValue >= 3 && lbsEditValue in 0..13) {
                        KilogramValue = pounds.text.toString()
                        myapp.weightst = KilogramValue.toInt()

                        PoundValue = lbs_edit.text.toString()
                        myapp.weightlbs = PoundValue.toInt()
                        val convertedToPounds = KilogramValue.toInt() * 14 + PoundValue.toInt()
                        val message = "$KilogramValue st $PoundValue lbs"
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
                            goal = userDetails[Key_GOAL] ?: "",
                            week_goal = userDetails[Key_WEEK_GOAL] ?: "",
                            height = userDetails[Key_HEIGHT] ?: "",
                            weight = convertedToPounds.toString(), // Only update weight
                            goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                            speed = userDetails[Key_Speed] ?: "",
                            unitPreference =  "imperial"
                        )
                        (activity as SignUp4thClass?)!!.weight.setText(message)

                        Pounds = false
                        Kilogram = false
                        Stones = true

                        (activity as SignUp4thClass?)!!.Pounds = false
                        (activity as SignUp4thClass?)!!.Kilogram = false
                        (activity as SignUp4thClass?)!!.Stones = true

                        dialog?.dismiss()
                    } else if (lbsEditValue >= 14) {
                        Toast.makeText(context, "Lbs value should be between 0 and 13", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Enter Valid Weight for Stones", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    // Catching any invalid weight value issues for Stones
                    Toast.makeText(context, "Invalid weight value. Please enter valid numbers.", Toast.LENGTH_LONG).show()
                }
            }
        }





        return rootView
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt(Key_weight_choice, spinner.selectedItemPosition)
        savedInstanceState.putString(Key_weight_left, KilogramValue1)
        savedInstanceState.putString(Key_weight_right, PoundValue1)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onResume() {

        super.onResume()
        pounds.setText(KilogramValue)
        lbs_edit.setText(PoundValue)
        if (Pounds == true)
            spinner.setSelection(0)
        if (Kilogram == true)
            spinner.setSelection(1)

        if (Stones == true)
            spinner.setSelection(2)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.putInt(Key_weight_choice, spinner.selectedItemPosition)
        savedInstanceState?.putString(Key_weight_left, KilogramValue1)
        savedInstanceState?.putString(Key_weight_right, PoundValue1)
    }

    override fun onStart() {
        super.onStart()
        pounds.setText(KilogramValue)
        lbs_edit.setText(PoundValue)
        if (Pounds == true)
            spinner.setSelection(0)
        if (Kilogram == true)
            spinner.setSelection(1)

        if (Stones == true)
            spinner.setSelection(2)
    }
    override fun onStop() {
        super.onStop()
        pounds.setText(KilogramValue)
        lbs_edit.setText(PoundValue)
        if (Pounds == true)
            spinner.setSelection(0)
        if (Kilogram == true)
            spinner.setSelection(1)

        if (Stones == true)
            spinner.setSelection(2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.putInt(Key_weight_choice, spinner.selectedItemPosition)
        savedInstanceState?.putString(Key_weight_left, KilogramValue1)
        savedInstanceState?.putString(Key_weight_right, PoundValue1)
    }
}
