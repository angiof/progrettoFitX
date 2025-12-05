package com.app.fityo.notifcationss

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.app.fityo.workMangerFit.NotificationWorker
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.TimeUnit

class APPlicationServices :Application() {
    override fun onCreate() {
        super.onCreate()

        setupWorkManager()
        registerWearCapability()
    }
    private fun setupWorkManager() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }

    private fun registerWearCapability() {
        Wearable.getCapabilityClient(this).addLocalCapability("fityo_sync")
    }
}
