package com.example.caloriecounter.LoginSignup.PopUps

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.caloriecounter.LoginSignup.SignUp7thClass
import com.example.caloriecounter.R
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
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import java.text.DecimalFormat
private const val Key_weight_left="Kg"
private const val Key_weight_right="Lbs"
private const val Key_weight_choice="Pounds"
class PopUpGoalWeightSet: DialogFragment() {
    lateinit var spinner: Spinner
    lateinit var stones: TextView
    lateinit var weight1:String
    lateinit var pounds: EditText
    lateinit var lbs:TextView
    lateinit var lbs_edit:EditText
    lateinit var sessionManager: SessionManager
    val df = DecimalFormat("#.#")
    val df1 = DecimalFormat("#")

            var Kilogram =false
            var Kilogram1 =false
            var KilogramValue ="0"
            var KilogramValue1 ="0"
            var PoundValue="0"
            var PoundValue1="0"
            var Pounds =false
            var Pounds1 =false
            var Stones =false
            var Stones1 =false
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_weight,container,false)
        sessionManager = SessionManager(requireContext())
        val userDetails = sessionManager.getUsersDetailFromSession()
        spinner=rootView.findViewById(R.id.spinner)
        stones=rootView.findViewById(R.id.textView2)
        pounds=rootView.findViewById(R.id.editTextNumber)
        lbs_edit=rootView.findViewById(R.id.editTextNumber2)
        pounds.setOnFocusChangeListener { view, b ->pounds.hint="" }
        lbs_edit.setOnFocusChangeListener { view, b ->lbs_edit.hint="" }

        lbs=rootView.findViewById(R.id.lbs_text_view)

            stones.setText("Insert Goal Weight")
            if((activity as SignUp7thClass?)!!.Kilogram==true)
            {
                spinner.setSelection(1)
            }
            else if((activity as SignUp7thClass?)!!.Pounds==true)
            {
                spinner.setSelection(0)
            }
            else if((activity as SignUp7thClass?)!!.Stones==true)
            {

                spinner.setSelection(2)
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
                        if (!pounds.text.toString().isEmpty() || !lbs_edit.text.toString()
                                .isEmpty()
                        ) {
                            var StToLbs: Double
                            StToLbs = pounds.text.toString().replace(',', '.').toDouble() * 14 + lbs_edit.text.toString().toDouble()
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
                           KilogramValue1 = (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()

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
                                .toDouble() * 14 + lbs_edit.text.toString().replace(',', '.')
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
                            lbsToSt = df1.format(pounds.text.toString().toDouble()).toInt() / 14
                            var Remainder: Int
                            Remainder =
                                df1.format(pounds.text.toString().toDouble()).toInt() % 14
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
                                df1.format((pounds.text.toString().toDouble() * 2.20462262))
                                    .toInt() / 14
                            KgToLbs =
                                df1.format((pounds.text.toString().toDouble() * 2.20462262))
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
            if (weight1 == "Pounds") {
                if (pounds.text.toString().isEmpty())
                    pounds.setText("0")
                if (pounds.text.toString().replace(',', '.').toDouble() >= 44.0) {
                    KilogramValue =
                        (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()
                    val message =
                        ((df.format(pounds.text.toString().replace(',', '.').toDouble())).toString() + " lbs")
                    (activity as SignUp7thClass?)!!.weight_goal.setText(message)
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
                        weight = userDetails[Key_WEIGHT] ?: "", // Only update weight
                        goal_weight = KilogramValue.toString(),
                        speed = userDetails[Key_Speed] ?: "",
                        unitPreference =  "imperial"
                    )
                    Pounds = true
                    Kilogram = false
                    Stones = false
                    if((activity as SignUp7thClass?)!!.Lose1==true)
                    {
                            (activity as SignUp7thClass?)!!.lose_slow.setText("Lose 0.5 lbs Per Week")
                            (activity as SignUp7thClass?)!!.Recommended.setText("Lose 1 lbs Per Week\nRecommended")
                            (activity as SignUp7thClass?)!!.lose.setText("Lose 1.5 lbs Per Week")
                            (activity as SignUp7thClass?)!!.lose_fast.setText("Lose 2 lbs Per Week")
                    }
                    else if((activity as SignUp7thClass?)!!.Gain1==true)
                    {
                        (activity as SignUp7thClass?)!!.lose_slow.setText("Gain 1 lbs Per Week")
                        (activity as SignUp7thClass?)!!.lose.setText("Gain 2 lbs Per Week")
                        (activity as SignUp7thClass?)!!.Recommended.visibility=View.GONE
                        (activity as SignUp7thClass?)!!.lose_fast.visibility=View.GONE
                    }
                    dialog?.dismiss()
                } else {
                    Toast.makeText(context, "Enter Valid Weight", Toast.LENGTH_LONG).show()
                }

            }
            if (weight1 == "Kilograms") {
                if (pounds.text.toString().isEmpty())
                    pounds.setText("0")
                if (pounds.text.toString().replace(',', '.').toDouble() >= 20.0) {
                    KilogramValue =
                        (df.format(pounds.text.toString().replace(',', '.').toDouble())).toString()
                    val message =
                        ((df.format(pounds.text.toString().replace(',', '.').toDouble())).toString() + " kg")
                    (activity as SignUp7thClass?)!!.weight_goal.setText(message)
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
                        weight = userDetails[Key_WEIGHT] ?: "", // Only update weight
                        goal_weight =  KilogramValue.toString(),
                        speed = userDetails[Key_Speed] ?: "",
                        unitPreference =  "metric"
                    )
                    Pounds = false
                    Kilogram = true
                    Stones = false
                    if((activity as SignUp7thClass?)!!.Lose1==true)
                    {
                        (activity as SignUp7thClass?)!!.lose_slow.setText("Lose 0.25 kg Per Week")
                        (activity as SignUp7thClass?)!!.Recommended.setText("Lose 0.5 kg Per Week\nRecommended")
                        (activity as SignUp7thClass?)!!.lose.setText("Lose 0.75 kg Per Week")
                        (activity as SignUp7thClass?)!!.lose_fast.setText("Lose 1 kg Per Week")
                    }
                    else if((activity as SignUp7thClass?)!!.Gain1==true)
                    {
                        (activity as SignUp7thClass?)!!.lose_slow.setText("Gain 0.5 kg Per Week")
                        (activity as SignUp7thClass?)!!.lose.setText("Gain 1 kg Per Week")
                        (activity as SignUp7thClass?)!!.Recommended.visibility=View.GONE
                        (activity as SignUp7thClass?)!!.lose_fast.visibility=View.GONE
                    }
                    dialog?.dismiss()
                } else {
                    Toast.makeText(context, "Enter Valid Weight", Toast.LENGTH_LONG).show()
                }


            }
            if (weight1 == "Stones") {
                if (pounds.text.toString().isEmpty())
                    pounds.setText("0")
                if (lbs_edit.text.toString().isEmpty())
                    lbs_edit.setText("0")
                if (pounds.text.toString().toInt() >= 3 && lbs_edit.text.toString()
                        .toInt() in 0..13
                ) {
                    KilogramValue = pounds.text.toString()
                    PoundValue = lbs_edit.text.toString()
                    val convertedToPounds = KilogramValue.toInt() * 14 + PoundValue.toInt()
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
                        weight = userDetails[Key_WEIGHT] ?: "", // Only update weight
                        goal_weight =  convertedToPounds.toString(),
                        speed = userDetails[Key_Speed] ?: "",
                        unitPreference =  "imperial"
                    )
                    val message =
                        (pounds.text.toString() + " st " + lbs_edit.text.toString() + " lbs")
                    (activity as SignUp7thClass?)!!.weight_goal.setText(message)
                    Pounds = false
                    Kilogram = false
                    Stones = true
                    if((activity as SignUp7thClass?)!!.Lose1==true)
                    {
                        (activity as SignUp7thClass?)!!.lose_slow.setText("Lose 0.5 lbs Per Week")
                        (activity as SignUp7thClass?)!!.Recommended.setText("Lose 1 lbs Per Week\nRecommended")
                        (activity as SignUp7thClass?)!!.lose.setText("Lose 1.5 lbs Per Week")
                        (activity as SignUp7thClass?)!!.lose_fast.setText("Lose 2 lbs Per Week")
                    }
                    else if((activity as SignUp7thClass?)!!.Gain1==true)
                    {
                        (activity as SignUp7thClass?)!!.lose_slow.setText("Gain 1 lbs Per Week")
                        (activity as SignUp7thClass?)!!.lose.setText("Gain 2 lbs Per Week")
                        (activity as SignUp7thClass?)!!.Recommended.visibility=View.GONE
                        (activity as SignUp7thClass?)!!.lose_fast.visibility=View.GONE
                    }
                    dialog?.dismiss()
                } else if (lbs_edit.text.toString().toInt() >= 14) {
                    var lbstoSt: Int
                    var SttoLbs: Int
                    var St = pounds.text.toString().toInt()
                    var lbs: Int
                    lbstoSt = lbs_edit.text.toString().toInt() / 14
                    SttoLbs = lbs_edit.text.toString().toInt() % 14
                    St += lbstoSt
                    lbs = SttoLbs
                    KilogramValue = St.toString()
                    PoundValue = lbs.toString()
                    val message = (St.toString() + " st " + lbs.toString() + " lbs")
                    (activity as SignUp7thClass?)!!.weight_goal.setText(message)
                    if((activity as SignUp7thClass?)!!.Lose1==true)
                    {
                        (activity as SignUp7thClass?)!!.lose_slow.setText("Lose 0.5 lbs Per Week")
                        (activity as SignUp7thClass?)!!.Recommended.setText("Lose 1 lbs Per Week\nRecommended")
                        (activity as SignUp7thClass?)!!.lose.setText("Lose 1.5 lbs Per Week")
                        (activity as SignUp7thClass?)!!.lose_fast.setText("Lose 2 lbs Per Week")
                    }
                    else if((activity as SignUp7thClass?)!!.Gain1==true)
                    {
                        (activity as SignUp7thClass?)!!.lose_slow.setText("Gain 1 lbs Per Week")
                        (activity as SignUp7thClass?)!!.lose.setText("Gain 2 lbs Per Week")
                        (activity as SignUp7thClass?)!!.Recommended.visibility=View.GONE
                        (activity as SignUp7thClass?)!!.lose_fast.visibility=View.GONE
                    }
                    Pounds = false
                    Kilogram = false
                    Stones = true
                    dialog?.dismiss()

                } else if (pounds.text.toString().toInt() < 3) {
                    Toast.makeText(context, "Enter Valid Weight", Toast.LENGTH_LONG).show()
                }


            }
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




    }
        return rootView
}
override fun onSaveInstanceState(savedInstanceState: Bundle) {
    savedInstanceState.putInt(Key_weight_choice,spinner.selectedItemPosition)
    savedInstanceState.putString(Key_weight_left,KilogramValue1)
    savedInstanceState.putString(Key_weight_right,PoundValue1)
    super.onSaveInstanceState(savedInstanceState)
}
override fun onResume() {

    super.onResume()
    pounds.setText(KilogramValue)
    lbs_edit.setText(PoundValue)
    if(Pounds==true)
        spinner.setSelection(0)
    if(Kilogram==true)
        spinner.setSelection(1)

    if(Stones==true)
        spinner.setSelection(2)
}

}
