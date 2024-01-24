package com.bitress.smsgateway.utils

data class Logs(
    val logText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean
)

