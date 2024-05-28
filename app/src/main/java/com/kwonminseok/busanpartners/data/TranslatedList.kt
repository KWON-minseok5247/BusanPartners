package com.kwonminseok.busanpartners.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TranslatedList(
    val ko: List<String>? = emptyList(),
    val en: List<String>? = emptyList(),
    val ja: List<String>? = emptyList(),
    val zh: List<String>? = emptyList()
) : Parcelable {
    constructor(): this(emptyList(),emptyList(),emptyList(),emptyList() )
}
