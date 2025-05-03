package com.android.fire_and_rescue_departures.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.data.Departure
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

fun formatDescription(description: String): String {
    var description = description.trim()

    description = description.replace("DN", "dopravní nehoda")
    description = description.replace("OA", "osobní automobil")
    description = description.replace("NA", "nákladní automobil")
    description = description.replace("DOD", "dodávka")
    description = description.replace("VB", "výšková budova")
    description = description.replace("EPS", "elektronická požární signalizace")
    description = description.replace("TM", "tiskový mluvčí")
    description = description.replace("ZZS", "záchranná služba")
    description = description.replace("PČR", "policie")
    description = description.replace("AED", "automatizovaný externí defibrilátor")
    description = description.replace("PPO", "protipožární opatření")
    description = description.replace("PHM", "pohonné hmoty")
    description = description.replace("RD", "rodinný dům")
    description = description.replace("PB", "propan-butan")
    description = description.replace("EZS", "elektronický zabezpečovací systém")
    description = description.replace("JPO", "jednotky požární ochrany")
    description = description.replace("IZS", "integrovaný záchranný systém")
    description = description.replace("LZS", "letecká záchranná služba")
    description = description.replace("m2", "m²")
    description = description.replace("m3", "m³")
    description = description.replace("ZOČ", "")

    description = description.replace(" .", ".")
    description = description.replace(" ,", ",")
    description = description.replaceFirst("P ", "Požár ")
    description = description.trimEnd('.')
    description = description.trimEnd(',')

    return capitalizeFirstLetter(description)
}

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
    var pattern = "d. MMMM HH:mm"

    var convertedDateTime: LocalDateTime

    val trimmedDateTime = dateTime.trim()
    try {
        val instant = Instant.parse(trimmedDateTime)
        convertedDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    } catch (_: DateTimeParseException) {
        try {
            convertedDateTime = OffsetDateTime.parse(trimmedDateTime).toLocalDateTime()
        } catch (_: DateTimeParseException) {
            convertedDateTime = LocalDateTime.parse(trimmedDateTime)
        }
    }


    if (convertedDateTime.year != LocalDateTime.now().year) {
        pattern = "d. MMMM yyyy HH:mm"
    }

    return DateTimeFormatter
        .ofPattern(pattern, czechLocale)
        .format(convertedDateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDateTimeFromString(dateTime: String): LocalDateTime {
    val trimmedDateTime = dateTime.trim()
    try {
        val instant = Instant.parse(trimmedDateTime)
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    } catch (_: DateTimeParseException) {
        return try {
            OffsetDateTime.parse(trimmedDateTime).toLocalDateTime()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(trimmedDateTime)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDateTime(dateTime: Long, pattern: String = "d. MMMM HH:mm"): String {
    val czechLocale = Locale("cs", "CZ")
    val instant = Instant.ofEpochMilli(dateTime)
    val zoneId = ZoneId.of("Europe/Prague")
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)

    val formatter = DateTimeFormatter.ofPattern(pattern, czechLocale)
    return localDateTime.format(formatter)
}
