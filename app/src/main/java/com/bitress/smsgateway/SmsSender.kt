package com.bitress.smsgateway

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
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



    @SuppressLint("NotifyDataSetChanged")
    fun sendSms(phoneNumber: String, message: String) {



        val sharedPreferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE)
        val receiveNotifications = sharedPreferences.getBoolean("receive_notifications", true)


        if (isGatewayOnline) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)

                logAdapter.addLog(Logs("SMS sent successfully. \n Message: $message", System.currentTimeMillis(), true))
                logAdapter.notifyDataSetChanged()

                if (receiveNotifications) {
                    notificationHandler.showNotification(
                        context,
                        notificationTitle = "SMS Sent",
                        notificationText = "Message sent successfully to $phoneNumber",
                        isOngoing = false,
                        channelID = "sms_api",
                        notificationID = 2
                    )
                }

            } catch (e: Exception) {
                val errorMessage = "Error sending SMS: ${e.message}"
                logAdapter.addLog(Logs(errorMessage, System.currentTimeMillis(), false))
                logAdapter.notifyDataSetChanged()

                Log.e(TAG, errorMessage)

                if (receiveNotifications) {
                    notificationHandler.showNotification(
                        context,
                        notificationTitle = "SMS Sending Error",
                        notificationText = errorMessage,
                        isOngoing = false,
                        channelID = "sms_api",
                        notificationID = 2
                    )
                }


            }
        } else {
            Log.d(TAG, "SMS not sent. Gateway is offline.")
            logAdapter.addLog(Logs("SMS not sent. Gateway is offline.", System.currentTimeMillis(), false))
            logAdapter.notifyDataSetChanged()

            if (receiveNotifications) {
                notificationHandler.showNotification(
                    context,
                    notificationTitle = "SMS Not Sent",
                    notificationText = "Gateway is offline. Message not sent.",
                    isOngoing = false,
                    channelID = "sms_api",
                    notificationID = 2
                )
            }


        }
    }
}
