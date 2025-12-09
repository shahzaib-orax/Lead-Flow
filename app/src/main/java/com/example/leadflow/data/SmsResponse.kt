package com.example.leadflow.data

data class SmsResponse(
    val success: Boolean,
    val number: String,
    val text: String
)
