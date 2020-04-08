package com.abencrauz.yates.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.abencrauz.yates.HotelBookingsFragment
import com.abencrauz.yates.RestaurantBookingFragment

class BookingsPagerAdapter(private val context: Context, fm: FragmentManager, internal var totalTabs: Int): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0->{
                return RestaurantBookingFragment()
            }
            1->{
                return HotelBookingsFragment()
            }

        }
        return HotelBookingsFragment()
    }

    override fun getCount(): Int {
        return totalTabs
    }
}