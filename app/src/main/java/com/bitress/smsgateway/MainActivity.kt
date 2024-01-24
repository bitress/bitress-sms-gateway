package com.bitress.smsgateway

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitress.smsgateway.databinding.ActivityMainBinding
import com.bitress.smsgateway.utils.LogAdapter
import com.bitress.smsgateway.utils.Logs
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
                val logAdapter = LogAdapter(ArrayList())
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

        CoroutineScope(Dispatchers.Main).launch {
            requestPermissions(
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_NETWORK_STATE,
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
    }

    override fun onDestroy() {
        unregisterReceiver(messageReceiver)
        super.onDestroy()
    }



    private fun startServer() {
        binding.configInfoTextView.text = getString(R.string.msg_token_fmt, deviceToken)
        binding.serverButton.text = "Stop Server"
        serviceActive = true
        copyToClipboard(deviceToken)

        val logRecycleView = findViewById<RecyclerView>(R.id.log_recycle_view)
        logRecycleView.layoutManager = LinearLayoutManager(this)

        val logList = ArrayList<Logs>()

        for (i in 1..20) {
            logList.add(Logs("Item $i", System.currentTimeMillis(), true))
        }

        logAdapter = LogAdapter(logList)

        logRecycleView.adapter = logAdapter
    }

    private fun stopServer() {
        binding.serverButton.text = "Start Server"
        serviceActive = false
        binding.configInfoTextView.text = ""
    }


    private fun copyToClipboard(text: CharSequence?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("FCM Token", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(baseContext, "Token copied to clipboard", Toast.LENGTH_SHORT).show()
    }



}