package com.android.fire_and_rescue_departures.consts

import com.android.fire_and_rescue_departures.data.AppRoute

object Routes {
    val DepartureMap = AppRoute("departureMap", false, true)
    val DeparturesList = AppRoute("departuresList", false, true)
    val DeparturesBookmarks = AppRoute("departuresBookmarks", false, true)
    val DepartureDetail = AppRoute("departureDetail/{departureId}-{departureDateTime}", true, false)

    val all = listOf(
        DepartureMap,
        DeparturesList,
        DeparturesBookmarks,
        DepartureDetail
    )

    fun departureDetail(departureId: Long, departureDateTime: String): String {
        return "departureDetail/$departureId-$departureDateTime"
    }

    fun getRoute(route: String?): AppRoute {
        return all.find { it.route == route } ?: DeparturesList
    }
}
