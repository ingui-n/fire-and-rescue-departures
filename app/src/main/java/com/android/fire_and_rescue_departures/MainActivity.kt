package com.android.fire_and_rescue_departures

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.screens.CenterAlignedTopAppBarExampleScreen
import com.android.fire_and_rescue_departures.screens.DepartureDetailScreen
import com.android.fire_and_rescue_departures.screens.DeparturesBookmarksScreen
import com.android.fire_and_rescue_departures.screens.DeparturesListScreen
import com.android.fire_and_rescue_departures.screens.DeparturesMapScreen
import com.android.fire_and_rescue_departures.screens.MainScreen
import com.android.fire_and_rescue_departures.ui.theme.BaseAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
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
        startDestination = Routes.DEPARTURES_LIST,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.DEPARTURES_LIST) { DeparturesListScreen(navController) }
        composable(Routes.DEPARTURES_MAP) { DeparturesMapScreen(navController) }
        composable(Routes.DEPARTURES_BOOKMARKS) { DeparturesBookmarksScreen(navController) }
        composable(Routes.DEPARTURE_DETAIL) { navBackStackEntry -> //todo offline mode
            val departureId = navBackStackEntry.arguments?.getString("departureId")
            val departureDateTime = navBackStackEntry.arguments?.getString("departureDateTime")

            if (departureId != null && departureDateTime != null) {
                DepartureDetailScreen(navController, departureId.toLong(), departureDateTime)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BaseAppTheme {
        MainScreen(rememberNavController())
    }
}
