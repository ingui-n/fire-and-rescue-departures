package com.android.fire_and_rescue_departures.helpers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import com.android.fire_and_rescue_departures.R
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

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun getFormattedDepartureStartDateTime(departure: Departure): String {
    return getFormattedDateTime(
        (departure.reportedDateTime ?: departure.startDateTime).toString()
    )
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun getDepartureStartDateTime(departure: Departure): String {
    return (departure.reportedDateTime ?: departure.startDateTime).toString()
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun getDepartureStartDateTime(departure: Departure?): LocalDateTime? {
    if (departure == null)
        return null

    if (departure.startDateTime != null) {
        return getDateTimeFromString(departure.startDateTime)
    } else if (departure.reportedDateTime != null) {
        return getDateTimeFromString(departure.reportedDateTime)
    }

    return null
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun getFormattedDateTime(dateTime: String): String {
    val czechLocale = Locale.forLanguageTag("cs-CZ")
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

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
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

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun getFormattedDateTime(dateTime: Long, pattern: String = "d. MMMM HH:mm"): String {
    val czechLocale = Locale.forLanguageTag("cs-CZ")
    val instant = Instant.ofEpochMilli(dateTime)
    val zoneId = ZoneId.of("Europe/Prague")
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)

    val formatter = DateTimeFormatter.ofPattern(pattern, czechLocale)
    return localDateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun longToIsoString(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    return DateTimeFormatter.ISO_INSTANT.format(instant)
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun getDateTimeLongFromString(dateTime: String): Long {
    val trimmedDateTime = dateTime.trim()
    return try {
        Instant.parse(trimmedDateTime).toEpochMilli()
    } catch (_: DateTimeParseException) {
        try {
            OffsetDateTime.parse(trimmedDateTime).toInstant().toEpochMilli()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(trimmedDateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }
}

fun trimRegionFromString(region: String): String {
    return region.lowercase()
        .replace("kraj", "")
        .trim()
        .replaceFirstChar { it.uppercase() }
}

fun getIconByType(context: Context, type: Int, size: Int = 48, grayed: Boolean = false): Drawable {
    data class DrawableIcon(val drawable: Int, val tint: Int)

    val icon = when (type) {
        3100 -> DrawableIcon(R.drawable.fire, Color.RED)
        3200 -> DrawableIcon(R.drawable.car, Color.BLUE)
        3400 -> DrawableIcon(R.drawable.water, Color.GREEN)
        3500 -> DrawableIcon(R.drawable.axe, Color.rgb(139, 69, 19))
        3550 -> DrawableIcon(R.drawable.person_standing, Color.rgb(191, 143, 17))
        3700 -> DrawableIcon(R.drawable.circle_alert, Color.MAGENTA)
        3600 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        3900 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        3800 -> DrawableIcon(R.drawable.bell_off, Color.rgb(102, 102, 0))
        5000 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        else -> DrawableIcon(R.drawable.siren, Color.RED)
    }

    val drawable = ContextCompat.getDrawable(context, icon.drawable)?.mutate()
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    drawable?.setTint(if (grayed) Color.GRAY else icon.tint)
    drawable?.setBounds(0, 0, size, size)
    drawable?.draw(canvas)

    return bitmap.toDrawable(context.resources)
}

fun getDrawableByType(type: Int): Int {
    return when (type) {
        3100 -> R.drawable.fire
        3200 -> R.drawable.car
        3400 -> R.drawable.water
        3500 -> R.drawable.axe
        3550 -> R.drawable.person_standing
        3700 -> R.drawable.circle_alert
        3600 -> R.drawable.circle_alert
        3900 -> R.drawable.circle_alert
        3800 -> R.drawable.bell_off
        5000 -> R.drawable.circle_alert
        else -> R.drawable.siren
    }
}
