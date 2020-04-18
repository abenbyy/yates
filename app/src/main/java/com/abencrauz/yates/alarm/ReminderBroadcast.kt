package com.abencrauz.yates.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abencrauz.yates.R

class ReminderBroadcast : BroadcastReceiver() {
    var id = 0
    override fun onReceive(context: Context?, intent: Intent?) {
        val builder = NotificationCompat.Builder(context!!, "notifyMe")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Miss You!")
            .setContentText("You haven't login for a while")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(id, builder.build())
        id++
        Log.d("get_notif", "now")
    }
}