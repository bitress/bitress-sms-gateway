package com.bitress.smsgateway.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bitress.smsgateway.R

class NotificationHandler(private val context: Context) {

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "sms_gateway_channel"

    @SuppressLint("ObsoleteSdkInt")
    fun showNotification(context: Context, notificationTitle: String, notificationText: String, isOngoing: Boolean) {
        val CHANNEL_ID = "Your_Channel_ID"
        val NOTIFICATION_ID = 1

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sms Gateway Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification`
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.bitress_logo)
            .setOngoing(isOngoing)
            .build()

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    fun cancelNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
