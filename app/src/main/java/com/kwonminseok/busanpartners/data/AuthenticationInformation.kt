package com.kwonminseok.busanpartners.data

import android.net.Uri

data class AuthenticationInformation(
    val isCollegeStudent: Boolean = false,
    val isTraveler: Boolean = false,
    val studentIdentificationCard: MutableList<String>? = null,
    val travelerAuthenticationImage: MutableList<String>? = null,
    )
{
    constructor(): this(false,false,null,null)
}