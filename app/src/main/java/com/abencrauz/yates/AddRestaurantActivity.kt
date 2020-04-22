package com.abencrauz.yates

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import com.abencrauz.yates.models.Restaurant
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_restaurant.*
import java.util.ArrayList

class AddRestaurantActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var cityRef = db.collection("cities")
    private var restRef = db.collection("restaurants")
    private var storage = Firebase.storage.reference.child("image/")
    val meals = mutableListOf<String>("Breakfast","Brunch","Lunch","Dinner","Tea & Coffe", "Bars & Pubs", "Dessert")
    val types = mutableListOf<String>("Seafood","Steakhouse","Oriental","Vegetarian","Finger Foods", "Others")
    var locations = ArrayList<String>()

    private lateinit var actvMeal: AutoCompleteTextView
    private lateinit var actvType: AutoCompleteTextView
    private lateinit var actvLocation: AutoCompleteTextView

    private lateinit var etName: TextInputEditText
    private lateinit var etHours: TextInputEditText
    private lateinit var etAddress: TextInputEditText

    private lateinit var btnImg: MaterialButton
    private lateinit var btnAdd: MaterialButton
    private lateinit var ivImg: ImageView

    private val REQUEST_CODE = 12
    private var IMG_EXIST = false
    private var IMG : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)

        tp_open.setIs24HourView(true)
        tp_close.setIs24HourView(true)

        initComponents()

        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_item, meals)
        actvMeal.setAdapter(adapter)

        val adapter2 = ArrayAdapter<String>(this, R.layout.dropdown_item, types)
        actvType.setAdapter(adapter2)
        pb.visibility = View.GONE

        cityRef.get()
            .addOnSuccessListener { documents->
                if(documents.size()>0){
                    for(document in documents){
                        locations.add(document.data["name"].toString())
                    }
                    val adapter3 = ArrayAdapter<String>(this, R.layout.dropdown_item, locations)
                    actvLocation.setAdapter(adapter3)
                }
            }

        btnImg.setOnClickListener(View.OnClickListener {
            openGallery()
        })
        btnAdd.setOnClickListener(View.OnClickListener {
            uploadRestaurant()
        })

    }

    fun initComponents(){
        actvMeal = findViewById(R.id.actv_meal)
        actvType = findViewById(R.id.actv_type)
        actvLocation = findViewById(R.id.actv_loc)
        etName = findViewById(R.id.et_name)

        etAddress = findViewById(R.id.et_address)
        btnImg = findViewById(R.id.btn_image)
        btnAdd = findViewById(R.id.btn_add)
        ivImg = findViewById(R.id.iv_image)
    }

    fun uploadRestaurant(){
        pb.visibility = View.VISIBLE
        var imgUri = ""
        if(IMG_EXIST){
            Toast.makeText(this,"Image exist", Toast.LENGTH_LONG)
            var imageRef = storage.child("restaurant-image/${IMG!!.lastPathSegment.toString()}")
            imageRef.putFile(IMG!!)
                .addOnCompleteListener{
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imgUri = uri.toString()
                        var res = Restaurant(
                            name = etName.text.toString(),
                            address = etAddress.text.toString(),
                            location = actvLocation.text.toString(),
                            image = imgUri,
                            hours = tp_open.currentHour.toString()+ ":"+ tp_open.currentMinute.toString() + " - "+tp_close.currentHour.toString()+ ":"+ tp_close.currentMinute.toString(),
                            meal = actvMeal.text.toString(),
                            type = actvType.text.toString(),
                            phone = et_phone.text.toString()
                        )

                        restRef.add(res).addOnSuccessListener {
                            pb.visibility = View.GONE
                            Toast.makeText(this, "Post Success", Toast.LENGTH_LONG)
                            finish()
                        }
                    }
                }
        }else{
            var res = Restaurant(
                name = etName.text.toString(),
                address = etAddress.text.toString(),
                location = actvLocation.text.toString(),
                image = "",
                hours = tp_open.currentHour.toString()+ ":"+ tp_open.currentMinute.toString() + " - "+tp_close.currentHour.toString()+ ":"+ tp_close.currentMinute.toString(),
                meal = actvMeal.text.toString(),
                type = actvType.text.toString(),
                phone = et_phone.text.toString()
            )

            restRef.add(res).addOnSuccessListener {
                pb.visibility = View.GONE
                Toast.makeText(this, "Post Success", Toast.LENGTH_LONG)
                finish()
            }
        }
    }

    fun openGallery(){
        IMG_EXIST = false
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            IMG = data?.data
            ivImg.setImageURI(data?.data)
            IMG_EXIST = true
        }
    }
}
