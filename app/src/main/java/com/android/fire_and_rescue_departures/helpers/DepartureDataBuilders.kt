package com.android.fire_and_rescue_departures.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureEntity
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureTypes
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

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

fun buildMapyComAddressLink(x: Double, y: Double): String {
    return "https://mapy.cz/turisticka?q=$y,$x"
}

fun buildDepartureAddress(departure: DepartureEntity): String {
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

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun buildDepartureShareText(departure: DepartureEntity): String {
    val isOpened = DepartureStatus.getOpened().contains(departure.state)
    val departureType = DepartureTypes.fromId(departure.type)
    val departureAddress = buildDepartureAddress(departure)
    val departureStartDateTime = getFormattedDateTime(departure.reportedDateTime)

    var text = "Výjezd hasičů "

    if (departureType != null) {
        text += "na ${departureType.name} "
    }

    if (departure.coordinateX != null && departure.coordinateY != null) {
        val addressLink = buildMapyComAddressLink(departure.coordinateX!!, departure.coordinateY!!)

        text += "na místě: $departureAddress ($addressLink) "
    }

    text += "ohlášen: $departureStartDateTime."
    text += " Stav události: ${if (isOpened) "otevřená" else "uzavřená"}"

    return text
}
