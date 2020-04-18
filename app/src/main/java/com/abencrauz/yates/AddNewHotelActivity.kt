package com.abencrauz.yates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.abencrauz.yates.models.Hotel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_new_hotel.*
import java.util.*

class AddNewHotelActivity : AppCompatActivity() {

    private lateinit var hotelName:TextInputEditText
    private lateinit var hotelAddress:TextInputEditText
    private lateinit var hotelType:AutoCompleteTextView
    private lateinit var openTime:TimePicker
    private lateinit var hotelPrice:TextInputEditText
    private lateinit var hotelPhoneNumber:TextInputEditText
    private lateinit var location:TextView
    private lateinit var addBtn:Button
    private lateinit var imageHotel:ImageView

    val types = mutableListOf("Hotel","Villa","Resort")

    private var urlImageDownload:Uri = Uri.parse("http://www.google.com")

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference.child("hotel-picture")
    private val PICK_IMAGE_REQUEST = 1
    private val uuid = UUID.randomUUID().toString()

    private lateinit var progressBar:ProgressBar

    private var addHotel = false

    private lateinit var value:String
    private var urlImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_hotel)

        val sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE)
        value = sharedPreferences.getString("location_city", "").toString()

        initialize()
        initializeAutoComplete()
        initializeButton()
    }

    private fun initializeAutoComplete(){
        val adapter2 = ArrayAdapter(this, R.layout.dropdown_item, types)
        hotelType.setAdapter(adapter2)
    }

    private fun getPicture(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            val imageData = data.data!!
            val imageRef = storageRef.child(uuid)
            imageRef.putFile(imageData)
                .addOnSuccessListener {
                    Log.e("Success Image", uuid)
                }.addOnFailureListener{
                    Log.e("Fail Image", uuid)
                }.addOnCompleteListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        urlImageDownload = uri
                        Picasso.get().load(uri).into(imageHotel)
                    }
                }
        }
    }

    private fun initializeButton(){
        addBtn = findViewById(R.id.add_btn)

        addBtn.setOnClickListener {
            if(checkHotelName()){
                Toast.makeText(this, "Hotel name must be filled", Toast.LENGTH_LONG).show()
            }else if(checkHotelAddress()){
                Toast.makeText(this, "Hotel address must be filled", Toast.LENGTH_LONG).show()
            }else if(!checkHotelType()){
                Toast.makeText(this, "Hotel type must be either Hotel or Villa or Resort", Toast.LENGTH_LONG).show()
            }else if(checkHotelPrice()){
                Toast.makeText(this, " Hotel price must be filled", Toast.LENGTH_LONG).show()
            }else if(checkPhoneNumber()){
                Toast.makeText(this, " Hotel phone number must be filled", Toast.LENGTH_LONG).show()
            }else{
                progressBar.visibility = View.VISIBLE
                addBtn.visibility = View.GONE
                val idx = HomeActivity.listCityName.indexOf(value)
                val countryId = HomeActivity.listCity[idx].cityId.toInt()

                if(urlImageDownload != Uri.parse("http://www.google.com")){
                    urlImage = urlImageDownload.toString()
                    Log.d("Error", urlImage)
                }else{
                    urlImage = ""
                }

                var hour = ""
                var minute = ""
                var amOrpm = ""

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(openTime.hour > 12){
                        if((openTime.hour - 12) < 10)
                            hour = "0"+(openTime.hour-12)
                        else
                            hour = (openTime.hour-12).toString()
                        amOrpm = "PM"
                    }else{
                        hour = openTime.hour.toString()
                        amOrpm = "AM"
                    }

                    if(openTime.minute < 10){
                        minute = "0"+ openTime.minute
                    }else{
                        minute = openTime.minute.toString()
                    }
                }

                val hotelOpenTime = "${hour}:${minute} $amOrpm"

                var hotel = Hotel(
                    hotelName.text.toString(),
                    hotelAddress.text.toString(),
                    countryId,
                    0.0,
                    0.0,
                    urlImage,
                    hotelType.text.toString(),
                    hotelPhoneNumber.text.toString(),
                    hotelOpenTime,
                    hotelPrice.text.toString().toInt()
                )
                val document = (HomeActivity.listHotel.size+1).toString()
                Log.d("document added ", document)
                db.collection("hotels").document(document).set(hotel)
                    .addOnSuccessListener {
                        HomeActivity.listHotelBaseLocation.add(hotel)
                        Toast.makeText(this, "New hotel has been successfully added", Toast.LENGTH_LONG).show()
                        addHotel = true
                        finish()
                    }
            }
        }

        imageHotel.setOnClickListener {
            getPicture()
        }

    }

    private fun initialize(){
        hotelName = findViewById(R.id.hotel_name)
        hotelAddress = findViewById(R.id.hotel_address)
        hotelType = findViewById(R.id.hotel_type)
        openTime = findViewById(R.id.hotel_open_time)
        openTime.setIs24HourView(true)
        hotelPrice = findViewById(R.id.hotel_price)
        location = findViewById(R.id.location_hotel)
        location.text = value
        imageHotel = findViewById(R.id.image_hotel)
        hotelPhoneNumber = findViewById(R.id.hotel_phone_number)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun checkHotelName():Boolean{
        return hotelName.text.toString() == ""
    }

    private fun checkHotelAddress():Boolean{
        return hotelAddress.text.toString() == ""
    }

    private fun checkHotelType():Boolean{
        return (hotelType.text.toString() == "Hotel" || hotelType.text.toString() == "Villa" || hotelType.text.toString() == "Resort")
    }

    private fun checkHotelPrice():Boolean{
        return hotelPrice.text.toString() == ""
    }

    private fun checkPhoneNumber():Boolean{
        return hotelPhoneNumber.text.toString() == ""
    }

    override fun onStop() {
        super.onStop()
        if(!addHotel){
            val imageRef = storageRef.child(uuid)
            imageRef.delete()
        }
    }

}