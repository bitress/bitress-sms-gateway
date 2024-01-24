package com.bitress.smsgateway

import android.content.ContentValues.TAG
import android.telephony.SmsManager
import android.util.Log
import com.bitress.smsgateway.utils.LogAdapter
import com.bitress.smsgateway.utils.Logs

class SmsSender(private val logAdapter: LogAdapter, private val isGatewayOnline: Boolean) {

    fun sendSms(phoneNumber: String, message: String) {
        if (isGatewayOnline) {
            try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            logAdapter.addLog(Logs("SMS sent successfully. \n Message: $message", System.currentTimeMillis(), true))
                logAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            val errorMessage = "Error sending SMS: ${e.message}"
            logAdapter.addLog(Logs(errorMessage, System.currentTimeMillis(), false))
                logAdapter.notifyDataSetChanged()

                Log.e(TAG, errorMessage)
        }
    } else {
            Log.d(TAG, "SMS not sent. Gateway is offline.")
            logAdapter.addLog(Logs("SMS not sent. Gateway is offline.", System.currentTimeMillis(), false))
            logAdapter.notifyDataSetChanged()

        }
    }
}
