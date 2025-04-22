package com.android.fire_and_rescue_departures

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.screens.DepartureDetailScreen
import com.android.fire_and_rescue_departures.screens.DeparturesBookmarksScreen
import com.android.fire_and_rescue_departures.screens.DeparturesListScreen
import com.android.fire_and_rescue_departures.screens.DeparturesMapScreen
import com.android.fire_and_rescue_departures.screens.MainScreen
import com.android.fire_and_rescue_departures.ui.theme.BaseAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(
                viewModelModule,
                networkModule,
                imageModule,
                objectBoxModule
            )
        }
        setContent {
            BaseAppTheme {
                val navController = rememberNavController()

                MainScreen(navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Routes.DeparturesList.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.DeparturesList.route) { DeparturesListScreen(navController) }
        composable(Routes.DepartureMap.route) { DeparturesMapScreen(navController) }
        composable(Routes.DeparturesBookmarks.route) { DeparturesBookmarksScreen(navController) }
        composable(Routes.DepartureDetail.route) { navBackStackEntry -> //todo offline mode
            val departureId = navBackStackEntry.arguments?.getString("departureId")
            val departureDateTime = navBackStackEntry.arguments?.getString("departureDateTime")

            if (departureId != null && departureDateTime != null) {
                DepartureDetailScreen(navController, departureId.toLong(), departureDateTime)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BaseAppTheme {
        MainScreen(rememberNavController())
    }
}
