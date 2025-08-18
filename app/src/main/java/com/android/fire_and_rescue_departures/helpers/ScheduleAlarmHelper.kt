package com.android.fire_and_rescue_departures.helpers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.alarms.ReportAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java
import com.android.fire_and_rescue_departures.BuildConfig

class ScheduleAlarmHelper(
    private val context: Context,
) {
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun scheduleReportAlarm() {
        CoroutineScope(Dispatchers.IO).launch {
            val channel = NotificationChannel(
                "report_channel",
                "Report Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager =
                context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReportAlarm::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 5 minutes interval
            val intervalMillis = 60_000L * BuildConfig.REPORT_INTERVAL_MINUTES.toLong()
            val startTime = System.currentTimeMillis() + intervalMillis

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                startTime,
                intervalMillis,
                pendingIntent
            )
        }
    }

    fun cancelReportAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReportAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun rescheduleStatusAlarm() {
        cancelReportAlarm()
        scheduleReportAlarm()
    }
}
