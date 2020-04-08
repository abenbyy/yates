package com.abencrauz.yates.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.R
import com.abencrauz.yates.models.Review
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_restaurant_review.view.*
import java.util.*

class RestaurantReviewAdapter: RecyclerView.Adapter<RestaurantReviewAdapter.ViewHolder> {

    private var context:Context
    private var reviews: Vector<Review> = Vector()
    private var db = Firebase.firestore
    private var userRef = db.collection("users")

    constructor(context: Context){
        this.context = context
    }

    fun setReview(reviews: Vector<Review>){
        this.reviews = reviews
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_restaurant_review,parent, false))

    override fun getItemCount(): Int = reviews.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = reviews.get(position).title.toString()
        holder.tvDescription.text = reviews.get(position).description.toString()
        Log.d("Rating", reviews.get(position).rating.toString())
        holder.ivR1.setImageResource(R.drawable.ic_unchecked)
        holder.ivR2.setImageResource(R.drawable.ic_unchecked)
        holder.ivR3.setImageResource(R.drawable.ic_unchecked)
        holder.ivR4.setImageResource(R.drawable.ic_unchecked)
        holder.ivR5.setImageResource(R.drawable.ic_unchecked)

        if(reviews.get(position).rating >= 1){
            holder.ivR1.setImageResource(R.drawable.ic_checked)
        }
        if(reviews.get(position).rating >= 2){
            holder.ivR2.setImageResource(R.drawable.ic_checked)
        }
        if(reviews.get(position).rating >= 3){
            holder.ivR3.setImageResource(R.drawable.ic_checked)
        }
        if(reviews.get(position).rating >= 4){
            holder.ivR4.setImageResource(R.drawable.ic_checked)
        }
        if(reviews.get(position).rating == 5){
            holder.ivR5.setImageResource(R.drawable.ic_checked)
        }
        holder.tvTime.text = reviews.get(position).timeReview!!.toDate().toString()
        if(reviews.get(position).image != ""){
            Picasso.get().load(reviews.get(position).image).into(holder.ivImage)
        }

        var q = userRef.document(reviews.get(position).userId.toString())
        q.get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot != null){
                    if(documentSnapshot["image"] != ""){
                        Picasso.get().load(documentSnapshot.data!!["image"].toString()).into(holder.ciImage)
                    }

                    holder.tvName.text = documentSnapshot.data!!["fullname"].toString()
                }
            }
    }

    class ViewHolder: RecyclerView.ViewHolder {
        var ciImage: CircleImageView
        var tvName : TextView
        var tvTime : TextView
        var ivImage : ImageView
        var ivR1 : ImageView
        var ivR2 : ImageView
        var ivR3 : ImageView
        var ivR4 : ImageView
        var ivR5 : ImageView
        var tvTitle : TextView
        var tvDescription : TextView
        constructor(view: View): super(view){
            ciImage = view.findViewById(R.id.ci_profile)
            tvName = view.findViewById(R.id.tv_name)
            tvTime = view.findViewById(R.id.tv_time)
            ivR1 = view.findViewById<ImageView>(R.id.r_1)

            ivR2 = view.findViewById<ImageView>(R.id.r_2)

            ivR3 = view.findViewById<ImageView>(R.id.r_3)

            ivR4 =  view.findViewById<ImageView>(R.id.r_4)

            ivR5 = view.findViewById<ImageView>(R.id.r_5)

            ivImage = view.findViewById(R.id.iv_image)
            tvTitle = view.findViewById(R.id.tv_title)
            tvDescription = view.findViewById(R.id.tv_desc)

        }
    }
}