package com.kwonminseok.newbusanpartners.data

data class MessageReport(
    val reportedBy: String? = "",
    val reportedUser: String? = "",
    val reason: String,
    val timestamp: com.google.firebase.Timestamp,
    val status: String = "pending"
)
