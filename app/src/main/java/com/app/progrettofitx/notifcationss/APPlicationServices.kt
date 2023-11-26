package com.app.progrettofitx.notifcationss

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.app.progrettofitx.workMangerFit.NotificationWorker
import java.util.concurrent.TimeUnit

class APPlicationServices :Application() {
    override fun onCreate() {
        super.onCreate()

        setupWorkManager()
    }
    private fun setupWorkManager() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }
}