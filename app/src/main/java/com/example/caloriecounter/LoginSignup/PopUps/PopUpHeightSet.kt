package com.example.caloriecounter.LoginSignup.PopUps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.text.TextUtils
import android.transition.Transition
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
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
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_GOAL_WEIGHT
import com.example.caloriecounter.Databases.SessionManager.Companion.Key_Speed
import com.example.caloriecounter.LoginSignup.SignUp4thClass
import com.example.caloriecounter.MyApp
import com.example.caloriecounter.R
import java.lang.ClassCastException
import java.text.DecimalFormat


class PopUpHeightSet: DialogFragment() {
    lateinit var spinner: Spinner
    lateinit var inch:TextView
    lateinit var ft:TextView
    lateinit var inches:EditText
    lateinit var height1:String
    lateinit var feet:EditText
    lateinit var sessionManager: SessionManager
    val df1 = DecimalFormat("#")

            var Metric =false
            var Metric1 =false
            var MetricValue ="0"
            var ImperialValue="0"


            var Imperial =false
            var Imperial1 =false


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager(requireContext())
        val userDetails = sessionManager.getUsersDetailFromSession()
        val myapp= activity?.application as MyApp
        val rootView: View= inflater.inflate(R.layout.fragment_height,container,false)
        spinner=rootView.findViewById(R.id.spinner)
        inch=rootView.findViewById(R.id.textView3)
        ft=rootView.findViewById(R.id.textView2)
        inches=rootView.findViewById(R.id.editTextNumber2)
        feet=rootView.findViewById(R.id.editTextNumber)
        feet.setOnFocusChangeListener { view, b ->feet.hint="" }
        inches.setOnFocusChangeListener { view, b -> inches.hint="" }
        spinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {


            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                height1=spinner.selectedItem.toString()
                if(height1=="Centimeters")
                {
                    inch.visibility=View.INVISIBLE
                    inches.visibility=View.INVISIBLE
                    ft.setText("cm ")
                    if(Imperial1==true)
                    {
                        if(!feet.text.toString().isEmpty()&&!inches.text.toString().isEmpty()) {
                            var FeetToCM: Int
                            FeetToCM = df1.format(((feet.text.toString().toDouble() * 30.48) + inches.text.toString().toDouble() * 2.54)).toInt()
                            feet.setText(FeetToCM.toString())
                        }
                    }
                    Metric1=true
                    Imperial1=false
                }
                if(height1=="Feet & Inches")
                {
                    inch.visibility=View.VISIBLE
                    inches.visibility=View.VISIBLE
                    ft.setText("ft")
                    if(Metric1==true)
                    {
                        if(!feet.text.toString().isEmpty()){
                            var CMtoFeet: Int
                            var CMtoInches: Int
                            CMtoFeet = (feet.text.toString().toDouble() / 30.48).toInt()
                            CMtoInches = df1.format(((feet.text.toString().toDouble() % 30.48)/2.54)).toInt()
                            feet.setText(CMtoFeet.toString())
                            inches.setText(CMtoInches.toString())
                        }
                    }
                    Metric1=false
                    Imperial1=true

                }
            }
            
        }
       rootView.findViewById<Button>(R.id.height_set_button).setOnClickListener{
           height1=spinner.selectedItem.toString()

//           val intent = Intent(context,SignUp4thClass::class.java)

           if(height1=="Centimeters")
           {
               myapp.isCM=true
               if(feet.text.toString().isEmpty())
                   feet.setText("0")
               if(feet.text.toString().toInt() in 50..320)
               {


                   val message=(feet.text.toString()+" cm")
                   ( activity as SignUp4thClass?)!!.height.setText(message)
                   MetricValue=feet.text.toString()
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
                       height =  MetricValue,
                       weight = userDetails[Key_WEIGHT] ?: "",
                       goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                       speed = userDetails[Key_Speed] ?: "",
                       unitPreference =  "metric"
                   )
                   Metric=true
                   dialog?.dismiss()
               }
               else{
                   Toast.makeText(context,"Enter Valid Height",Toast.LENGTH_LONG).show()
               }
            myapp.heightcm=MetricValue.toInt()
           }
           if(height1=="Feet & Inches")
           {
               myapp.isCM=false
                    if(inches.text.toString().isEmpty())
                        inches.setText("0")
                    if(inches.text.toString().toInt() in 0..11 &&inches.text.toString().trim().length>0&&feet.text.toString().trim().length>0&&feet.text.toString().toInt() in 2..10)
                    {

                        val message=(feet.text.toString()+" ft "+inches.text.toString()+" in")
                        ( activity as SignUp4thClass?)!!.height.setText(message)
                        MetricValue=feet.text.toString()
                        ImperialValue=inches.text.toString()

                        dialog?.dismiss()
                    }
                    else if(inches.text.toString().toInt()>=12)
                    {
                        var inchToFeet:Int
                        var Remainder:Int
                        inchToFeet=inches.text.toString().toInt()/12+feet.text.toString().toInt()
                        Remainder=inches.text.toString().toInt()%12
                        feet.setText(inchToFeet.toString())
                        inches.setText(Remainder.toString())
                        val message=(feet.text.toString()+" ft "+inches.text.toString()+" in")
                        ( activity as SignUp4thClass?)!!.height.setText(message)
                        MetricValue=feet.text.toString()
                        ImperialValue=inches.text.toString()
                        val heightcm=(feet.text.toString().toInt()*12+ImperialValue.toInt())*2.54
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
                            height =  heightcm.toString(),
                            weight = userDetails[Key_WEIGHT] ?: "",
                            goal_weight = userDetails[Key_GOAL_WEIGHT] ?: "",
                            speed = userDetails[Key_Speed] ?: "",
                            unitPreference =  "metric"
                        )
                        dialog?.dismiss()
                    }
                    else if(feet.text.toString().trim().length==0||!(feet.text.toString().toInt() in 2..10) )
                    {
                        Toast.makeText(context,"Enter Valid Height",Toast.LENGTH_LONG).show()
                    }

               myapp.heightft=feet.text.toString().toInt()
               myapp.heightin=inches.text.toString().toInt()

           }




       }

        return rootView
    }
    override fun onResume() {

        super.onResume()
        feet.setText(MetricValue)
        inches.setText(ImperialValue)
        if(Imperial==true)
            spinner.setSelection(0)
        if(Metric==true)
            spinner.setSelection(1)

    }
//    private fun validateHeight(): Boolean {
//        var valueHeight = feet.text.toString().trim()
//        height1 = spinner.selectedItem.toString()
//        if (height1 == "Feet & Inches") {
//            if (valueHeight.toInt() > 10 && valueHeight.toInt() < 1) {
//                feet.error = "Invalid Height"
//                return false
//            } else {
//                feet.error = null
//                return true
//            }
//        } else if (height1 == "Centimeters") {
//            if (valueHeight.toInt() in 50..320) {
//                feet.error = null
//                return true
//            } else {
//                feet.error = "Invalid Height"
//                return false
//            }
//        }
//        return false
//    }

}



