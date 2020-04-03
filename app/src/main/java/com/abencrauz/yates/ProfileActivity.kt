package com.abencrauz.yates

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    var users = HomeActivity.users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeBottomNavigationMenu()
        setTextView()
        buttonListener()
    }

    private fun initializeBottomNavigationMenu(){
        var bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.nav_profile

        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.nav_home -> {
                val intent = Intent(this,HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
        false
    }

    private fun setTextView(){
        findViewById<TextView>(R.id.username_tv).text = users.username
        findViewById<TextView>(R.id.fullname_tv).text = users.fullname
        findViewById<TextView>(R.id.description_tv).text = users.description
    }

    private fun buttonListener(){
        findViewById<Button>(R.id.edit_profile_btn).setOnClickListener(){
            var toEditProfile = Intent(this, EditProfileActivity::class.java)
            toEditProfile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(toEditProfile)
        }
    }

    override fun onStart() {
        super.onStart()
        setTextView()
    }

    override fun onResume() {
        super.onResume()
        setTextView()
    }

}
