package com.abencrauz.yates.models

import com.google.firebase.firestore.GeoPoint

data class Hotel(
    var name:String = "",
    var address:String = "",
    var countryId:Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var image:String = "",
    var type:String = "",
    var phoneNumber:String = "",
    var openTime:String = "",
    var price:Int = 0
)