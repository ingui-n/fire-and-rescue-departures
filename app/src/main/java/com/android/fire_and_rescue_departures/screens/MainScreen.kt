package com.android.fire_and_rescue_departures.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.fire_and_rescue_departures.Navigation
import com.android.fire_and_rescue_departures.consts.BottomNavItem
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.layouts.BottomBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem.Departures,
        BottomNavItem.Map,
        BottomNavItem.Bookmarks,
    )

    Scaffold(
        //todo move top bar here
        bottomBar = {
            if (Routes.getRoute(currentRoute).showBottomBar) {
                BottomBar(navController, currentRoute, items)
            }
        }
    ) { innerPadding ->
        Navigation(navController = navController, innerPadding = innerPadding)
    }
}
