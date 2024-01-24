package com.bitress.smsgateway

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            if (remoteMessage.data.containsKey("message")) {
                val message = remoteMessage.data["message"]
                val to = remoteMessage.data["to"]

                // Send a broadcast with the message body
                val intent = Intent("com.bitress.MESSAGE_RECEIVED")
                intent.putExtra("messageBody", message)
                intent.putExtra("number", to)
                sendBroadcast(intent)

            }
        }
    }



    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

}