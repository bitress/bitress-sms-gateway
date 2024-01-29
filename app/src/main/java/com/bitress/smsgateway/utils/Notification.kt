package com.bitress.smsgateway.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bitress.smsgateway.R

class NotificationHandler(private val context: Context) {



    @SuppressLint("ObsoleteSdkInt")
    fun showNotification(context: Context, notificationTitle: String, notificationText: String, isOngoing: Boolean, channelID: String, notificationID: Int) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "SMS Gateway",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification`
        val notification = NotificationCompat.Builder(context, channelID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.bitress_logo)
            .setOngoing(isOngoing)
            .build()

        // Show the notification
        notificationManager.notify(notificationID, notification)
    }


    fun cancelNotification(notificationID: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationID)
    }
}
