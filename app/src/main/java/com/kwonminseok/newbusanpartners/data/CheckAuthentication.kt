package com.kwonminseok.newbusanpartners.data

data class CheckAuthentication(
    val uid: String = "",
    val studentIdentificationCard: MutableList<String>? = null,
    val travelerAuthenticationImage: MutableList<String>? = null,
    val universityEmail: String? = null,
    val university: String? = null

    )
