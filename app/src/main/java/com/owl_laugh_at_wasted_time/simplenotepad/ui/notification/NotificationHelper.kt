package com.owl_laugh_at_wasted_time.simplenotepad.ui.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.preferences
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import java.util.*


class NotificationHelper() : Service() {

    private var notificationManager: NotificationManager? = null
    private var pendingIntent: PendingIntent? = null
    private val listOfRunningNotifikation: TreeSet<Int> = TreeSet()

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
                    listOfRunningNotifikation.remove(id)
                    if (listOfRunningNotifikation.size == 0) {
                        stopForeground(true)
                        stopSelf()
                    }
                }
                "ACTION_START_FOREGROUND_SERVICE" -> {
                    listOfRunningNotifikation.add(id!!)
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
            if (listOfRunningNotifikation.size == 1) {
                startForeground(id, notification)
            } else {
                notificationManager?.notify(id, notification)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "CHANNEL_ID"
        val channelName = "Notepad Reminder Background Service"
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_MIN
        )
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_MIN
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager?.createNotificationChannel(chan)
        return channelId
    }

    fun deleteNotification(id: Int) {
        notificationManager?.cancel(id)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}