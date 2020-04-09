package com.abencrauz.yates.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.R
import com.abencrauz.yates.models.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_popular_item_cardview.view.*

class PopularRestaurantRecycleViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Restaurant> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RestaurantViewHolder(
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
            is RestaurantViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(hotelList: List<Restaurant>) {
        items = hotelList
    }

    class RestaurantViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageRestaurant = itemView.item_image
        private val restaurantName = itemView.item_name
        private val restaurantAddress = itemView.item_address
        private val restaurantTime = itemView.item_time

        fun bind(restaurant: Restaurant) {
            if(restaurant.image != "")
                Picasso.get().load(Uri.parse(restaurant.image)).into(imageRestaurant)
            restaurantName.text = restaurant.name
            restaurantAddress.text = restaurant.address
            restaurantTime.text = "Open time : ${restaurant.hours}"
        }
    }
}