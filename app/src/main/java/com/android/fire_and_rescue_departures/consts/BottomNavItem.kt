package com.android.fire_and_rescue_departures.consts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val activeIcon: ImageVector,
    val defaultIcon: ImageVector,
    val screenRoute: String
) {
    object Departures : BottomNavItem(
        UIText.DEPARTURES_LIST_TITLE_SHORT.value,
        Icons.Filled.Search,
        Icons.Outlined.Search,
        Routes.DeparturesList.route
    )

    object Map : BottomNavItem(
        UIText.DEPARTURES_MAP_TITLE_SHORT.value,
        Icons.Filled.LocationOn,
        Icons.Outlined.LocationOn,
        Routes.DepartureMap.route
    )

    object Bookmarks : BottomNavItem(
        UIText.DEPARTURES_BOOKMARKS_TITLE_SHORT.value,
        Icons.Filled.Bookmark,
        Icons.Outlined.BookmarkBorder,
        Routes.DeparturesBookmarks.route
    )

    object QuestionsAndAnswers : BottomNavItem(
        UIText.QUESTIONS_AND_ANSWERS_TITLE_SHORT.value,
        Icons.Filled.QuestionMark,
        Icons.Outlined.QuestionMark,
        Routes.QuestionsAndAnswers.route
    )

    object Report : BottomNavItem(
        UIText.DEPARTURES_REPORT_TITLE_SHORT.value,
        Icons.Filled.Notifications,
        Icons.Outlined.Notifications,
        Routes.DeparturesReport.route
    )
}
