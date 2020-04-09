package com.abencrauz.yates.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.R
import com.abencrauz.yates.models.HotelBookings
import com.abencrauz.yates.models.RestaurantBooking
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_restaurant_booking.view.*
import java.util.*

class HotelBookingAdapter: RecyclerView.Adapter<HotelBookingAdapter.ViewHolder> {
    private var db = Firebase.firestore
    private var hotRef = db.collection("hotels")
    private var bookRef = db.collection("hotel-bookings")
    private var context:Context
    private lateinit var bookings:Vector<HotelBookings>
    private lateinit var ids: Vector<String>

    constructor(context: Context){
        this.context = context
    }
    fun setBooking(bookings:Vector<HotelBookings>){
        this.bookings = bookings
    }
    fun setIds(ids:Vector<String>){
        this.ids = ids
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_restaurant_booking, parent, false))

    override fun getItemCount(): Int = bookings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var q = hotRef.document(bookings.get(position).hotelId.toString())
        q.get()
            .addOnSuccessListener { documentSnapshot ->
                holder.tvHotel.text = documentSnapshot.data!!["name"].toString()
            }

        holder.tvName.text = bookings.get(position).hotelId
        holder.tvPerson.text = bookings.get(position).nights.toString()
        holder.tvEmail.text = bookings.get(position).email
        val dt = bookings.get(position).timeBooking!!.toDate()
        holder.tvTime.text = dt.toString()

        holder.btnCancel.setOnClickListener(View.OnClickListener {
            bookRef.document(ids.get(position))
                .delete().addOnSuccessListener {
                    bookings.removeAt(position)
                    ids.removeAt(position)
                    this.notifyDataSetChanged()
                }
        })


    }

    class ViewHolder:RecyclerView.ViewHolder {
        var tvHotel: TextView
        var tvName: TextView
        var tvEmail:TextView
        var tvPerson:TextView
        var tvTime:TextView
        var btnCancel: MaterialButton
        constructor(view: View):super(view){
            tvHotel = view.findViewById(R.id.tv_name)
            tvName = view.findViewById(R.id.tv_name)
            tvEmail = view.findViewById(R.id.tv_email)
            tvPerson = view.findViewById(R.id.tv_person)
            tvTime = view.findViewById(R.id.tv_time)

            btnCancel = view.findViewById(R.id.btn_cancel)
        }
    }
}