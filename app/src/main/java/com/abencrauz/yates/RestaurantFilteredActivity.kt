package com.abencrauz.yates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapters.RestaurantAdapter
import com.abencrauz.yates.models.Restaurant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.util.*

class RestaurantFilteredActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var restRef = db.collection("restaurants")

    private lateinit var rvRestaurant: RecyclerView
    private lateinit var type: String
    private lateinit var meal: String
    private lateinit var restaurants :Vector<Restaurant>
    private lateinit var radapter :RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_filtered)

        val intent =intent
        type = intent.getStringExtra("type")
        meal = intent.getStringExtra("meal")
        restaurants = Vector()
        rvRestaurant = findViewById(R.id.rv_restaurant)
        radapter = RestaurantAdapter(this)
        rvRestaurant.apply {
            layoutManager = GridLayoutManager(this@RestaurantFilteredActivity, 1)
            adapter = radapter
        }
        if(type!=""){
            restRef.whereEqualTo("type",type)
                .get().addOnSuccessListener { documents->
                    if(documents.size()>0){
                        for(document in documents){
                            restaurants.add(document.toObject<Restaurant>())
                        }
                        radapter.setRestaurant(restaurants)
                        radapter.notifyDataSetChanged()
                    }
                }
        }else if(meal !=""){
            restRef.whereEqualTo("meal",meal)
                .get().addOnSuccessListener { documents->
                    if(documents.size()>0){
                        for(document in documents){
                            restaurants.add(document.toObject<Restaurant>())
                        }
                        radapter.setRestaurant(restaurants)
                        radapter.notifyDataSetChanged()
                    }
                }
        }
    }
}
