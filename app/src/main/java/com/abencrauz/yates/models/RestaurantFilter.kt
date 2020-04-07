package com.abencrauz.yates.models

import android.graphics.drawable.Drawable

data class RestaurantFilter(
    var type: String,
    var name: String,
    var image: Drawable?
)