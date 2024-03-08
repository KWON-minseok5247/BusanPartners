package com.kwonminseok.busanpartners.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val imagePath: String = "",
    val uid: String = ""

    ) {
    constructor(): this("","","","", "")
}
