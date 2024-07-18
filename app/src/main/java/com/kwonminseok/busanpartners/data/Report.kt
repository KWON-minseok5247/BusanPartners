package com.kwonminseok.busanpartners.data

data class Report(
    val reportedBy: String,
    val reportedUser: String,
    val reason: String,
    val details: String?,
    val chipGroup: String?,
    val timestamp: com.google.firebase.Timestamp,
    val status: String = "pending"
)
