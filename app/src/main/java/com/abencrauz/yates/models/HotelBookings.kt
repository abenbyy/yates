package com.abencrauz.yates.models

import com.google.firebase.Timestamp

data class HotelBookings (
    var userId:String = "",
    var fullname:String = "",
    var email:String = "",
    var nights:Int = 0,
    var hotelId:String = "",
    var timeBooking:Timestamp = Timestamp.now()
)