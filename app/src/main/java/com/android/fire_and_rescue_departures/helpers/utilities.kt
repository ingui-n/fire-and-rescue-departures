package com.android.fire_and_rescue_departures.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.data.Departure
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun capitalizeFirstLetter(string: String): String {
    return if (string.isEmpty()) {
        string
    } else {
        string.substring(0, 1).uppercase() + string.substring(1)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDepartureStartDateTime(departure: Departure): String {
    return getFormattedDateTime(
        (departure.reportedDateTime ?: departure.startDateTime).toString()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDateTime(dateTime: String): String {
    val czechLocale = Locale("cs", "CZ")
    var pattern = "dd. MMMM HH:mm"

    val convertedDateTime = LocalDateTime.parse(dateTime)

    if (convertedDateTime.year != LocalDateTime.now().year) {
        pattern = "dd. MMMM yyyy HH:mm"
    }

    return DateTimeFormatter
        .ofPattern(pattern, czechLocale)
        .format(
            LocalDateTime.parse(
                dateTime
            )
        )
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDateTime(dateTime: Long, pattern: String = "dd. MMMM HH:mm"): String {
    val czechLocale = Locale("cs", "CZ")
    val instant = Instant.ofEpochMilli(dateTime)
    val zoneId = ZoneId.of("Europe/Prague")
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)

    val formatter = DateTimeFormatter.ofPattern(pattern, czechLocale)
    return localDateTime.format(formatter)
}
