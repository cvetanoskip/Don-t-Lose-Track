package com.example.caloriecounter.LoginSignup

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.*
import androidx.core.view.WindowCompat
import com.example.caloriecounter.MainActivity
import com.example.caloriecounter.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hbb20.CountryCodePicker

private const val Key_code="Country Code"
private const val Key_country="Country name"
private const val Key_phone="Phone Number"

class SignUp3rdClass : AppCompatActivity() {
     lateinit var backBtn: ImageView
     lateinit var next: Button
     lateinit var login: Button
     lateinit var titleText: TextView
    lateinit var region: CountryCodePicker
    lateinit var phone: TextInputEditText
    lateinit var verification_group: RadioGroup
    lateinit var selectedOptionV:RadioButton
    lateinit var yes_btn: RadioButton
    lateinit var no_btn: RadioButton
    lateinit var phoneLayout: LinearLayout
    lateinit var phoneNo:TextInputLayout
    lateinit var phone_verification:TextView
    class Phone {
        companion object {
            var PhoneNumber="PhoneNumber"
            var PhoneCode="Code"
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3rd_class)
        backBtn = findViewById(R.id.signup_back_button)
        next = findViewById(R.id.signup_next_button)
        phoneNo = findViewById(R.id.Phone_number_layout)
        login = findViewById(R.id.signup_login_button)
        titleText = findViewById(R.id.signup_title_text)
        phone_verification=findViewById(R.id.Verification_survey)
        region=findViewById(R.id.country)
        phone=findViewById(R.id.insert_phone)
        verification_group=findViewById(R.id.radio_phone_verification)
        yes_btn=findViewById(R.id.verify_phone_yes)
        no_btn=findViewById(R.id.verify_phone_no)
        phoneLayout=findViewById(R.id.Insert_phone_layout)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor=this.resources.getColor(R.color.transparent)
        yes_btn.setOnClickListener{
            phoneLayout.visibility=View.VISIBLE
            yes_btn.isChecked=true
        }
         no_btn.setOnClickListener{
            phoneLayout.visibility=View.GONE
             no_btn.isChecked=true
        }
         if(no_btn.isChecked==false&&yes_btn.isChecked==false)
        {
            phoneLayout.visibility=View.GONE
        }
        if(MakeSelection.Sms.BtnClicked==true)
        {
            titleText.setText("Enter\nPhone")
            phone_verification.visibility=View.GONE
            verification_group.visibility=View.GONE
            phoneLayout.visibility=View.VISIBLE
            login.visibility=View.GONE
        }
        next.setOnClickListener {

            if(!validateVerification())
            {
                return@setOnClickListener
            }
            else
            {
                selectedOptionV=findViewById(verification_group.checkedRadioButtonId)
                var optionS:String=selectedOptionV.text.toString()
               if(optionS=="Yes") {
                    if (!validatePhoneNo()) {
                        return@setOnClickListener
                    } else {
                        if (yes_btn.isChecked == true && no_btn.isChecked == false) {
                            var genderS: String? =intent.getStringExtra("gender")
                            var day:Int=intent.getIntExtra("day",1)
                            var month:Int=intent.getIntExtra("month",1)
                            var year:Int=intent.getIntExtra("year",1)
                            var _fullname:String
                            var _username:String
                            var _password:String
                            var _email:String
                            var _phoneNo:String=" +" + region!!.selectedCountryCode + phone.text.toString().trim()
                            _fullname= intent.getStringExtra("full").toString()
                            _username= intent.getStringExtra("user").toString()
                            _password= intent.getStringExtra("pass").toString()
                            _email= intent.getStringExtra("email").toString()
                            val intent = Intent(this, VerifyPhone::class.java)
                            intent.putExtra("gender",genderS)
                            intent.putExtra("day",day)
                            intent.putExtra("month",month)
                            intent.putExtra("year",year)
                            intent.putExtra("full",_fullname)
                            intent.putExtra("user",_username)
                            intent.putExtra("pass",_password)
                            intent.putExtra("email",_email)
                            intent.putExtra("phoneNo",_phoneNo)
                            Phone.PhoneNumber = " +" + region!!.selectedCountryCode + phone.text.toString().trim()
                            startActivity(intent)
                        }
                    }
                }
               else if(optionS=="No") {
                   var genderS: String? =intent.getStringExtra("gender")
                   var day:Int=intent.getIntExtra("day",1)
                   var month:Int=intent.getIntExtra("month",1)
                   var year:Int=intent.getIntExtra("year",1)
                   var _fullname:String
                   var _username:String
                   var _password:String
                   var _email:String
                   _fullname= intent.getStringExtra("full").toString()
                   _username= intent.getStringExtra("user").toString()
                   _password= intent.getStringExtra("pass").toString()
                   _email= intent.getStringExtra("email").toString()
                   val intent = Intent(this, SignUp4thClass::class.java)
                   intent.putExtra("gender",genderS)
                   intent.putExtra("day",day)
                   intent.putExtra("month",month)
                   intent.putExtra("year",year)
                   intent.putExtra("full",_fullname)
                   intent.putExtra("user",_username)
                   intent.putExtra("pass",_password)
                   intent.putExtra("email",_email)
                   val pairs = arrayOf(
                       (Pair<View, String>(backBtn, "transition_back_arrow_btn")),
                       Pair<View, String>(next, "transition_next_btn"),
                       (Pair<View, String>(login, "transition_login_btn")),
                       (Pair<View, String>(titleText, "transition_title_text"))
                   )
                   val options: ActivityOptions
                   options = ActivityOptions.makeSceneTransitionAnimation(
                       this@SignUp3rdClass,
                       *pairs
                   )
                   startActivity(intent, options.toBundle())
               }
                else if(MakeSelection.Sms.BtnClicked == true)
            {
                val intent = Intent(this, VerifyPhone::class.java)
                Phone.PhoneNumber =
                    " +" + region!!.selectedCountryCode + phone.text.toString()
                startActivity(intent)
            }

            }

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
            options = ActivityOptions.makeSceneTransitionAnimation(this@SignUp3rdClass, *pairs)
            startActivity(intent, options.toBundle())
        }
        backBtn.setOnClickListener {
            finish()

        }
        if(savedInstanceState!=null)
        {
            var savedCountryCode:String
            var savedCountryName:String
            var savedPhone:String

            savedCountryCode=savedInstanceState.getString(Key_code).toString()
            savedCountryName=savedInstanceState.getString(Key_country).toString()
            savedPhone=savedInstanceState.getString(Key_phone).toString()
           region!!.setDefaultCountryUsingNameCode(savedCountryName)
            region!!.resetToDefaultCountry()
            Phone.PhoneNumber=" +"+savedCountryCode+savedPhone
            phone.setText(savedPhone)
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString(Key_country,region!!.selectedCountryNameCode)
        savedInstanceState.putString(Key_phone,phone.text.toString())
        savedInstanceState.putString(Key_code,region!!.selectedCountryCode)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }
    private fun validateVerification(): Boolean {
        if (verification_group.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please Select An Option", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
    private fun validatePhoneNo(): Boolean {
        phoneNo.editText?.setText(phoneNo.editText?.text.toString().replace("\\s".toRegex(), ""))
        phoneNo.editText?.setText(phoneNo.editText?.text.toString().replace("-", ""))
        var valuePhone = phoneNo.editText?.text.toString().trim()
        val checkPhone: String = "^[0-9]{1,20}$"
        if (valuePhone.isEmpty()) {
            phoneNo.error = "Field can not be empty"
            return false
        } else if (valuePhone.length > 20) {
            phoneNo.error = "Phone Number is too large"
            return false
        } else if (!valuePhone.matches(checkPhone.toRegex())) {
            phoneNo.error = "Invalid Phone Number"
            return false
        } else {
            phoneNo.error = null
            phoneNo.isErrorEnabled = false
            return true
        }
    }
}