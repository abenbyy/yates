package com.abencrauz.yates.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.R
import com.abencrauz.yates.models.Hotel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_popular_item_cardview.view.*

class PopularHotelRecycleViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Hotel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HotelViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_popular_item_cardview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HotelViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(hotelList: List<Hotel>) {
        items = hotelList
    }

    class HotelViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageHotel = itemView.item_image
        private val hotelName = itemView.item_name
        private val hotelAddress = itemView.item_address
        private val hotelOpenTime = itemView.item_time

        fun bind(hotel: Hotel) {
            if(hotel.image != "")
                Picasso.get().load(Uri.parse(hotel.image)).into(imageHotel)
            hotelName.text = hotel.name
            hotelAddress.text = hotel.address
            hotelOpenTime.text = "Open time : ${hotel.openTime}"
        }
    }
}