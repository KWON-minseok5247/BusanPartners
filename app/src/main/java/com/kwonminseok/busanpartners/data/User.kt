package com.kwonminseok.busanpartners.data

import android.net.Uri
import com.google.android.gms.auth.api.Auth

data class User(
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String,
    val imagePath: String = "",
    val uid: String = "",
    val gender: String? = null,
    val college: String = "",
    val introduction: String = "",
    val name: String? = "",
    val authentication: AuthenticationInformation? = null,
    ) {
    constructor(): this("","","","", "")
}
