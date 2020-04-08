package com.abencrauz.yates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RestaurantBookingActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var bookRef = db.collection("restaurant-bookings")
    private lateinit var tvRestaurant:TextView

    private lateinit var smToggle: SwitchMaterial
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etSeats: TextInputEditText

    private lateinit var dpDate: DatePicker
    private lateinit var tpTime: TimePicker

    private lateinit var btnBook : MaterialButton
    private lateinit var btnCancel: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_booking)

        initComponent()

        smToggle.setOnCheckedChangeListener{c: CompoundButton, b : Boolean ->
            if(b){
                etName.text = SpannableStringBuilder("Andree Benaya")
                etEmail.text = SpannableStringBuilder("benayaabyatar@gmail.com")
            }else{
                etName.text = SpannableStringBuilder("")
                etEmail.text = SpannableStringBuilder("")
            }
        }

        btnBook.setOnClickListener(View.OnClickListener {

        })

        btnCancel.setOnClickListener(View.OnClickListener {
            finish()
        })

    }

    fun initComponent(){
        tvRestaurant = findViewById(R.id.tv_restaurant)
        smToggle = findViewById(R.id.sm_toggle)
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etSeats = findViewById(R.id.et_seats)

        dpDate = findViewById(R.id.dp_date)
        tpTime = findViewById(R.id.tp_time)

        btnBook = findViewById(R.id.btn_book)
        btnCancel = findViewById(R.id.btn_book)
    }

}
