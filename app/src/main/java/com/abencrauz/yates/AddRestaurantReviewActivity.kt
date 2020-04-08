package com.abencrauz.yates

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.abencrauz.yates.models.Review
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddRestaurantReviewActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var revRef = db.collection("user-reviews")
    private var storage = Firebase.storage.reference.child("image/")

    private lateinit var restaurantId:String
    private lateinit var restaurantName: String

    private lateinit var tvRestaurant: TextView
    private lateinit var rbRating: RatingBar

    private lateinit var ivImage: ImageView

    private lateinit var btnImage: MaterialButton
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnAdd: MaterialButton

    private val REQUEST_CODE = 12
    private var IMG_EXIST = false
    private var IMG : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant_review)
        IMG_EXIST = false

        val intent = intent

        restaurantId = intent.getStringExtra("restaurantId")
        restaurantName = intent.getStringExtra("restaurantName")
        initComponents()

        tvRestaurant.text = restaurantName

        btnImage.setOnClickListener(View.OnClickListener {
            openGallery()
        })

        btnAdd.setOnClickListener(View.OnClickListener {
            uploadReview()
        })
    }

    fun initComponents(){
        tvRestaurant = findViewById(R.id.tv_restaurant)
        rbRating = findViewById(R.id.rb_rating)

        ivImage = findViewById(R.id.iv_image)
        btnImage = findViewById(R.id.btn_image)

        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)

        btnAdd = findViewById(R.id.btn_add)


    }

    fun uploadReview(){
        var imgUri= ""
        if(IMG_EXIST){
            Toast.makeText(this,"Image exist",Toast.LENGTH_LONG)
            var imageRef = storage.child("review-image/${IMG!!.lastPathSegment.toString()}")
            imageRef.putFile(IMG!!)
                .addOnCompleteListener{
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imgUri = uri.toString()
                        var rev = Review(
                            hotelId = "",
                            restaurantId = restaurantId,
                            rating = rbRating.rating.toInt(),
                            timeReview = Timestamp.now(),
                            image = imgUri,
                            userId = "Ofnat05Ddal8mSJyblc8",
                            title = etTitle.text.toString(),
                            description = etDescription.text.toString()
                        )

                        revRef.add(rev).addOnSuccessListener {
                            Toast.makeText(this, "Post Success", Toast.LENGTH_LONG)
                            finish()
                        }
                    }
                }
        }else{
            var rev = Review(
                hotelId = "",
                restaurantId = restaurantId,
                rating = rbRating.rating.toInt(),
                timeReview = Timestamp.now(),
                image = imgUri,
                userId = "Ofnat05Ddal8mSJyblc8",
                title = etTitle.text.toString(),
                description = etDescription.text.toString()
            )

            revRef.add(rev).addOnSuccessListener {
                Toast.makeText(this, "Post Success", Toast.LENGTH_LONG)
                finish()
            }
        }

    }

    fun openGallery(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            IMG = data?.data
            ivImage.setImageURI(data?.data)
            IMG_EXIST = true
        }
    }
}
