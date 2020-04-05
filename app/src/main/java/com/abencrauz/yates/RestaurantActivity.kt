package com.abencrauz.yates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapters.RestaurantAdapter
import com.abencrauz.yates.models.Restaurant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class RestaurantActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private var restRef = db.collection("restaurants")
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var rvRestaurant: RecyclerView
    private lateinit var restaurants : Vector<Restaurant>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        restaurantAdapter = RestaurantAdapter(this)
        rvRestaurant = findViewById(R.id.rv_restaurant)
        restaurants = Vector()

        rvRestaurant.apply {
            layoutManager = GridLayoutManager(this@RestaurantActivity,1)
            adapter = restaurantAdapter
        }


    }

    override fun onStart() {
        super.onStart()
        var query = restRef.limit(10)
        restaurants = Vector()
        restRef.get()
            .addOnSuccessListener { documents->
                if(documents.size()>0){
                    for(document in documents){
                        var r = Restaurant(
                            document.data["name"] as String,
                            document.data["location"] as String?,
                            document.data["image"] as String?,
                            document.data["address"] as String,
                            document.data["hours"] as String,
                            document.data["type"] as String,
                            document.data["meal"] as String
                        )
                        restaurants.add(r)
                    }
                }

                restaurantAdapter.setRestaurant(restaurants)
                restaurantAdapter.notifyDataSetChanged()
            }
    }
}
