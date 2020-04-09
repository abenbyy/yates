package com.abencrauz.yates

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapter.HotelRecycleViewAdapter
import com.abencrauz.yates.models.Hotel

class HotelActivity : AppCompatActivity(), HotelRecycleViewAdapter.OnHotelItemClickListener {

    var listHotel:MutableList<Hotel> = mutableListOf()

    private lateinit var hotelRecycleViewAdapter: HotelRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel)

        setListHotel()
        initAdapter()
        setButtonOnClickListener()
    }

    private fun setListHotel(){
        if(listHotel.isNotEmpty())
            listHotel.clear()

        for (i in HomeActivity.listHotelBaseLocation){
            listHotel.add(i)
        }
    }

    private fun setButtonOnClickListener(){
        val addNewPost = findViewById<Button>(R.id.add_new_btn)
        addNewPost.setOnClickListener {
            val toAddNewHotel = Intent(this, AddNewHotelActivity::class.java)
            toAddNewHotel.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(toAddNewHotel)
        }

        val filterHotel = findViewById<CardView>(R.id.filter_hotel)
        val filterVilla = findViewById<CardView>(R.id.filter_villa)
        val filterResort = findViewById<CardView>(R.id.filter_resort)

        filterHotel.setOnClickListener {
            if(listHotel.isNotEmpty())
                listHotel.clear()

            for ( i in HomeActivity.listHotelBaseLocation ){
                if(i.type == "Hotel")
                    listHotel.add(i)
            }
            addDataSet()
        }

        filterVilla.setOnClickListener {
            if(listHotel.isNotEmpty())
                listHotel.clear()

            for ( i in HomeActivity.listHotelBaseLocation ){
                Log.d("types ", i.type)
                if(i.type == "Villa") {
                    listHotel.add(i)
                }
            }
            addDataSet()
        }

        filterResort.setOnClickListener {
            if(listHotel.isNotEmpty())
                listHotel.clear()

            for ( i in HomeActivity.listHotelBaseLocation ){
                if(i.type == "Resort") {
                    listHotel.add(i)
                }
            }
            addDataSet()
        }

    }

    private fun initAdapter(){
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HotelActivity)
            hotelRecycleViewAdapter = HotelRecycleViewAdapter(this@HotelActivity)
            adapter = hotelRecycleViewAdapter
        }
        addDataSet()
    }

    private fun addDataSet(){
        hotelRecycleViewAdapter.submitList(listHotel)
        hotelRecycleViewAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        setListHotel()
        addDataSet()
    }

    override fun onResume() {
        super.onResume()
        setListHotel()
        addDataSet()
    }

    override fun onItemClickListener(items: Hotel, position: Int) {
//        Toast.makeText(this, items.name, Toast.LENGTH_LONG).show()
        var hotelListToIntent = arrayListOf<String>()
        hotelListToIntent.add(items.name)
        hotelListToIntent.add(items.image)
        hotelListToIntent.add(items.openTime)
        hotelListToIntent.add(items.phoneNumber)
        hotelListToIntent.add(items.price.toString())
        hotelListToIntent.add(items.address)
        hotelListToIntent.add(items.longitude.toString())
        hotelListToIntent.add(items.latitude.toString())

        val toHotelDetail = Intent(this, HotelDetailActivity::class.java)
        toHotelDetail.putExtra("hotels", hotelListToIntent)
        startActivity(toHotelDetail)
    }

}
