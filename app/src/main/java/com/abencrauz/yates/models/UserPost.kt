package com.abencrauz.yates.models

import com.google.firebase.Timestamp

data class UserPost(
    var userId:String = "",
    var image:String = "",
    var description:String = "",
    var locationId:String = "",
    var timePost:Timestamp = Timestamp.now()
)