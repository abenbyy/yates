package com.abencrauz.yates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.abencrauz.yates.models.Hotel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class AddNewHotelActivity : AppCompatActivity() {

    private lateinit var hotelName:TextInputEditText
    private lateinit var hotelAddress:TextInputEditText
    private lateinit var hotelType:TextInputEditText
    private lateinit var openTime:TextInputEditText
    private lateinit var amPm:TextInputEditText
    private lateinit var hotelPrice:TextInputEditText
    private lateinit var longitude:TextInputEditText
    private lateinit var latitude:TextInputEditText
    private lateinit var hotelPhoneNumber:TextInputEditText
    private lateinit var location:TextView
    private lateinit var addBtn:Button
    private lateinit var backBtn:ImageView
    private lateinit var imageHotel:ImageView

    private var urlImageDownload:Uri = Uri.parse("http://www.google.com")

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference.child("hotel-picture")
    private val PICK_IMAGE_REQUEST = 1
    private val uuid = UUID.randomUUID().toString()

    private var addHotel = false

    private lateinit var value:String
    private var urlImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_hotel)

        val sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE)
        value = sharedPreferences.getString("location_city", "").toString()

        initialize()
        initializeButton()
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
        backBtn = findViewById(R.id.back_btn)

        addBtn.setOnClickListener {
            if(checkHotelName()){
                Toast.makeText(this, "Hotel name must be filled", Toast.LENGTH_LONG).show()
            }else if(checkHotelAddress()){
                Toast.makeText(this, "Hotel address must be filled", Toast.LENGTH_LONG).show()
            }else if(!checkHotelType()){
                Toast.makeText(this, "Hotel type must be either Hotel or Villa or Resort", Toast.LENGTH_LONG).show()
            }else if(checkOpenTime()){
                Toast.makeText(this, "Hotel open time must be filled", Toast.LENGTH_LONG).show()
            }else if(!checkAMPM()){
                Toast.makeText(this, " AM or PM must be specified", Toast.LENGTH_LONG).show()
            }else if(checkHotelPrice()){
                Toast.makeText(this, " Hotel price must be filled", Toast.LENGTH_LONG).show()
            }else if(checkLongitude()){
                Toast.makeText(this, " Hotel longitude must be filled", Toast.LENGTH_LONG).show()
            }else if(checkLatitude()){
                Toast.makeText(this, " Hotel latitude must be filled", Toast.LENGTH_LONG).show()
            }else if(checkPhoneNumber()){
                Toast.makeText(this, " Hotel phone number must be filled", Toast.LENGTH_LONG).show()
            }else{
                val idx = HomeActivity.listCityName.indexOf(value)
                val countryId = HomeActivity.listCity[idx].cityId.toInt()

                if(urlImageDownload != Uri.parse("http://www.google.com")){
                    urlImage = urlImageDownload.toString()
                    Log.d("Error", urlImage)
                }

                var hotel = Hotel(
                    hotelName.text.toString(),
                    hotelAddress.text.toString(),
                    countryId,
                    latitude.text.toString().toDouble(),
                    longitude.text.toString().toDouble(),
                    urlImage,
                    hotelType.text.toString(),
                    hotelPhoneNumber.text.toString(),
                    openTime.text.toString() + " " + amPm.text.toString(),
                    hotelPrice.text.toString().toInt()
                )
                val document = (HomeActivity.listHotel.size+1).toString()
                Log.d("document added ", document)
                db.collection("hotels").document(document).set(hotel)
                    .addOnSuccessListener {
                        Toast.makeText(this, "New hotel has been successfully added", Toast.LENGTH_LONG).show()
                        addHotel = true
                        finish()
                    }
            }
        }

        backBtn.setOnClickListener {
            finish()
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
        amPm = findViewById(R.id.hotel_am_pm)
        hotelPrice = findViewById(R.id.hotel_price)
        longitude = findViewById(R.id.hotel_longitude)
        latitude = findViewById(R.id.hotel_latitude)
        location = findViewById(R.id.location_hotel)
        location.text = value
        imageHotel = findViewById(R.id.image_hotel)
        hotelPhoneNumber = findViewById(R.id.hotel_phone_number)
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

    private fun checkOpenTime():Boolean{
        return openTime.text.toString() == ""
    }

    private fun checkHotelPrice():Boolean{
        return openTime.text.toString() == ""
    }

    private fun checkLongitude():Boolean{
        return longitude.text.toString() == ""
    }

    private fun checkLatitude():Boolean{
        return latitude.text.toString() == ""
    }

    private fun checkAMPM():Boolean{
        return (amPm.text.toString() == "AM" || amPm.text.toString() == "PM")
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