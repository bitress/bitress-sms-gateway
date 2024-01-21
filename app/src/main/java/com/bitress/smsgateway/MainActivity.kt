package com.bitress.smsgateway

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitress.smsgateway.databinding.ActivityMainBinding
import com.innfinity.permissionflow.lib.requestPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var serviceActive = false

    private val prefName = "pref"
    private val isServerOnline = "isServerOnline"

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
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            ).collect { permissions ->
                // Iterate over the permissions and check if the specific permission is granted
                val isSendSmsGranted = permissions.any { it.permission == Manifest.permission.SEND_SMS && it.isGranted }

                if (isSendSmsGranted) {
                    // SEND_SMS permission is granted, proceed with the logic
                    // ...
                } else {
                    // SEND_SMS permission is not granted, handle accordingly
                    // ...
                }

                // Alternatively, you can check if all permissions are granted
                val allGranted = permissions.all { it.isGranted }
                if (allGranted) {
                    // All permissions are granted, proceed with the logic
                    // ...
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



    private fun startServer() {
        // Set isServerOnline to true in SharedPreferences
        setServerStatus(true)

        // Update UI or perform other actions with the secretKey
        binding.configInfoTextView.text = "Device ID: dsakldjalkd ajkldjald \n Secret Key: to be added"
        binding.serverButton.text = "Stop Server"
        serviceActive = true
    }

    private fun stopServer() {
        // Set isServerOnline to false in SharedPreferences
        setServerStatus(false)

        binding.serverButton.text = "Start Server"
        serviceActive = false
        binding.configInfoTextView.text = ""
    }

    private fun setServerStatus(isOnline: Boolean) {
        val prefs = getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(isServerOnline, isOnline)
        editor.apply()
    }

    private fun isServerOnline(): Boolean {
        val prefs = getSharedPreferences(prefName, Context.MODE_PRIVATE)
        return prefs.getBoolean(isServerOnline, false)
    }

}