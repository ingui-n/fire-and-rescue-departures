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

fun buildDepartureSmallAddress(departure: Departure): String {
    val listOfText = mutableListOf<String>()

    if (departure.region.name != null)
        listOfText.add(departure.region.name)
    if (departure.municipality != null)
        listOfText.add(departure.municipality)
    if (departure.street != null)
        listOfText.add(departure.street)
    if (departure.road != null)
        listOfText.add(departure.road)

    return listOfText.joinToString(" • ")
}

@RequiresApi(Build.VERSION_CODES.O)
fun buildDepartureShareText(departure: Departure): String {
    val isOpened = DepartureStatus.getOpened().contains(departure.state)
    val departureType = DepartureTypes.fromId(departure.type)
    val departureAddress = buildDepartureAddress(departure)
    val departureStartDateTime =
        getFormattedDepartureStartDateTime(departure)

    var text = "Výjezd hasičů "

    if (departureType != null) {
        text += "na ${departureType.name} "
    }

    if (departure.gis1 != null && departure.gis2 != null) {
        val coordinates = convertSjtskToWgs(
            departure.gis1.toDouble(),
            departure.gis2.toDouble()
        )
        val addressLink = buildMapyComAddressLink(coordinates)

        text += "na místě: $departureAddress ($addressLink) "
    }

    text += "ohlášen: $departureStartDateTime."
    text += " Stav události: ${if (isOpened) "otevřená" else "uzavřená"}"

    return text
}
