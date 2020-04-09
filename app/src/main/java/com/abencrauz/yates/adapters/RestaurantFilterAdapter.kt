package com.abencrauz.yates.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.R
import com.abencrauz.yates.RestaurantFilteredActivity
import com.abencrauz.yates.models.RestaurantFilter
import kotlinx.android.synthetic.main.item_restaurant_filter.view.*
import java.util.*

class RestaurantFilterAdapter: RecyclerView.Adapter<RestaurantFilterAdapter.ViewHolder> {
    private var context:Context
    private var filters: Vector<RestaurantFilter> = Vector()

    constructor(context: Context){
        this.context = context
    }

    fun setFilter(filters:Vector<RestaurantFilter>){
        this.filters = filters
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant_filter,parent, false))

    override fun getItemCount(): Int = filters.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivImage.setImageDrawable(filters.get(position).image)

        holder.tvName.text = filters.get(position).name.toString()
        holder.cvFilter.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, RestaurantFilteredActivity::class.java)
            if(filters.get(position).type.equals("Type")){
                intent.putExtra("type",filters.get(position).name)
                intent.putExtra("meal","")
            }else if(filters.get(position).type.equals("Meal")){
                intent.putExtra("type",filters.get(position).name)
                intent.putExtra("meal","")
            }
            context.startActivity(intent)
        })
    }

    class ViewHolder: RecyclerView.ViewHolder {
        var ivImage: ImageView
        var tvName: TextView
        var cvFilter: CardView
        constructor(restaurantFilterView: View): super(restaurantFilterView){
            ivImage = restaurantFilterView.findViewById(R.id.iv_image)
            tvName = restaurantFilterView.findViewById(R.id.tv_name)
            cvFilter = restaurantFilterView.findViewById(R.id.cv_filter)
        }
    }
}