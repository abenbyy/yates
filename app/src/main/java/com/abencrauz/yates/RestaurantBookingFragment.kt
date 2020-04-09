package com.abencrauz.yates

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapters.RestaurantBookingAdapter
import com.abencrauz.yates.models.RestaurantBooking
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

class RestaurantBookingFragment : Fragment() {

    private var db = Firebase.firestore
    private var bookRef = db.collection("restaurant-bookings")
    private lateinit var rvRbooking: RecyclerView
    private lateinit var bookings: Vector<RestaurantBooking>
    private lateinit var rvAdapter: RestaurantBookingAdapter
    private var userId: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = this.activity!!.getSharedPreferences("users",Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", "")
        bookings = Vector()

       // rvRbooking = view!!.findViewById(R.id.rv_rbookings)
        var q = bookRef.whereEqualTo("userId",userId)
        q.get().addOnSuccessListener { documents->
            if(documents.size()>0){
                for(document in documents){
                    bookings.add(document.toObject<RestaurantBooking>())
                }
            }

            rvAdapter = RestaurantBookingAdapter(this.context!!)
            rvRbooking.apply {
                layoutManager = GridLayoutManager(this.context, 1)
                adapter = rvAdapter
            }

            rvAdapter.setBooking(bookings)
            rvAdapter.notifyDataSetChanged()



        }

        return inflater.inflate(R.layout.fragment_restaurant_booking, container, false)
    }

   
}
