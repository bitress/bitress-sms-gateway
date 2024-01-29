package com.bitress.smsgateway

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.bitress.smsgateway.R
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

        val toolbar: Toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }



        val deviceToken: TextInputEditText = findViewById(R.id.deviceTokenInput)

        val token = intent.getStringExtra("token")
        deviceToken.setText(token)







        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        notificationHandler = NotificationHandler(this)

        val enableNotificationSwitch = findViewById<SwitchMaterial>(R.id.enableNotificationSwitch)
        val saveButton = findViewById<Button>(R.id.saveButton)

        enableNotificationSwitch.isChecked = sharedPreferences.getBoolean("receive_notifications", true)

        saveButton.setOnClickListener {
            with(sharedPreferences.edit()) {
                putBoolean("receive_notifications", enableNotificationSwitch.isChecked)
                apply()
            }
        }
    }
}
