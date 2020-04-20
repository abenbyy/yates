package com.abencrauz.yates

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapters.RestaurantReviewAdapter
import com.abencrauz.yates.models.Review
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_restaurant_detail.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class RestaurantDetailActivity : AppCompatActivity() {
    private var restaurant: Map<String,Any> = mutableMapOf()
    private var reviews : Vector<Review> = Vector()
    private val db = Firebase.firestore
    private val restRef = db.collection("restaurants")
    private val revRef = db.collection("user-reviews")
    private lateinit var pb: ProgressBar
    private lateinit var restaurantName: String
    private lateinit var restaurantId: String
    lateinit var cont: LinearLayout

    private lateinit var rvReview: RecyclerView
    private lateinit var reviewAdapter:RestaurantReviewAdapter

    private lateinit var ivImage: ImageView
    private lateinit var tvName:TextView
    private lateinit var tvMeal:TextView
    private lateinit var tvType:TextView
    private lateinit var tvHours:TextView
    private lateinit var tvAddress:TextView
    private lateinit var tvAddrev: TextView
    private lateinit var btnMap: MaterialButton
    private lateinit var btnBook: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        cont = findViewById(R.id.cont_detail)
        toggleRev(false)
        pb = findViewById(R.id.pb)
        toggleLoad(true)
        restaurantId = ""
        initComponents()
        val intent = intent
        restaurantName = intent.getStringExtra("restaurantName")

        var q = restRef.whereEqualTo("name",restaurantName).limit(1)
        q.get()
            .addOnSuccessListener { documents->
                if(documents.size() == 1){
                    restaurantId = documents.first().id
                    restaurant = documents.first().data
                    handleRestaurantData()
                    bindReview()
                }else{
                    Toast.makeText(this, "Seems there is an error",Toast.LENGTH_LONG)
                }
            }


    }

    override fun onStart() {
        super.onStart()
        if(restaurantId != ""){
            bindReview()
        }
    }

    fun toggleRev(bool: Boolean){
        if(bool){
            ll_rev.visibility = View.VISIBLE
            tv_norev.visibility = View.GONE
        }else{
            ll_rev.visibility = View.GONE
            tv_norev.visibility = View.VISIBLE
        }
    }


    fun bindReview(){
        var q = revRef.whereEqualTo("restaurantId",restaurantId)
        q.get()
            .addOnSuccessListener { documents ->
                if(documents.size()>0){
                    tv_review.text = documents.size().toString()
                    var count = 0
                    reviews = Vector()
                    for(document in documents){
                        val rev = document.toObject<Review>()
                        var temp = document.data
                        count+=rev.rating
                        reviews.add(rev)
//                        reviews.add(Review(
//                            temp["hotelId"].toString(),
//                            temp["restaurantId"].toString(),
//                            temp["userId"].toString(),
//                            temp["image"].toString(),
//                            Date(temp["timeStamp"]),
//                            temp["rating"].toString().toInt(),
//                            temp["title"].toString(),
//                            temp["description"].toString()
//                        ))
                        toggleRev(true)
                    }
                    var avg = count/documents.size()
                    setReviews(avg)
                    reviewAdapter.setReview(reviews)
                    reviewAdapter.notifyDataSetChanged()

                }else{
                    toggleRev(false)
                }
            }
    }

    fun initComponents(){
        rvReview = findViewById(R.id.rv_reviews)
        ivImage = findViewById(R.id.iv_image)
        tvName = findViewById(R.id.tv_name)
        tvMeal = findViewById(R.id.tv_meal)
        tvType = findViewById(R.id.tv_type)
        tvHours = findViewById(R.id.tv_hours)
        tvAddress = findViewById(R.id.tv_address)
        tvAddrev = findViewById(R.id.tv_addrev)

        btnMap = findViewById(R.id.btn_map)
        btnBook = findViewById(R.id.btn_book)

        reviewAdapter = RestaurantReviewAdapter(this)
        rvReview.apply {
            layoutManager = LinearLayoutManager(this@RestaurantDetailActivity,RecyclerView.VERTICAL, false)
            adapter = reviewAdapter
        }
        rvReview.isNestedScrollingEnabled = false


    }

    fun setReviews(value: Int){
        if(value < 5){
            r_5.setImageResource(R.drawable.ic_unchecked)
        }
        if(value < 4){
            r_4.setImageResource(R.drawable.ic_unchecked)
        }
        if(value < 3){
            r_3.setImageResource(R.drawable.ic_unchecked)
        }
        if(value < 2){
            r_2.setImageResource(R.drawable.ic_unchecked)
        }
    }

    fun handleRestaurantData(){
        tvName.text = restaurant["name"].toString()
        tvMeal.text = restaurant["meal"].toString()
        tvType.text = restaurant["type"].toString()
        tvHours.text = restaurant["hours"].toString()
        tvAddress.text = restaurant["address"].toString()
        tv_phone.text = restaurant["phone"].toString()

        btnMap.setOnClickListener(View.OnClickListener {
//            val gmmIntentUri: Uri =
//                Uri.parse("geo:0,0?q="+Uri.encode(restaurant["name"].toString()))
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
            val mapIntent = Intent(this, LocationActivity::class.java)
            mapIntent.putExtra("location", restaurant["name"].toString())
            startActivity(mapIntent)
        })

        btnBook.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RestaurantBookingActivity::class.java)
            intent.putExtra("restaurantId",restaurantId)
            intent.putExtra("restaurantName",restaurantName)
            startActivity(intent)
        })

        tvAddrev.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddRestaurantReviewActivity::class.java)
            intent.putExtra("restaurantName",restaurantName)
            intent.putExtra("restaurantId",restaurantId)

            startActivity(intent)

        })


        if(restaurant["image"] == ""){

        }else{
            Picasso.get().load(restaurant["image"].toString()).into(ivImage)
        }
        toggleLoad(false)
    }

    fun toggleLoad(isLoading: Boolean){
        if(isLoading){
            pb.visibility = View.VISIBLE
            cont.visibility = View.GONE
        }else{
            pb.visibility = View.GONE
            cont.visibility = View.VISIBLE
        }
    }
}
