package com.example.androidinterview.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

interface DeviceManager {
    fun getDeviceId(): String
}

class AndroidDeviceManager(private val context: Context) : DeviceManager {
    /**
     * Returns a stable device identifier.
     *
     * We use Settings.Secure.ANDROID_ID here because Payouts are high-value transactions
     * that require robust fraud prevention. ANDROID_ID provides a stable, app-scoped
     * identifier that survives uninstalls and data clears, which is superior to random UUIDs
     * for tracking suspicious device-level behavior in a financial context.
     */
    @SuppressLint("HardwareIds")
    override fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown"
    }
}
