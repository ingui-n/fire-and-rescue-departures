package com.android.fire_and_rescue_departures

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.android.fire_and_rescue_departures.screens.MainScreen
import com.android.fire_and_rescue_departures.ui.theme.BaseAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(
                objectBoxModule,
                repositoryModule,
                viewModelModule,
                networkModule,
                imageModule
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
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BaseAppTheme {
        MainScreen(rememberNavController())
    }
}
