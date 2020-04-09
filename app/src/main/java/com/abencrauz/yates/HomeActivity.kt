package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapter.PostRecycleViewAdapter
import com.abencrauz.yates.models.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth
    private lateinit var locationAc: AutoCompleteTextView
    private lateinit var dropDownBtn: ImageView
    private var listPostBaseCity:MutableList<UserPost> = mutableListOf()

    lateinit var postRecycleViewAdapter: PostRecycleViewAdapter

    private var popularHotel:MutableList<Hotel> = mutableListOf()
    private var popularRestaurant:MutableList<Restaurant> = mutableListOf()

    private lateinit var hotelButton: Button

    companion object{
        var users = User()

        var listUserPost:MutableList<UserPost> = mutableListOf()
        var listUserReview:MutableList<UserReview> = mutableListOf()

        var listCity:MutableList<City> = mutableListOf()
        var listCityName:MutableList<String> = mutableListOf()

        var listHotel:MutableList<Hotel> = mutableListOf()
        var listHotelBaseLocation:MutableList<Hotel> = mutableListOf()

        var listRestaurant:MutableList<Restaurant> = mutableListOf()
        var listRestaurantId:MutableList<String> = mutableListOf()

        var addNewPost = true
        var addNewReview = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        locationAc = findViewById(R.id.location_ac)

        getAllCity()
        getAllHotel()
        getAllRestaurant()

        checkToSignInAnonymously()

        initializeBottomNavigationMenu()

        setButtonListener()
        setAdapterPost()
        subscribeMessage()
    }


    private fun subscribeMessage(){
        FirebaseMessaging.getInstance().subscribeToTopic("Login")
    }

    private fun setButtonListener(){
        hotelButton = findViewById(R.id.hotel_button)
        hotelButton.setOnClickListener {
            if(locationAc.text.toString() == "Where To?"){
                locationAc.showDropDown()
                Snackbar.make(it, "Choose the city first", Snackbar.LENGTH_LONG).show()
            }else{
                getHotelBaseLocation()
            }
        }
    }

    private fun getHotelBaseLocation(){
        if(listHotelBaseLocation.isNotEmpty())
            listHotelBaseLocation.clear()

        val idx = listCityName.indexOf(locationAc.text.toString())
        val id = listCity[idx].cityId

        db.collection("hotels").whereEqualTo("countryId", id.toInt()).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    listHotelBaseLocation.add(document.toObject())
                }
                val intent = Intent(this,HotelActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
    }

    private fun checkToSignInAnonymously(){
        if(auth.currentUser == null){
            auth.signInAnonymously()
        }
    }

    private fun getAllCity(){
        db.collection("cities")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    listCity.add(
                        City(
                            document.id,
                            document.data["name"].toString(),
                            document.data["country"].toString(),
                            document.data["sub_country"].toString()
                        )
                    )
                    listCityName.add(document.data["name"].toString())
                    initDropDown()
                }
            }
    }

    private fun initDropDown(){
        val sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("location_city", locationAc.text.toString())
        editor.commit()

        val arrayAdapter = ArrayAdapter(this, R.layout.location_list_layout, listCityName)
        locationAc.setAdapter(arrayAdapter)

        dropDownBtn = findViewById(R.id.dropdown_btn)

        dropDownBtn.setOnClickListener {
            locationAc.showDropDown()
        }

        locationAc.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if( locationAc.text.toString() != sharedPreferences.getString("location_city", "") ){
                    editor.putString("location_city", locationAc.text.toString())
                    editor.commit()
                    getPostBaseOnCity()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun getPostBaseOnCity(){
        if(listPostBaseCity.isNotEmpty())
            listPostBaseCity.clear()

        val idx = listCityName.indexOf(locationAc.text.toString())
        db.collection("user-post").whereEqualTo("locationId", listCity[idx].cityId).get()
            .addOnSuccessListener { documents ->
                for ( document in documents ) {
                    listPostBaseCity.add(document.toObject())
                }
                addDataSet()
            }
        Log.d("change to -> ", locationAc.text.toString())
    }

    private fun getAllHotel(){
        db.collection("hotels")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    listHotel.add(document.toObject())
                }
            }
    }

    private fun getAllRestaurant(){
        db.collection("restaurants")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    listRestaurant.add(document.toObject())
                    listRestaurantId.add(document.id)
                }
            }
    }

    private fun getUserPost(){
        if(addNewPost) {
            if (listUserPost.isNotEmpty()) {
                listUserPost.clear()
            }
            val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("user_id", "")
            db.collection("user-post").orderBy("timePost", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.data["userId"].toString() == userId) {
                            listUserPost.add(document.toObject())
                        }
                    }
                }
            addNewPost = false
        }
    }

    private fun getUserReview(){
        if(addNewReview) {
            if (listUserReview.isNotEmpty()) {
                listUserReview.clear()
            }
            val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("user_id", "")
            db.collection("user-reviews").orderBy("timeReview", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.data["userId"].toString() == userId) {
                            listUserReview.add(document.toObject())
                        }
                    }
                }
            addNewReview = false
        }
    }

    private fun getPopularHotel(){
        if(popularHotel.isNotEmpty())
            popularHotel.clear()
    }

    private fun initializeBottomNavigationMenu(){
        var bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.nav_home

        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.nav_profile -> {
                val intent = Intent(this,AccountActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            R.id.nav_add -> {
                val intent = Intent(this,AddPostActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
        false
    }

    private fun getUserAccountData(){
        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        val userRef = db.collection("users").document( userId.toString() )
        userRef.get()
            .addOnSuccessListener { document ->
                users.fullname = document.data?.get("fullname").toString()
                users.username = document.data?.get("username").toString()
                users.password = document.data?.get("password").toString()
                users.email = document.data?.get("email").toString()
                users.description = document.data?.get("description").toString()
                users.image = document.data?.get("image").toString()
                getUserPost()
                getUserReview()
            }
    }

    override fun onStart() {
        super.onStart()
        getUserAccountData()
    }

    override fun onResume() {
        super.onResume()
        getUserPost()
        getUserReview()
    }

    override fun finish() {
        super.finish()
        val sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if(sharedPreferences.getString("location_city", "") != ""){
            editor.remove("location_city")
        }
    }

    private fun setAdapterPost(){
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view_p)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            postRecycleViewAdapter = PostRecycleViewAdapter()
            adapter = postRecycleViewAdapter
        }

        addDataSet()
    }

    private fun addDataSet(){
        postRecycleViewAdapter.submitList(listPostBaseCity)
        postRecycleViewAdapter.notifyDataSetChanged()
    }

}
