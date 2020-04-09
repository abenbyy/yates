package com.abencrauz.yates.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.R
import com.abencrauz.yates.RestaurantDetailActivity
import com.abencrauz.yates.models.Restaurant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_restaurant.view.*
import java.util.Vector

class RestaurantAdapter: RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private var db = Firebase.firestore
    private var storage = Firebase.storage.reference.child("image/")

    private var context: Context
    private var restaurants : Vector<Restaurant> = Vector()

    constructor(context: Context){
        this.context = context
    }

    fun setRestaurant(restaurants: Vector<Restaurant>){
        this.restaurants = restaurants
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false))

    override fun getItemCount(): Int = restaurants.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       if(restaurants.get(position).image != ""){
           Picasso.get()
               .load(restaurants.get(position).image)
               .error(R.drawable.illustration_restaurant)
               .into(holder.ivImage)
       }

        holder.tvName.text = restaurants.get(position).name
        holder.tvType.text = restaurants.get(position).type
        holder.tvMeal.text = restaurants.get(position).meal

        holder.cvRestaurant.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, RestaurantDetailActivity::class.java)
            intent.putExtra("restaurantName", restaurants.get(position).name)
            context.startActivity(intent)
        })

    }

    class ViewHolder: RecyclerView.ViewHolder {

        var ivImage : ImageView
        var tvName: TextView
        var tvMeal : TextView
        var tvType: TextView
        var cvRestaurant: CardView

        constructor(restaurantView:View) : super(restaurantView){

            ivImage = restaurantView.findViewById(R.id.iv_image)
            tvName = restaurantView.findViewById(R.id.tv_name)
            tvMeal = restaurantView.findViewById(R.id.tv_meal)
            tvType = restaurantView.findViewById(R.id.tv_type)
            cvRestaurant = restaurantView.findViewById(R.id.cv_restaurant)
        }
    }
}