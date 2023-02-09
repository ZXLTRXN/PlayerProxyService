package com.example.localproxy.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.localproxy.MainActivity
import com.example.localproxy.R

class NotificationHelper(context: Context) {
    private val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val activityIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, SILENT_CHANNEL_ID)
    .setContentIntent(activityIntent)
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .setAutoCancel(true)

    fun getNotification(): Notification {
        createNotificationChannelIfNecessary()
        return notification.build()
    }

    private fun createNotificationChannelIfNecessary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SILENT_CHANNEL_ID,
                SILENT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = SILENT_CHANNEL_DESCRIPTION
                setSound(null, null)
            }
            manager.createNotificationChannels(listOf(channel))
        }
    }

    companion object {
        const val NOTIFICATION_ID = 99
        const val SILENT_CHANNEL_ID = "IntTimerChannel"
        const val SILENT_CHANNEL_NAME = "IntTimerChannelName"
        const val SILENT_CHANNEL_DESCRIPTION = "Interval timer silent channel"
    }
}