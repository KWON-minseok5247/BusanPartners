package com.kwonminseok.newbusanpartners.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TranslatedText(
    val ko: String,
    val en: String? = null,
    val ja: String? = null,
    val zh: String? = null,
    val es: String? = null
) : Parcelable {
    constructor(): this("", "", "", "", "")
}
