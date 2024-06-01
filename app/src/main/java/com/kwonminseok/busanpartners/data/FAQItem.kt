package com.kwonminseok.busanpartners.data



//data class FAQItem(val question: String, val answer: String)
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FAQItem(val question: String, val answer: String, val category: String) : Parcelable
