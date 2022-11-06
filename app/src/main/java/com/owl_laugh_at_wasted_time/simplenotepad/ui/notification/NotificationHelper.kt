package com.owl_laugh_at_wasted_time.simplenotepad.ui.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainActivity

class NotificationHelper(context: Context) {

    private var builder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var pendingIntent: PendingIntent? = null

    init {
        builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_baseline_notifications)
        notificationManager = NotificationManagerCompat.from(context)
        val intent = Intent(context, MainActivity::class.java)
        pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    fun createNotification(id:Int,title: String, time: String) {
        builder!!.setContentTitle("$title  $time")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        notificationManager!!.notify(id, builder!!.build())
    }

    fun deleteNotification(id:Int){
        notificationManager?.cancel(id)
    }
}