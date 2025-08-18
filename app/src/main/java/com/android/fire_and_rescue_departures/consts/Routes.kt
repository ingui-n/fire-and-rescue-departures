package com.android.fire_and_rescue_departures.consts

import com.android.fire_and_rescue_departures.data.AppRoute

object Routes {
    val DepartureMap = AppRoute(
        route = "departureMap",
        showTopBarBackButton = false,
        topBarTitle = UIText.DEPARTURES_MAP_TITLE.value
    )
    val DeparturesList = AppRoute(
        route = "departuresList",
        topBarTitle = UIText.DEPARTURES_LIST_TITLE.value
    )
    val DeparturesBookmarks = AppRoute(
        route = "departuresBookmarks",
        topBarTitle = UIText.DEPARTURES_BOOKMARKS_TITLE.value
    )
    val DepartureDetail = AppRoute(
        route = "departureDetail/{regionId}-{departureId}-{departureDateTime}",
        showTopBarBackButton = true,
        topBarTitle = UIText.DEPARTURE_DETAIL_TITLE.value
    )
    val QuestionsAndAnswers = AppRoute(
        route = "questionsAndAnswers",
        topBarTitle = UIText.QUESTIONS_AND_ANSWERS_TITLE.value
    )
    val DeparturesReport = AppRoute(
        route = "departuresReport",
        topBarTitle = UIText.DEPARTURES_REPORT_TITLE.value
    )

    val all = listOf(
        DepartureMap,
        DeparturesList,
        DeparturesBookmarks,
        DepartureDetail,
        QuestionsAndAnswers,
        DeparturesReport
    )

    fun departureDetail(regionId: Int, departureId: Long, departureDateTime: String): String {
        return "departureDetail/$regionId-$departureId-$departureDateTime"
    }

    fun getRoute(route: String?): AppRoute {
        return all.find { it.route == route } ?: DeparturesList
    }
}
