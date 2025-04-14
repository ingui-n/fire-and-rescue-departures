package com.android.fire_and_rescue_departures.consts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
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
        Routes.DEPARTURES_LIST
    )

    object Map : BottomNavItem(
        UIText.DEPARTURES_MAP_TITLE_SHORT.value,
        Icons.Filled.LocationOn,
        Icons.Outlined.LocationOn,
        Routes.DEPARTURES_MAP
    )

    object Bookmarks : BottomNavItem(
        UIText.DEPARTURES_BOOKMARKS_TITLE_SHORT.value,
        Icons.Filled.Star,
        Icons.Outlined.Star,
        Routes.DEPARTURES_BOOKMARKS
    )
}
