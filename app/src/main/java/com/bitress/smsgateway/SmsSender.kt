package com.bitress.smsgateway

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import com.bitress.smsgateway.utils.LogAdapter
import com.bitress.smsgateway.utils.Logs
import com.bitress.smsgateway.utils.NotificationHandler

class SmsSender(
    private val context: Context,
    private val logAdapter: LogAdapter,
    private val isGatewayOnline: Boolean,
    private val notificationHandler: NotificationHandler
) {

    companion object {
        const val SMS_SENT_ACTION = "SMS_SENT"

        // Notification constants
        const val NOTIFICATION_CHANNEL_ID = "sms_api"
        const val NOTIFICATION_ID_SMS_SENT = 2
    }

    private val sentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SMS_SENT_ACTION) {
                val resultCode = resultCode
                handleSmsResult(resultCode)
            }
        }
    }

    init {
        // Register the BroadcastReceiver in the constructor
        context.registerReceiver(sentReceiver, IntentFilter(SMS_SENT_ACTION))
    }

    private fun unregisterReceiver() {
        context.unregisterReceiver(sentReceiver)
    }



    private fun handleSmsResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            logSuccess("SMS sent successfully")
            unregisterReceiver()
        } else {
            logError("Error sending SMS. Result code: $resultCode")
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun sendSms(phoneNumber: String, message: String) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val receiveNotifications = sharedPreferences.getBoolean("receive_notifications", true)

        if (isGatewayOnline) {
            try {
                val sentIntent = PendingIntent.getBroadcast(context, 0, Intent(SMS_SENT_ACTION), 0)
                sendSmsWithIntent(phoneNumber, message, sentIntent)

                logSuccess("SMS sent successfully. \n Message: $message")

                if (receiveNotifications) {
                    notificationHandler.showNotification(
                        context,
                        "SMS Sent",
                        "Message sent successfully to $phoneNumber",
                        false,
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_ID_SMS_SENT
                    )
                }
            } catch (e: Exception) {
                logError("Error sending SMS: ${e.message}")

                if (receiveNotifications) {
                    notificationHandler.showNotification(
                        context,
                        "SMS Sending Error",
                        "Error sending SMS: ${e.message}",
                        false,
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_ID_SMS_SENT
                    )
                }
            }
        } else {
            logError("SMS not sent. Gateway is offline.")

            if (receiveNotifications) {
                notificationHandler.showNotification(
                    context,
                    "SMS Not Sent",
                    "Gateway is offline. Message not sent.",
                    false,
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_ID_SMS_SENT
                )
            }
        }

//        unregisterReceiver()
    }

    private fun sendSmsWithIntent(phoneNumber: String, message: String, sentIntent: PendingIntent) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, null)
    }

    private fun logSuccess(message: String) {
        Log.d(TAG, message)
        logAdapter.addLog(Logs(message, System.currentTimeMillis(), true))
        logAdapter.notifyDataSetChanged()
    }

    private fun logError(message: String) {
        Log.e(TAG, message)
        logAdapter.addLog(Logs(message, System.currentTimeMillis(), false))
        logAdapter.notifyDataSetChanged()
    }

}
