package com.android.fire_and_rescue_departures.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureTypes
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.locationtech.proj4j.ProjCoordinate

fun buildDeparturesUrl(
    baseUrl: String,
    fromDateTime: String? = null,
    toDateTime: String? = null,
    status: List<Int>? = DepartureStatus.getAllIds()
): String {
    val urlBuilder = "${baseUrl}api".toHttpUrlOrNull()!!.newBuilder()
    fromDateTime?.let { urlBuilder.addQueryParameter("casOd", it) }
    toDateTime?.let { urlBuilder.addQueryParameter("casDo", it) }
    status?.forEach { urlBuilder.addQueryParameter("stavIds", it.toString()) }

    return urlBuilder.build().toString()
}

fun buildMapyComAddressLink(coordinates: ProjCoordinate): String {
    return "https://mapy.cz/turisticka?q=${coordinates.y},${coordinates.x}"
}

fun buildDepartureAddress(departure: Departure): String {
    var text = ""

    if (departure.municipality != null) {
        text += departure.municipality
    }
    if (departure.street != null) {
        if (!text.endsWith(" "))
            text += " "
        text += departure.street
    }
    if (departure.road != null) {
        if (!text.endsWith(" "))
            text += " "
        text += departure.road
    }

    return text
}

@RequiresApi(Build.VERSION_CODES.O)
fun buildDepartureShareText(departure: Departure): String {
    val departureStatus = DepartureStatus.fromId(departure.state)
    val departureType = DepartureTypes.fromId(departure.type)
    val departureAddress = buildDepartureAddress(departure)
    val departureStartDateTime =
        getFormattedDepartureStartDateTime(departure)
    val coordinates = convertSjtskToWgs(
        departure.gis1.toDouble(),
        departure.gis2.toDouble()
    )
    val addressLink = buildMapyComAddressLink(coordinates)

    var text = "Výjezd hasičů "

    if (departureType != null) {
        text += "na ${departureType.name} "
    }

    text += "na místě: $departureAddress ($addressLink) ohlášen: $departureStartDateTime."

    if (departureStatus != null)
        text += " Stav události: ${departureStatus.name}"

    return text
}
