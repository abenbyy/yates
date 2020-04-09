package com.abencrauz.yates

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.abencrauz.yates.adapter.ViewPagerTabAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class AccountActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        initializeBottomNavigationMenu()
        initTab()
    }

    private fun initTab(){
        viewPager = findViewById(R.id.view_pager)

        val fragmentAdapter = ViewPagerTabAdapter(supportFragmentManager)
        viewPager.adapter = fragmentAdapter

        fragmentAdapter.addFragment(ProfileActivity())
    }

    private fun initializeBottomNavigationMenu(){
        var bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.nav_profile

        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.nav_home -> {
                finish()
            }
            R.id.nav_add -> {
                val intent = Intent(this,AddPostActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
        false
    }

}
