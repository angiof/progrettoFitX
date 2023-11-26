package com.app.progrettofitx.workMangerFit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.dao.DaoSchede
import com.app.progrettofitx.ui.shedeForms.SchedeRepository

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Ottieni l'istanza del tuo database
        val db = DbFit.getDatabase(applicationContext)

        // Ottieni DaoSchede dal tuo database
        val daoSchede = db.schedeDao()

        // Passa DaoSchede al tuo SchedeRepository
        val repo = SchedeRepository(daoSchede)

        val schedeWithTime = repo.getSchedeWithTime()

        if (schedeWithTime.isNotEmpty()) {
            // Mostra la notifica
            showNotification()
            // ...
        }

        return Result.success()
    }


    private fun showNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            "reminderChannel",
            "Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(applicationContext, "reminderChannel")
            .setContentTitle("Promemoria")
            .setContentText("È ora di fare il tuo esercizio!")
            .setSmallIcon(R.drawable.chiusura) // Assicurati di avere questa risorsa
            .build()

        notificationManager.notify(1, notification)
    }

}
