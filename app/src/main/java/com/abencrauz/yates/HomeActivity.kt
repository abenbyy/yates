package com.abencrauz.yates

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapter.PopularHotelRecycleViewAdapter
import com.abencrauz.yates.adapter.PopularRestaurantRecycleViewAdapter
import com.abencrauz.yates.adapter.PostRecycleViewAdapter
import com.abencrauz.yates.alarm.ReminderBroadcast
import com.abencrauz.yates.models.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class HomeActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth
    private lateinit var locationAc: AutoCompleteTextView
    private lateinit var dropDownBtn: ImageView
    private var listPostBaseCity:MutableList<UserPost> = mutableListOf()

    private lateinit var postRecycleViewAdapter: PostRecycleViewAdapter
    private lateinit var popularHotelRecycleViewAdapter: PopularHotelRecycleViewAdapter
    private lateinit var popularRestaurantRecycleViewAdapter: PopularRestaurantRecycleViewAdapter

    private var popularHotel:MutableList<Hotel> = mutableListOf()
    private var popularRestaurant:MutableList<Restaurant> = mutableListOf()

    private lateinit var hotelButton: Button
    private lateinit var restaurantButton: Button

    private lateinit var userPostLayout:LinearLayout
    private lateinit var popularRestaurantLayout: LinearLayout
    private lateinit var popularHotelLayout: LinearLayout

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

        createNotificationChannel()
        activeAlarm()

        auth = FirebaseAuth.getInstance()

        locationAc = findViewById(R.id.location_ac)

        popularHotelLayout = findViewById(R.id.popular_hotel_layout)
        popularRestaurantLayout = findViewById(R.id.popular_restaurant_layout)
        userPostLayout = findViewById(R.id.user_post_layout)

        popularHotelLayout.visibility = View.GONE
        popularRestaurantLayout.visibility = View.GONE
        userPostLayout.visibility = View.GONE

        getAllCity()
        getAllHotel()
        getAllRestaurant()

        checkToSignInAnonymously()

        initializeBottomNavigationMenu()

        setButtonListener()
        setAdapterPost()
        setAdapterPopularHotel()
        setAdapterPopularRestaurant()
        subscribeMessage()
    }

    private fun subscribeMessage(){
        FirebaseMessaging.getInstance().subscribeToTopic("Login")
    }

    private fun setButtonListener(){
        hotelButton = findViewById(R.id.hotel_button)
        restaurantButton = findViewById(R.id.restaurant_button)
        hotelButton.setOnClickListener {
            if(locationAc.text.toString() == "Where To?"){
                locationAc.showDropDown()
                Snackbar.make(it, "Choose the city first", Snackbar.LENGTH_LONG).show()
            }else{
                getHotelBaseLocation()
            }
        }
        restaurantButton.setOnClickListener {
            if(locationAc.text.toString() == "Where To?"){
                locationAc.showDropDown()
                Snackbar.make(it, "Choose the city first", Snackbar.LENGTH_LONG).show()
            }else{
                val sharedPreferences = getSharedPreferences("locations",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("location_city",locationAc.text.toString())
                val intent = Intent(this, RestaurantActivity::class.java)
                startActivity(intent)
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
        if(listCity.isNotEmpty()){
            listCity.clear()
            listCityName.clear()
        }
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
                    getPopularHotel()
                    getPopularRestaurant()
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
        if(listHotel.isNotEmpty())
            listHotel.clear()
        db.collection("hotels")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    listHotel.add(document.toObject())
                }
            }
    }

    private fun getAllRestaurant(){
        if (listRestaurant.isNotEmpty()){
            listRestaurantId.clear()
            listRestaurant.clear()
        }
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
        if(popularHotel.isNotEmpty()){
            popularHotel.clear()
            addDataSetHotel()
        }

        db.collection("user-reviews")
            .whereGreaterThanOrEqualTo("rating",4)
            .orderBy("rating", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documents ->
                for (document in documents){
                    if(popularHotel.size == 5){
                        break
                    }
                    if(document.data["hotelId"].toString() != "")
                        db.collection("hotels").document(document.data["hotelId"].toString())
                            .get()
                            .addOnSuccessListener { doc ->
                                db.collection("cities").document(doc["countryId"].toString())
                                    .get()
                                    .addOnSuccessListener { res ->
                                        if(res["name"].toString() == locationAc.text.toString().trim()){
                                            var add = true
                                            for( j in popularHotel ){
                                                if(j.name == doc["name"].toString()){
                                                    add = false
                                                    break
                                                }
                                            }
                                            if(add){
                                                var i = Hotel(
                                                    doc["name"].toString(),
                                                    doc["address"].toString(),
                                                    doc["countryId"].toString().toInt(),
                                                    doc["latitude"].toString().toDouble(),
                                                    doc["longitude"].toString().toDouble(),
                                                    doc["image"].toString(),
                                                    doc["type"].toString(),
                                                    doc["phoneNumber"].toString(),
                                                    doc["openTime"].toString(),
                                                    doc["price"].toString().toInt()
                                                )
                                                popularHotel.add(i)
                                                addDataSetHotel()
                                            }
                                        }
                                    }
                            }
                }
            }
    }

    private fun getPopularRestaurant(){
        if(popularRestaurant.isNotEmpty()){
            popularRestaurant.clear()
            addDataSetRestaurant()
        }

        db.collection("user-reviews")
            .whereGreaterThanOrEqualTo("rating",4)
            .orderBy("rating", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documents ->
                for (document in documents){
                    if(popularRestaurant.size == 5)
                        break
                    if(document.data["restaurantId"].toString() != "")
                        db.collection("restaurants").document(document.data["restaurantId"].toString())
                            .get()
                            .addOnSuccessListener { doc ->
                                if(doc["location"].toString() == locationAc.text.toString().trim()){
                                    var add = true
                                    for ( i in popularRestaurant){
                                        if(doc["name"].toString() == i.name) {
                                            add = false
                                            break
                                        }
                                    }
                                    if(add){
                                        var i = Restaurant(
                                            doc["name"].toString(),
                                            doc["location"].toString(),
                                            doc["image"].toString(),
                                            doc["address"].toString(),
                                            doc["hours"].toString(),
                                            doc["type"].toString(),
                                            doc["meal"].toString(),
                                            doc["phone"].toString()
                                        )
                                        popularRestaurant.add(i)
                                        addDataSetRestaurant()
                                    }
                                }
                            }
                }
            }
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
            R.id.nav_bookings ->{
                val intent = Intent(this, BookingsActivity::class.java)
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
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            postRecycleViewAdapter = PostRecycleViewAdapter()
            adapter = postRecycleViewAdapter
        }

        addDataSet()
    }

    private fun setAdapterPopularHotel(){
        var recyclerViewHotel = findViewById<RecyclerView>(R.id.popular_hotel_rv)

        recyclerViewHotel.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            popularHotelRecycleViewAdapter = PopularHotelRecycleViewAdapter()
            adapter = popularHotelRecycleViewAdapter
        }
        addDataSetHotel()
    }

    private fun setAdapterPopularRestaurant(){
        var recyclerViewRestaurant = findViewById<RecyclerView>(R.id.popular_restaurant_rv)

        recyclerViewRestaurant.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            popularRestaurantRecycleViewAdapter = PopularRestaurantRecycleViewAdapter()
            adapter = popularRestaurantRecycleViewAdapter
        }
        addDataSetRestaurant()
    }

    private fun addDataSet(){
        if(listPostBaseCity.size != 0){
            userPostLayout.visibility = View.VISIBLE
        }else{
            userPostLayout.visibility = View.GONE
        }
        postRecycleViewAdapter.submitList(listPostBaseCity)
        postRecycleViewAdapter.notifyDataSetChanged()
    }

    private fun addDataSetHotel(){
        if(popularHotel.size != 0){
            popularHotelLayout.visibility = View.VISIBLE
        }else{
            popularHotelLayout.visibility = View.GONE
        }
        popularHotelRecycleViewAdapter.submitList(popularHotel)
        popularHotelRecycleViewAdapter.notifyDataSetChanged()
    }

    private fun addDataSetRestaurant(){
        if(popularRestaurant.size != 0){
            popularRestaurantLayout.visibility = View.VISIBLE
        }else{
            popularRestaurantLayout.visibility = View.GONE
        }
        popularRestaurantRecycleViewAdapter.submitList(popularRestaurant)
        popularRestaurantRecycleViewAdapter.notifyDataSetChanged()
    }

    private fun activeAlarm(){
        val intent = Intent(this, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val alarmUp = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null

        if (!alarmUp) {
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 13)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            alarmManager?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }else{
            Log.d("myTag", "Alarm is already active")
        }

//        val timeActive = System.currentTimeMillis()
//        val tenSecondInMillis = 1000 * 10
//        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP,
//            timeActive + tenSecondInMillis,
//            1000*5,
//            pendingIntent
//        )

    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "YATESChannel"
            val desc = "Channel for YATES Reminder"
            val important = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notifyMe", name, important)
            channel.description = desc

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            Log.d("Alarm set", "alarm has been set")
        }
    }

}
