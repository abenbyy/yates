package com.abencrauz.yates

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.abencrauz.yates.models.HotelBookings
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HotelBookingActivity : AppCompatActivity() {

    private lateinit var toggleAutoFill:SwitchMaterial
    private lateinit var fullNameET : TextInputEditText
    private lateinit var emailET : TextInputEditText
    private lateinit var nightsET : TextInputEditText
    private lateinit var bookHotelBtn : Button
    private lateinit var backBtn : ImageView
    private lateinit var fullNameTL : TextInputLayout
    private lateinit var emailTL : TextInputLayout
    private lateinit var nightTL : TextInputLayout

    private val db = Firebase.firestore
    private var hotelId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_booking)

        getIncomeContent()
        initializeComponent()
        setToggleListener()
        setButtonListener()
    }

    private fun getIncomeContent(){
        if(intent.hasExtra("hotel_id"))
            hotelId = intent.getStringExtra("hotel_id")
    }

    private fun initializeComponent(){
        toggleAutoFill = findViewById(R.id.toggle_auto_fill)
        fullNameET = findViewById(R.id.fullname_et)
        emailET = findViewById(R.id.email_et)
        nightsET = findViewById(R.id.nights_et)
        bookHotelBtn = findViewById(R.id.book_btn)
        backBtn = findViewById(R.id.back_btn)
        fullNameTL = findViewById(R.id.fullname_tl)
        emailTL = findViewById(R.id.email_tl)
        nightTL = findViewById(R.id.nights_tl)
    }

    private fun setToggleListener(){
        toggleAutoFill.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                fullNameET.setText(HomeActivity.users.fullname)
                emailET.setText(HomeActivity.users.email)
            }else{
                fullNameET.setText("")
                emailET.setText("")
            }
        }
    }

    private fun setButtonListener(){
        bookHotelBtn.setOnClickListener {
            if(!validateFullname())
                fullNameTL.error = "Name minimum length must be 3 character"
            else
                fullNameTL.error = null

            if(!validateEmail())
                emailTL.error = "Filled the email"
            else
                emailTL.error = null

            if(!validateNight())
                nightTL.error = "Minimal 1 night"
            else
                nightTL.error = null

            if(validateFullname() && validateEmail() && validateNight()){
                val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("user_id", "")
                var hotelBook = HotelBookings(
                    userId!!,
                    fullNameET.text.toString(),
                    emailET.text.toString(),
                    nightsET.text.toString().toInt(),
                    hotelId,
                    Timestamp.now()
                )
                db.collection("hotel-bookings").add(hotelBook)
                    .addOnSuccessListener {
                        Toast.makeText(this, "The hotel has been successfully booked", Toast.LENGTH_LONG).show()
                        finish()
                    }
            }
        }
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun validateFullname():Boolean{
        return fullNameET.text.toString().length >= 3
    }

    private fun validateEmail():Boolean{
        val email = emailET.text.toString()
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateNight():Boolean{
        return nightsET.text.toString().toInt() > 0
    }

}
