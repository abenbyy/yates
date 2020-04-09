package com.abencrauz.yates

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.abencrauz.yates.models.RestaurantBooking
import com.abencrauz.yates.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.util.*

class RestaurantBookingActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var bookRef = db.collection("restaurant-bookings")
    private var userRef = db.collection("users")
    private lateinit var restaurantId:String
    private lateinit var restaurantName: String
    private var userId: String? = ""

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


        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", "")
        val intent = intent
        restaurantName = intent.getStringExtra("restaurantName")
        restaurantId = intent.getStringExtra("restaurantId")
        initComponent()
        if(userId != ""){
            userRef.document(userId!!)
                .get().addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.data
                    smToggle.setOnCheckedChangeListener{c: CompoundButton, b : Boolean ->
                        if(b){
                            etName.text = SpannableStringBuilder(user!!["fullname"].toString())
                            etEmail.text = SpannableStringBuilder(user!!["email"].toString())
                        }else{
                            etName.text = SpannableStringBuilder("")
                            etEmail.text = SpannableStringBuilder("")
                        }
                    }
                }
        }




        btnBook.setOnClickListener(View.OnClickListener {
            if(Build.VERSION.SDK_INT < 23){
                bookRestaurant21()
            }else{
                bookRestaurant23()
            }
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            finish()
        })

    }

    fun initComponent(){
        tvRestaurant = findViewById(R.id.tv_restaurant)
        tvRestaurant.text = restaurantName
        smToggle = findViewById(R.id.sm_toggle)
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etSeats = findViewById(R.id.et_seats)

        dpDate = findViewById(R.id.dp_date)
        tpTime = findViewById(R.id.tp_time)

        btnBook = findViewById(R.id.btn_book)
        btnCancel = findViewById(R.id.btn_cancel)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun bookRestaurant23(){
        val date = Date(dpDate.year, dpDate.month, dpDate.dayOfMonth, tpTime.hour, tpTime.minute)
        bookRef.add(RestaurantBooking(
            restaurantId = restaurantId,
            userId = userId,
            name = etName.text.toString(),
            email = etEmail.text.toString(),
            seats = etSeats.text.toString().toInt(),
            time = Timestamp(date.time)
        )).addOnSuccessListener {
            Toast.makeText(this, "Success!!", Toast.LENGTH_LONG)
            Log.d("Booked","via 23 API")
            finish()
        }
    }

    fun bookRestaurant21(){
        val date = Date(dpDate.year, dpDate.month, dpDate.dayOfMonth)
        bookRef.add(RestaurantBooking(
            restaurantId = restaurantId,
            userId = userId,
            name = etName.text.toString(),
            email = etEmail.text.toString(),
            seats = etSeats.text.toString().toInt(),
            time = Timestamp(date.time)
        ))
            .addOnSuccessListener {
                Toast.makeText(this, "Success!!", Toast.LENGTH_LONG)
                Log.d("Booked","via 21 API")
                finish()
            }

    }

}
