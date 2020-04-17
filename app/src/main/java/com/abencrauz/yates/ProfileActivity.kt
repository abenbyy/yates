package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.abencrauz.yates.adapter.ViewPagerTabAdapter
import com.abencrauz.yates.tab_fragments.PostFragment
import com.abencrauz.yates.tab_fragments.ReviewFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class ProfileActivity : Fragment() {

    var users = HomeActivity.users

    lateinit var v:View

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var fragmentAdapter : ViewPagerTabAdapter

    private val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.activity_profile, container, false)
        setTextView()
        setProfilePicture()
        buttonListener()
        initializeTab()
        return v
    }

    private fun initializeTab(){
        tabLayout = v.findViewById(R.id.tab_layout)
        viewPager = v.findViewById(R.id.view_pager)

        fragmentAdapter = ViewPagerTabAdapter(childFragmentManager)
        viewPager.adapter = fragmentAdapter

        setTabPager()
    }

    private fun setTabPager(){
        val postFragment = PostFragment(HomeActivity.listUserPost)
        val reviewFragment = ReviewFragment(HomeActivity.listUserReview)

        val fragmentCount = fragmentAdapter.count
        for (i in fragmentCount downTo 0){
            try {
                fragmentAdapter.destroyItem(viewPager, i, fragmentAdapter.getItem(i))
                tabLayout.removeTabAt(i)
            }catch (e : Exception){
                break
            }
        }

        fragmentAdapter.addFragment(postFragment)
        fragmentAdapter.addFragment(reviewFragment)

        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)?.text = "Post"
        tabLayout.getTabAt(1)?.text = "Review"
    }

    private fun setTextView(){
        v.findViewById<TextView>(R.id.username_tv).text = users.username
        v.findViewById<TextView>(R.id.fullname_tv).text = users.fullname
        v.findViewById<TextView>(R.id.description_tv).text = users.description
    }

    private fun setProfilePicture(){
        val profilePicture = v.findViewById<CircleImageView>(R.id.image_profile)
        if(users.image != "")
            Picasso.get().load( Uri.parse(users.image) ).into(profilePicture)
    }

    private fun buttonListener(){
        v.findViewById<Button>(R.id.edit_profile_btn).setOnClickListener(){
            var toEditProfile = Intent(v.context, EditProfileActivity::class.java)
            toEditProfile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(toEditProfile)
        }
    }

    private fun getUserPost(){
        if(HomeActivity.addNewPost) {
            if (HomeActivity.listUserPost.isNotEmpty()) {
                HomeActivity.listUserPost.clear()
            }
            val sharedPreferences = activity?.getSharedPreferences("users", Context.MODE_PRIVATE)
            val userId = sharedPreferences?.getString("user_id", "")
            db.collection("user-post").orderBy("timePost", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.data["userId"].toString() == userId) {
                            HomeActivity.listUserPost.add(document.toObject())
                        }
                    }
                    initializeTab()
                }
            HomeActivity.addNewPost = false
        }
    }

    private fun getUserReview(){
        if(HomeActivity.addNewReview) {
            if (HomeActivity.listUserReview.isNotEmpty()) {
                HomeActivity.listUserReview.clear()
            }
            val sharedPreferences = activity?.getSharedPreferences("users", Context.MODE_PRIVATE)
            val userId = sharedPreferences?.getString("user_id", "")
            db.collection("user-reviews").orderBy("timeReview", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.data["userId"].toString() == userId) {
                            HomeActivity.listUserReview.add(document.toObject())
                        }
                    }
                    initializeTab()
                }
            HomeActivity.addNewReview = false
        }
    }

    override fun onStart() {
        super.onStart()
        setTextView()
        setProfilePicture()
    }

    override fun onResume() {
        super.onResume()
        setTextView()
        setProfilePicture()
        getUserPost()
        getUserReview()
    }

}
