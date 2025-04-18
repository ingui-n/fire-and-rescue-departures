package com.android.fire_and_rescue_departures.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.data.Departure
import java.time.LocalDateTime
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
    val czechLocale = Locale("cs", "CZ")
    return DateTimeFormatter
        .ofPattern("dd. MMMM HH:mm", czechLocale)
        .format(
            LocalDateTime.parse(
                (departure.reportedDateTime ?: departure.startDateTime).toString()
            )
        )
}
