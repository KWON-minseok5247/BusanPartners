package com.kwonminseok.newbusanpartners.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollegeData (
    val email: String,
    val selectedUniversity: String,
) : Parcelable {
    constructor() : this("", "",)
}