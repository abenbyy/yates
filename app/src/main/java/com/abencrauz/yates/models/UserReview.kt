package com.abencrauz.yates.models

import com.google.firebase.Timestamp

data class UserReview(
    var userId:String = "",
    var image:String = "",
    var hotelId:String = "",
    var restaurantId:String = "",
    var rating:Double = 0.0,
    var description:String = "",
    var timeReview:Timestamp = Timestamp.now()
)