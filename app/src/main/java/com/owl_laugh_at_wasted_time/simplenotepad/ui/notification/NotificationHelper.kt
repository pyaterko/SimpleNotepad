package com.owl_laugh_at_wasted_time.simplenotepad.ui.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity

class NotificationHelper() : Service() {

    private var builder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val intent = Intent(applicationContext, MainNoteBookActivity::class.java)
        pendingIntent =
            PendingIntent.getActivity(this, 0, intent, 0)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        builder = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val id = intent?.getIntExtra("itemId", 0)
        val title = intent?.getStringExtra("itemTitle")
        val data = intent?.getStringExtra("data")

        val action = intent?.action
        if (action != null) {
            when (action) {
                "ACTION_STOP_FOREGROUND_SERVICE" -> {
                    stopForeground(true)
                    deleteNotification(id!!)
                }
                "ACTION_START_FOREGROUND_SERVICE" -> {
                    createNotification(id!!, title!!, data!!)
                }
            }
        }
        return START_STICKY
    }

    fun createNotification(id: Int, title: String, time: String) {
        builder!!.setContentTitle(title )
            .setContentText(time)
            .setOngoing(true)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.drawable.ic_baseline_notifications)
            .setContentIntent(pendingIntent)
       notificationManager?.notify(id, builder!!.build())
    }

    fun deleteNotification(id: Int) {
        notificationManager?.cancel(id)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val alarmService =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText ="channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).
            apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}