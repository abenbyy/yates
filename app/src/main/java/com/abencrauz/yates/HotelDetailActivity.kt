package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapter.HotelReviewRecycleViewAdapter
import com.abencrauz.yates.models.UserReview
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hotel_detail.*
import kotlin.math.roundToInt

class HotelDetailActivity : AppCompatActivity() {

    private val listHotelReview = mutableListOf<UserReview>()
    private var hotelId = ""
    lateinit var reviewRecycleViewAdapter: HotelReviewRecycleViewAdapter

    private lateinit var layoutWriteReview:LinearLayout

    private lateinit var hotelName:TextView
    private lateinit var hotelAddress:TextView
    private lateinit var hotelPhoneNumber:TextView
    private lateinit var hotelOpenTime:TextView
    private lateinit var hotelPrice:TextView
    private lateinit var hotelImage:ImageView
    private lateinit var backBtn:ImageView

    private lateinit var viewDealBtn:Button
    private lateinit var viewInMapBtn:Button
    private lateinit var writeReviewBtn:Button
    private lateinit var addReview:Button

    private lateinit var ratingBar:RatingBar
    private lateinit var descriptionEt:TextInputEditText

    private var totalReviewRating:MutableList<ImageView> = mutableListOf()
    private lateinit var countReviewer:TextView

    private val db = Firebase.firestore
    private var longitude:Double = 0.0
    private var latitude:Double = 0.0

    private var hotelDefaultPrice = 0

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
        viewInMapBtn = findViewById(R.id.view_in_map_btn)

        ratingBar = findViewById(R.id.rating_bar)
        descriptionEt = findViewById(R.id.description_et)

        layoutWriteReview = findViewById(R.id.layout_add_review)
        layoutWriteReview.visibility = View.GONE

        totalReviewRating.add(findViewById(R.id.r_1))
        totalReviewRating.add(findViewById(R.id.r_2))
        totalReviewRating.add(findViewById(R.id.r_3))
        totalReviewRating.add(findViewById(R.id.r_4))
        totalReviewRating.add(findViewById(R.id.r_5))

        countReviewer = findViewById(R.id.count_review_tv)
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
            hotelDefaultPrice = hotels[4].toInt()
            longitude = hotels[6].toDouble()
            latitude = hotels[7].toDouble()
        }
    }

    private fun getReviews(){
        var count = 0
        var countRating = 0

        for(i in 0..4){
            totalReviewRating[i].visibility = View.GONE
        }

        if(listHotelReview.isNotEmpty())
            listHotelReview.clear()

        db.collection("user-reviews").orderBy("timeReview", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document.data["hotelId"].toString() == hotelId) {
                        listHotelReview.add(document.toObject())
                        count++
                        countRating += document.data["rating"].toString()[0].toString().toInt()
                    }
                }

                countReviewer.text = count.toString()

                if(count != 0){
                    countRating = (countRating.toDouble() / count.toDouble()).roundToInt()
                    countRating -= 1
                }

                if(countRating != 0)
                    for(i in 0..countRating)
                        totalReviewRating[i].visibility = View.VISIBLE

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
            toHotelBooking.putExtra("hotel_price", hotelDefaultPrice)
            startActivity(toHotelBooking)
        }

        writeReviewBtn.setOnClickListener {
            if (layoutWriteReview.visibility == View.VISIBLE){
                layoutWriteReview.visibility = View.GONE
                writeReviewBtn.text = "Write A Review"
            }else{
                layoutWriteReview.visibility = View.VISIBLE
                writeReviewBtn.text = "Cancel Write Review"
            }
        }

        addReview.setOnClickListener {
            addReview.isEnabled = false
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
                    writeReviewBtn.text = "Write A Review"
                    getReviews()
                }
        }

        viewInMapBtn.setOnClickListener {
            val mapIntent = Intent(this, LocationActivity::class.java)
            mapIntent.putExtra("location", hotelName.text.toString())
            startActivity(mapIntent)
        }
    }

    private fun setAdapter(){
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HotelDetailActivity, RecyclerView.VERTICAL, false)
            reviewRecycleViewAdapter = HotelReviewRecycleViewAdapter()
            adapter = reviewRecycleViewAdapter
        }
        recyclerView.isNestedScrollingEnabled = false

        addDataSet()
    }

    private fun addDataSet(){
        reviewRecycleViewAdapter.submitList(listHotelReview)
        reviewRecycleViewAdapter.notifyDataSetChanged()
    }

}
