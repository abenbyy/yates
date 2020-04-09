package com.abencrauz.yates.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.HomeActivity
import com.abencrauz.yates.R
import com.abencrauz.yates.models.UserPost
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_post_cardview.view.*

class PostRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items:List<UserPost> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_post_cardview, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(postList:List<UserPost>){
        items = postList
    }

    class PostViewHolder constructor( itemView: View): RecyclerView.ViewHolder (itemView) {
        private val imagePost = itemView.image_post
        private val usernameTv = itemView.username_tv
        private val descriptionTv = itemView.description_tv
        private val cityTv = itemView.city_tv
        private val timePostTv = itemView.time_post_tv
        private val locationTvShow = itemView.location_tv_show

        private val db = Firebase.firestore

        private val userRef = db.collection("users")

        fun bind(userPost : UserPost){
            Picasso.get().load(Uri.parse(userPost.image)).into(imagePost)

            userRef.document(userPost.userId).get().addOnSuccessListener { document ->
                usernameTv.text = document.data?.get("username").toString()
            }


            if(userPost.locationId != ""){
                locationTvShow.visibility = View.VISIBLE
                for (i in HomeActivity.listCity){
                    if(i.cityId == userPost.locationId){
                        cityTv.text = i.name
                        break
                    }
                }
            }else{
                cityTv.text = ""
                locationTvShow.visibility = View.GONE
            }

            descriptionTv.text = userPost.description
            timePostTv.text = userPost.timePost.toDate().toString()
        }

    }
}