package com.owl_laugh_at_wasted_time.simplenotepad.ui.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.preferences
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import java.util.*


class NotificationHelper : Service() {

    private var notificationManager: NotificationManager? = null
    private var pendingIntent: PendingIntent? = null
    private val listOfRunningNotification: TreeSet<Int> = TreeSet()

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext, MainNoteBookActivity::class.java)
        pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getIntExtra("itemId", 0)
        val title = intent?.getStringExtra("itemTitle")
        val data = intent?.getStringExtra("data")
        val array = intent?.getStringArrayListExtra("array")
        val action = intent?.action
        if (action != null) {
            when (action) {
                "ACTION_STOP_FOREGROUND_SERVICE" -> {
                    deleteNotification(id!!)
                    listOfRunningNotification.remove(id)
                }
                "ACTION_START_FOREGROUND_SERVICE" -> {
                    listOfRunningNotification.add(id!!)
                    createNotification(id, title!!, data!!, array!!)
                }
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun createNotification(
        id: Int,
        title: String,
        time: String,
        arrayList: ArrayList<String>
    ) {
        if (preferences(applicationContext)
                .getBoolean(
                    getString
                        (
                        com.owl_laugh_at_wasted_time.settings.R.string.show_notifications_key
                    ), true
                )
        ) {

            val canal = createNotificationChannel()
            val pendingIntent: PendingIntent =
                Intent(this, MainNoteBookActivity::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(
                        this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
                .setSummaryText(time)
            for (str in arrayList) {
                inboxStyle.addLine(str)
            }
            val notification = NotificationCompat.Builder(this, canal)
                .setOngoing(true)
                .setContentTitle(title)
                .setContentText(time)
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
            if (listOfRunningNotification.size == 1) {
                notificationManager?.notify(id, notification)
          //      startForeground(id, notification)
            } else {
                notificationManager?.notify(id, notification)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "CHANNEL_ID"
        val channelName = "Notepad Reminder Background Service"
        val chain = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        chain.lightColor = Color.BLUE
        chain.importance = NotificationManager.IMPORTANCE_MIN
        chain.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager?.createNotificationChannel(chain)
        return channelId
    }

    private fun deleteNotification(id: Int) {
        notificationManager?.cancel(id)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }



}