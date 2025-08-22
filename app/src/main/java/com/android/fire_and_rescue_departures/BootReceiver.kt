package com.android.fire_and_rescue_departures

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.helpers.ScheduleAlarmHelper

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ScheduleAlarmHelper(context).scheduleReportAlarm()
        }
    }
}
