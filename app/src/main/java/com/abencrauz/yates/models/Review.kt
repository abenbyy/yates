package com.abencrauz.yates.models

import com.google.firebase.Timestamp



data class Review(
    var hotelId: String?,
    var restaurantId: String?,
    var userId: String?,
    var image: String?,
    var timeReview: Timestamp?,
    var rating: Int,
    var title: String?,
    var description: String?
){
    constructor() : this("", "", "", "", Timestamp.now(), 0,"","")
}