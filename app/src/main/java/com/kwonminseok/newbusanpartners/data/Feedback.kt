package com.kwonminseok.newbusanpartners.data

data class Feedback(
    val reason: String,
    val count: Long = 0,
    val details: String? = null
)
