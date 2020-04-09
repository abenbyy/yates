package com.abencrauz.yates.models

import com.google.firebase.Timestamp
import java.util.*

data class RestaurantBooking(
    var restaurantId: String?,
    var userId: String?,
    var name: String?,
    var seats: Int,
    var time: Timestamp?,
    var email: String
){
    constructor() : this("", "", "", 0, null, "")
}