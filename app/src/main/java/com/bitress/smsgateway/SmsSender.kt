package com.bitress.smsgateway

import android.content.Context
import android.telephony.SmsManager
import android.util.Log

class SmsSender(private val context: Context) {
    private val TAG = "SmsSender"

    fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d(TAG, "SMS sent successfully ")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SMS: ${e.message}")
        }
    }
}
