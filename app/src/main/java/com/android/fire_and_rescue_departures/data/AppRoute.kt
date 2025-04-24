package com.android.fire_and_rescue_departures.data

data class AppRoute(
    val route: String,
    val showTopBar: Boolean = true,
    val showTopBarBackButton: Boolean = false,
    val topBarTitle: String = "",
    val showBottomBar: Boolean = true,
)
