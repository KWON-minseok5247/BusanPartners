package com.kwonminseok.busanpartners.data

data class User(
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String,
    val imagePath: String = "",
    val uid: String = "",
    val isCollegeStudent: Boolean = false,
    val isTraveler: Boolean = false,
    val gender: String? = null,
    val college: String = "",
    val introduction: String = "",
    val name: String? = ""


    ) {
    constructor(): this("","","","", "")
}
