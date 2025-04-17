package com.android.fire_and_rescue_departures.consts

object Routes {
    const val DEPARTURES_MAP = "departuresMap"
    const val DEPARTURES_LIST = "departuresList"
    const val DEPARTURES_BOOKMARKS = "departuresBookmarks"
    const val DEPARTURE_DETAIL = "departureDetail/{departureId}-{departureDateTime}"

    fun departureDetail(departureId: Long, departureDateTime: String): String {
        return "departureDetail/$departureId-$departureDateTime"
    }
}
