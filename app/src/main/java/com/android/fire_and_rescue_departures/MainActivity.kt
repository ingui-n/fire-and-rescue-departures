package com.android.fire_and_rescue_departures

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.android.fire_and_rescue_departures.helpers.PermissionHelper
import com.android.fire_and_rescue_departures.screens.MainScreen
import com.android.fire_and_rescue_departures.ui.theme.BaseAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionHelper = PermissionHelper(this)
        permissionHelper.requestNotificationPermission()

        enableEdgeToEdge()

        setContent {
            BaseAppTheme {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BaseAppTheme {
        MainScreen(rememberNavController())
    }
}
