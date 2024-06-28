package com.kwonminseok.busanpartners.data

data class Feedback(
    val reason: String,
    val count: Long = 0,
    val details: String? = null
)
