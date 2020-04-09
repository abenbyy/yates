package com.abencrauz.yates.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.HomeActivity
import com.abencrauz.yates.R
import com.abencrauz.yates.models.UserReview
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_review_cardview.view.*

class ReviewRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items:List<UserReview> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_review_cardview, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ReviewViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(reviewList:List<UserReview>){
        items = reviewList
    }

    class ReviewViewHolder constructor( itemView:View ): RecyclerView.ViewHolder (itemView) {
        private val imageReview = itemView.image_review
        private val usernameTv = itemView.username_tv
        private val nameTv = itemView.name_tv
        private val ratingTv = itemView.rating_tv
        private val descriptionTv = itemView.description_tv
        private val timeReviewTv = itemView.time_review_tv

        private val db = Firebase.firestore

        private val userRef = db.collection("users")

        fun bind(userReview : UserReview){
            Picasso.get().load(Uri.parse(userReview.image)).into(imageReview)

            userRef.document(userReview.userId).get().addOnSuccessListener { document ->
                usernameTv.text = document.data?.get("username").toString()
            }

            if(userReview.hotelId != ""){
                val index = userReview.hotelId.toInt()
                nameTv.text = HomeActivity.listHotel[index].name
            } else {
                val index = HomeActivity.listRestaurantId.indexOf(userReview.restaurantId)
                nameTv.text = HomeActivity.listRestaurant[index].name
            }

            ratingTv.rating = userReview.rating.toFloat()
            descriptionTv.text = userReview.description
            timeReviewTv.text = userReview.timeReview.toDate().toString()
        }

    }

}