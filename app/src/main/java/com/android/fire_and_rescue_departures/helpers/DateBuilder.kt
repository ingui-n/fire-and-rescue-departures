package com.android.fire_and_rescue_departures.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
fun buildDateTimeStringFromPickers(date: DatePickerState, time: TimePickerState): String {
    val instant = Instant.ofEpochMilli(date.selectedDateMillis!!)
    val zoneId = ZoneId.systemDefault()
    val localDate = instant.atZone(zoneId).toLocalDate()

    val hour = time.hour
    val minute = time.minute

    val localDateTime = LocalDateTime.of(
        localDate.year,
        localDate.month,
        localDate.dayOfMonth,
        hour,
        minute
    )

    return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
fun buildDateTimeFromPickers(date: DatePickerState, time: TimePickerState): LocalDateTime {
    val instant = Instant.ofEpochMilli(date.selectedDateMillis!!)
    val zoneId = ZoneId.systemDefault()
    val localDate = instant.atZone(zoneId).toLocalDate()

    val hour = time.hour
    val minute = time.minute

    val localDateTime = LocalDateTime.of(
        localDate.year,
        localDate.month,
        localDate.dayOfMonth,
        hour,
        minute
    )

    return localDateTime
}
