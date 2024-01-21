package com.bitress.smsgateway

import android.app.Application
import android.content.SharedPreferences
import java.util.*

open class SmsGatewayApplication : Application() {
    companion object {
        lateinit var app: SmsGatewayApplication
        lateinit var secret: String
    }

    private lateinit var sp: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        app = this
        sp = getSharedPreferences("pref", 0)
        secret = sp.getString("secret", UUID.randomUUID().toString()) ?: ""
        sp.edit().putString("secret", secret).apply()
    }

}
