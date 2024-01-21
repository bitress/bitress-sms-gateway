package com.bitress.smsgateway

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bitress.smsgateway.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var serviceActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.serverButton.setOnClickListener {
            if (serviceActive) {
                stopServer()
            } else {
                startServer()
            }
        }
    }

    private fun startServer() {

        binding.configInfoTextView.text = "Device ID: dsakldjalkd ajkldjald \n Secret Key: dlsajdaskldjsakldjsakldjaskld"
        binding.serverButton.text = "Stop Server"
        serviceActive = true

    }

    private fun stopServer() {

        binding.serverButton.text = "Start Server"
        serviceActive = false
        binding.configInfoTextView.text = ""
    }

}