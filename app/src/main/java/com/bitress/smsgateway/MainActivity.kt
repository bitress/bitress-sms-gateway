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

                val smsSender = SmsSender(context,logAdapter, serviceActive, notificationHandler)

                if (number != null) {
                    smsSender.sendSms(number, message)
                }
            }
        }
    }



    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
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
            var permissionsGranted = false

            while (!permissionsGranted) {
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

                    permissionsGranted = permissions.all { it.isGranted }

                    if (permissionsGranted) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(TAG, getString(R.string.fetching_fcm_registration_token_failed), task.exception)
                                return@OnCompleteListener
                            }
                            // Get new FCM registration token
                            val token = task.result
                            deviceToken = token
                            val filter = IntentFilter("com.bitress.MESSAGE_RECEIVED")
                            registerReceiver(messageReceiver, filter, RECEIVER_NOT_EXPORTED)
                        })
                    } else {

                        logAdapter.addLog(Logs("Permissions aren't granted! Asking again...", System.currentTimeMillis(), true))
                        logAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, AppSettings::class.java)
            intent.putExtra("token", deviceToken)
            startActivity(intent)
        }


        binding.serverButton.setOnClickListener {
            if (serviceActive) {
                stopServer()
            } else {
                startServer()
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
        logAdapter.addLog(Logs(getString(R.string.sms_gateway_server_has_started), System.currentTimeMillis(), true))
        logAdapter.notifyDataSetChanged()
        notificationHandler = NotificationHandler(this)
        notificationHandler.showNotification(this, getString(R.string.bitress_sms_gateway_is_running), getString(R.string.gateway_service_is_currently_active), true,"gateway_init", 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun stopServer() {
        binding.serverButton.text = getString(R.string.start_server)
        serviceActive = false
        logAdapter.addLog(Logs(getString(R.string.sms_gateway_server_has_stopped), System.currentTimeMillis(), true))
        logAdapter.notifyDataSetChanged()
        notificationHandler = NotificationHandler(this)
        notificationHandler.cancelNotification(1)
    }


}