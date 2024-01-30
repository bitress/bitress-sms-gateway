package com.bitress.smsgateway

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bitress.smsgateway.utils.NotificationHandler
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

class AppSettings : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationHandler: NotificationHandler

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_settings)

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        notificationHandler = NotificationHandler(this)

        val toolbar: Toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val saveButton = findViewById<Button>(R.id.saveButton)
        val deviceToken: TextInputEditText = findViewById(R.id.deviceTokenInput)
        val batteryOptimizationSwitch: SwitchMaterial = findViewById(R.id.batteryOptimizationSwitch)
        val enableNotificationSwitch = findViewById<SwitchMaterial>(R.id.enableNotificationSwitch)

        val token = intent.getStringExtra("token")
        deviceToken.setText(token)

        batteryOptimizationSwitch.isChecked =
            sharedPreferences.getBoolean("disable_battery_optimization", false)


        enableNotificationSwitch.isChecked =
            sharedPreferences.getBoolean("receive_notifications", true)

        saveButton.setOnClickListener {
            with(sharedPreferences.edit()) {
                putBoolean("receive_notifications", enableNotificationSwitch.isChecked)
                apply()
            }

            val batteryOptimizationStatus = batteryOptimizationSwitch.isChecked

            with(sharedPreferences.edit()) {
                putBoolean("disable_battery_optimization",batteryOptimizationStatus)
                apply()
            }

            if (batteryOptimizationStatus) {
                disableBatteryOptimization()
            } else {
                enableBatteryOptimization()
            }

        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun enableBatteryOptimization() {
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Unable to open battery optimization settings", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @SuppressLint("QueryPermissionsNeeded", "BatteryLife")
    private fun disableBatteryOptimization() {
        val packageName = packageName
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Unable to disable battery optimization", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
