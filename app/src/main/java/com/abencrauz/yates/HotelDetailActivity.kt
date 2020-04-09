package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapter.ReviewRecycleViewAdapter
import com.abencrauz.yates.models.UserReview
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hotel_detail.*

class HotelDetailActivity : AppCompatActivity() {

    private val listHotelReview = mutableListOf<UserReview>()
    private var hotelId = ""
    lateinit var reviewRecycleViewAdapter: ReviewRecycleViewAdapter

    private lateinit var layoutWriteReview:LinearLayout

    private lateinit var hotelName:TextView
    private lateinit var hotelAddress:TextView
    private lateinit var hotelPhoneNumber:TextView
    private lateinit var hotelOpenTime:TextView
    private lateinit var hotelPrice:TextView
    private lateinit var hotelImage:ImageView
    private lateinit var backBtn:ImageView

    private lateinit var viewDealBtn:Button
    private lateinit var writeReviewBtn:Button
    private lateinit var addReview:Button

    private lateinit var ratingBar:RatingBar
    private lateinit var descriptionEt:TextInputEditText

    private val db = Firebase.firestore
    private var longitude:Double = 0.0
    private var latitude:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_detail)

        initialiazeComponent()
        getIncomeContent()
        setBtnClickListener()
        setAdapter()
    }

    private fun initialiazeComponent(){
        hotelName = findViewById(R.id.hotel_name)
        hotelAddress = findViewById(R.id.hotel_address)
        hotelOpenTime = findViewById(R.id.hotel_open_time)
        hotelPhoneNumber = findViewById(R.id.hotel_phone_number)
        hotelPrice = findViewById(R.id.hotel_price)
        hotelImage = findViewById(R.id.hotel_image)

        backBtn = findViewById(R.id.back_btn)
        viewDealBtn = findViewById(R.id.view_deal_btn)
        writeReviewBtn = findViewById(R.id.write_review_btn)
        addReview = findViewById(R.id.add_review)

        ratingBar = findViewById(R.id.rating_bar)
        descriptionEt = findViewById(R.id.description_et)

        layoutWriteReview = findViewById(R.id.layout_add_review)
        layoutWriteReview.visibility = View.GONE
    }

    private fun getIncomeContent(){
        if(intent.hasExtra("hotels")){
            var hotels = intent.getStringArrayListExtra("hotels")

            db.collection("hotels").whereEqualTo("name", hotels[0])
                .get().addOnSuccessListener { documents ->
                    for(document in documents){
                        hotelId = document.id
                        break
                    }
                    getReviews()
                }

            Picasso.get().load(Uri.parse(hotels[1])).into(hotelImage)

            hotelName.text = hotels[0]
            hotelAddress.text = hotels[5]
            hotelOpenTime.text = hotels[2]
            hotelPhoneNumber.text = hotels[3]
            hotelPrice.text = "IDR ${hotels[4]}"
            longitude = hotels[6].toDouble()
            latitude = hotels[7].toDouble()
        }
    }

    private fun getReviews(){
        if(listHotelReview.isNotEmpty())
            listHotelReview.clear()

        db.collection("user-reviews").whereEqualTo("hotelId", hotelId)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    listHotelReview.add(document.toObject())
                }
                addDataSet()
            }
    }

    private fun setBtnClickListener(){
        backBtn.setOnClickListener {
            finish()
        }

        viewDealBtn.setOnClickListener {
            val toHotelBooking = Intent(this, HotelBookingActivity::class.java)
            toHotelBooking.putExtra("hotel_id", hotelId)
            startActivity(toHotelBooking)
        }

        writeReviewBtn.setOnClickListener {
            if (layoutWriteReview.visibility == View.VISIBLE){
                layoutWriteReview.visibility = View.GONE
            }else{
                layoutWriteReview.visibility = View.VISIBLE
            }
        }

        addReview.setOnClickListener {
            val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("user_id", "")

            var review = UserReview(
                userId!!,
                "",
                hotelId,
                "",
                rating_bar.rating.toDouble(),
                descriptionEt.text.toString(),
                Timestamp.now()
            )
            db.collection("user-reviews").add(review)
                .addOnSuccessListener {
                    layoutWriteReview.visibility = View.GONE
                    getReviews()
                }
        }
    }

    private fun setAdapter(){
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HotelDetailActivity)
            reviewRecycleViewAdapter = ReviewRecycleViewAdapter()
            adapter = reviewRecycleViewAdapter
        }

        addDataSet()
    }

    private fun addDataSet(){
        reviewRecycleViewAdapter.submitList(listHotelReview)
        reviewRecycleViewAdapter.notifyDataSetChanged()
    }

}
