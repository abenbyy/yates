package com.abencrauz.yates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RestaurantFilteredActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var restRef = db.collection("restaurants")

    private lateinit var type: String
    private lateinit var meal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_filtered)

        val intent =intent
        intent.getStringExtra("type")
        intent.getStringExtra("meal")

        if(type!=""){

        }else if(meal !=""){

        }
    }
}
