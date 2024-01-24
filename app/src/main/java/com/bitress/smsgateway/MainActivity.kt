package com.bitress.smsgateway

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitress.smsgateway.databinding.ActivityMainBinding
import com.bitress.smsgateway.utils.LogAdapter
import com.bitress.smsgateway.utils.Logs
import com.bitress.smsgateway.utils.NotificationHandler
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.innfinity.permissionflow.lib.requestPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var serviceActive = false
    private var deviceToken = ""
    private lateinit var logAdapter : LogAdapter
    private lateinit var logRecycleView : RecyclerView
    private lateinit var notificationHandler: NotificationHandler



    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null || intent == null) {
                Log.e(TAG, "Error: Invalid context or intent.")
                return
            }

            val message = intent.getStringExtra("messageBody")
            val number = intent.getStringExtra("number")

            if (!message.isNullOrBlank()) {
                Log.e(TAG, message)

                val smsSender = SmsSender(logAdapter, serviceActive)

                if (number != null) {
                    smsSender.sendSms(number, message)
                }
            }
        }
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logRecycleView = findViewById(R.id.log_recycle_view)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        logRecycleView.layoutManager = layoutManager

        logAdapter = LogAdapter(ArrayList())
        logRecycleView.adapter = logAdapter



        CoroutineScope(Dispatchers.Main).launch {
            requestPermissions(
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            ).collect { permissions ->

                // Alternatively, you can check if all permissions are granted
                val allGranted = permissions.all { it.isGranted }
                if (allGranted) {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result

                        deviceToken = token


                        val filter = IntentFilter("com.bitress.MESSAGE_RECEIVED")
                        registerReceiver(messageReceiver, filter, RECEIVER_NOT_EXPORTED)

                    })
                } else {
                    // Not all permissions are granted, handle accordingly
                    // ...
                }
            }

        }


        binding.serverButton.setOnClickListener {
            if (serviceActive) {
                stopServer()
            } else {
                startServer()
            }
        }


        var isConfigInfoVisible = true

        binding.showConfigBtn.setOnClickListener {
            if (!isConfigInfoVisible) {
                binding.configInfoTextView.visibility = View.GONE
                binding.showConfigBtn.text = getString(R.string.show_configuration)
                isConfigInfoVisible = true
            } else {
                binding.configInfoTextView.text = getString(R.string.msg_token_fmt, deviceToken)
                binding.configInfoTextView.visibility = View.VISIBLE
                binding.showConfigBtn.text = getString(R.string.hide_configuration)
                isConfigInfoVisible = false
            }
        }

    }

    override fun onDestroy() {
        unregisterReceiver(messageReceiver)
        super.onDestroy()
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun startServer() {
        binding.serverButton.text = getString(R.string.stop_server)
        serviceActive = true
        logAdapter.addLog(Logs("SMS gateway server has started", System.currentTimeMillis(), true))
        logAdapter.notifyDataSetChanged()
        notificationHandler = NotificationHandler(this)
        notificationHandler.showNotification()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun stopServer() {
        binding.serverButton.text = getString(R.string.start_server)
        serviceActive = false
        logAdapter.addLog(Logs("SMS gateway server has stopped", System.currentTimeMillis(), true))
        logAdapter.notifyDataSetChanged()
        notificationHandler = NotificationHandler(this)
        notificationHandler.cancelNotification()
    }




}