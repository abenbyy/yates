package com.abencrauz.yates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RestaurantFilteredActivity : AppCompatActivity() {

    private lateinit var type: String
    private lateinit var meal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_filtered)

        val intent =intent
        intent.getStringExtra("type")
        intent.getStringExtra("meal")
    }
}
