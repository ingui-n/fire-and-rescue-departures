package com.android.fire_and_rescue_departures.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.fire_and_rescue_departures.consts.BottomNavItem
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.layouts.BottomBar
import com.android.fire_and_rescue_departures.layouts.DepartureDetailTopBar
import com.android.fire_and_rescue_departures.layouts.DepartureListTopBar
import com.android.fire_and_rescue_departures.layouts.DepartureMapTopBar
import com.android.fire_and_rescue_departures.layouts.TopBar
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
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
        BottomNavItem.QuestionsAndAnswers
    )

    val departureListViewModel: DeparturesListViewModel = koinViewModel()

    Scaffold(
        topBar = {
            if (Routes.getRoute(currentRoute).showTopBar) {
                if (currentRoute?.startsWith(Routes.DepartureDetail.route) == true) {
                    DepartureDetailTopBar(navController, departureListViewModel)
                } else if (currentRoute == Routes.DepartureMap.route) {
                    DepartureMapTopBar(departureListViewModel)
                } else if (currentRoute == Routes.DeparturesList.route) {
                    DepartureListTopBar(departureListViewModel)
                } else {
                    TopBar(navController, currentRoute)
                }
            }
        },
        bottomBar = {
            if (Routes.getRoute(currentRoute).showBottomBar) {
                BottomBar(navController, currentRoute, items)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DeparturesList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DeparturesList.route) {
                DeparturesListScreen(navController, departureListViewModel)
            }
            composable(Routes.DepartureMap.route) {
                DeparturesMapScreen(navController, departureListViewModel)
            }
            composable(Routes.DeparturesBookmarks.route) { DeparturesBookmarksScreen(navController) }
            composable(Routes.DepartureDetail.route) { navBackStackEntry ->
                //todo offline mode
                val id = navBackStackEntry.arguments?.getString("departureId")?.toLongOrNull()
                val date = navBackStackEntry.arguments?.getString("departureDateTime")

                LaunchedEffect(Unit) {
                    if (id != null && date != null) {
                        departureListViewModel.getDeparture(id, date)
                    }
                }

                if (id != null && date != null) {
                    DepartureDetailScreen(id, date, departureListViewModel)
                }
            }
            composable(Routes.QuestionsAndAnswers.route) { QuestionsAndAnswersScreen() }
        }
    }
}
