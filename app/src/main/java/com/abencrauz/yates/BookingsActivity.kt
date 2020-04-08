package com.abencrauz.yates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.abencrauz.yates.adapters.BookingsPagerAdapter
import com.google.android.material.tabs.TabLayout

class BookingsActivity : AppCompatActivity() {

    private lateinit var tbLayout: TabLayout
    private lateinit var vpView : ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)

        tbLayout = findViewById(R.id.tb_layout)
        vpView = findViewById(R.id.vp_view)

        tbLayout.addTab(tbLayout.newTab().setText(R.string.restaurant_bookings))
        tbLayout.addTab(tbLayout.newTab().setText(R.string.hotel_bookings))
        tbLayout.tabGravity = TabLayout.GRAVITY_FILL

        vpView.apply {
            adapter = BookingsPagerAdapter(this@BookingsActivity, supportFragmentManager, tbLayout.tabCount)
        }


        vpView.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tbLayout))

        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                vpView.currentItem = tab!!.position
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

        })
    }
}
