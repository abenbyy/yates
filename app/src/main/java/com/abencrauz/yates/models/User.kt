package com.abencrauz.yates.models

data class User (
    var fullname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var password: String? = null,
    var description: String? = null,
    var image: String? = null
)