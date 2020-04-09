package com.abencrauz.yates

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : Fragment() {

    var users = HomeActivity.users

    lateinit var v:View

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.activity_profile, container, false)
        initializeTab()
        setTextView()
        setProfilePicture()
        buttonListener()
        return v
    }

    private fun initializeTab(){
        tabLayout = v.findViewById(R.id.tab_layout)
        viewPager = v.findViewById(R.id.view_pager)

        val fragmentAdapter = ViewPagerTabAdapter(childFragmentManager)
        viewPager.adapter = fragmentAdapter

        fragmentAdapter.addFragment(PostFragment(HomeActivity.listUserPost))
        fragmentAdapter.addFragment(ReviewFragment(HomeActivity.listUserReview))

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

    override fun onStart() {
        super.onStart()
        setTextView()
        setProfilePicture()
    }

    override fun onResume() {
        super.onResume()
        setTextView()
        setProfilePicture()
    }

}
