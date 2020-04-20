package com.abencrauz.yates.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.HomeActivity
import com.abencrauz.yates.PreferenceHelper
import com.abencrauz.yates.R
import com.abencrauz.yates.models.Hotel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_hotel_cardview.view.*

class HotelRecycleViewAdapter(var clickListener: OnHotelItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Hotel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HotelViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_hotel_cardview,
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
                holder.bind(items[position], clickListener)
            }
        }
    }

    fun submitList(hotelList: List<Hotel>) {
        items = hotelList
    }

    class HotelViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageHotel = itemView.hotel_image
        private val hotelName = itemView.hotel_name
        private val hotelAddress = itemView.hotel_address
        private val hotelOpenTime = itemView.hotel_open_time
        private val hotelPrice = itemView.hotel_price

        fun bind(hotel: Hotel, action:OnHotelItemClickListener) {
            if(hotel.image != "")
                Picasso.get().load(Uri.parse(hotel.image)).into(imageHotel)
            hotelName.text = hotel.name
            hotelAddress.text = hotel.address
            hotelOpenTime.text = "Open time : ${hotel.openTime}"
            hotelPrice.text = PreferenceHelper.currencyString+ " ${(hotel.price.toDouble() * PreferenceHelper.currencyMultiplier)}"

            itemView.setOnClickListener{
                action.onItemClickListener(hotel, adapterPosition)
            }
        }
    }

    interface OnHotelItemClickListener{
        fun onItemClickListener(items:Hotel, position: Int)
    }
}