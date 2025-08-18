package com.android.fire_and_rescue_departures.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.MainApplication
import com.android.fire_and_rescue_departures.repository.AlarmReportRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin

class ReportAlarm : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    override fun onReceive(context: Context, intent: Intent) {
        val koin = (context.applicationContext as MainApplication).getKoin()
        val alarmReportRepository = koin.get<AlarmReportRepository>()

        CoroutineScope(Dispatchers.IO).launch {
            alarmReportRepository.triggerReport()
        }
    }
}
