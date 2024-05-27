package com.kwonminseok.busanpartners.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TranslatedList(
    val ko: List<String>? = null,
    val en: List<String>? = null,
    val ja: List<String>? = null,
    val zh: List<String>? = null
) : Parcelable {
    constructor(): this(null,null,null,null )
}
