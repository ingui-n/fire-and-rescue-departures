package com.android.fire_and_rescue_departures

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.screens.CenterAlignedTopAppBarExampleScreen
import com.android.fire_and_rescue_departures.screens.DeparturesMapScreen
import com.android.fire_and_rescue_departures.ui.theme.BaseAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(

            )
        }
        setContent {
            BaseAppTheme {
                val navController = rememberNavController()

                DeparturesMapScreen(navController)
            }
        }
    }
}

@Composable
fun Navigation(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Routes.DEPARTURES_MAP,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.DEPARTURES_MAP) { DeparturesMapScreen(navController) }
    }
}
