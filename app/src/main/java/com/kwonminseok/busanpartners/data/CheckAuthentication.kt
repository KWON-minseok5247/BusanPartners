package com.kwonminseok.busanpartners.data

data class CheckAuthentication(
    val uid: String = "",
    val studentIdentificationCard: MutableList<String>? = null,
    val travelerAuthenticationImage: MutableList<String>? = null,

    )
