package com.abencrauz.yates.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.R
import com.abencrauz.yates.models.RestaurantBooking
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_restaurant_booking.view.*
import java.util.*

class RestaurantBookingAdapter: RecyclerView.Adapter<RestaurantBookingAdapter.ViewHolder> {
    private var db = Firebase.firestore
    private var resRef = db.collection("restaurants")
    private var bookRef = db.collection("restaurant-bookings")
    private var context:Context
    private lateinit var bookings:Vector<RestaurantBooking>
    private lateinit var ids: Vector<String>

    constructor(context: Context){
        this.context = context
    }
    fun setBooking(bookings:Vector<RestaurantBooking>){
        this.bookings = bookings
    }
    fun setIds(ids:Vector<String>){
        this.ids = ids
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_restaurant_booking, parent, false))

    override fun getItemCount(): Int = bookings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var q = resRef.document(bookings.get(position).restaurantId.toString())
        q.get()
            .addOnSuccessListener { documentSnapshot ->
                holder.tvRestaurant.text = documentSnapshot.data!!["name"].toString()
            }

        holder.tvName.text = bookings.get(position).name
        holder.tvPerson.text = bookings.get(position).seats.toString()
        holder.tvEmail.text = bookings.get(position).email
        val dt = bookings.get(position).time!!.toDate()
        holder.tvTime.text = dt.toString()

        holder.btnCancel.setOnClickListener(View.OnClickListener {
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this.context)
            dialogBuilder.setMessage(R.string.cancel_booking)
            dialogBuilder.setCancelable(false)
            dialogBuilder.setPositiveButton(R.string.yes){dialog, which ->
                bookRef.document(ids.get(position))
                    .delete().addOnSuccessListener {
                        bookings.removeAt(position)
                        ids.removeAt(position)
                        this.notifyDataSetChanged()
                    }
            }
            dialogBuilder.setNegativeButton(R.string.no){dialog, which ->

            }

            val dialog = dialogBuilder.create()
            dialog.setTitle(R.string.cancel_confirmation)
            dialog.show()
        })


    }

    class ViewHolder:RecyclerView.ViewHolder {
        var tvRestaurant: TextView
        var tvName: TextView
        var tvEmail:TextView
        var tvPerson:TextView
        var tvTime:TextView
        var btnCancel: MaterialButton
        constructor(view: View):super(view){
            tvRestaurant = view.findViewById(R.id.tv_restaurant)
            tvName = view.findViewById(R.id.tv_name)
            tvEmail = view.findViewById(R.id.tv_email)
            tvPerson = view.findViewById(R.id.tv_person)
            tvTime = view.findViewById(R.id.tv_time)

            btnCancel = view.findViewById(R.id.btn_cancel)
        }
    }
}