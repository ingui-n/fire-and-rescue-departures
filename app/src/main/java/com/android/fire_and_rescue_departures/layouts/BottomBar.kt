package com.android.fire_and_rescue_departures.layouts

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.consts.BottomNavItem

@Composable
fun BottomBar(navController: NavHostController, currentRoute: String?, items: List<BottomNavItem>) {
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) item.activeIcon else item.defaultIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {  //odebrání ostatních obrazovek z back stacku
                                saveState =
                                    true //uložení stavu obrazovek odstraněných z back stacku
                            }
                        }
                        launchSingleTop =
                            true //zabraňuje vytváření nových instancí stejné obrazovky
                        restoreState =
                            true //obnovení stavu, pokud byl uložen pomocí saveState
                    }
                }
            )
        }
    }
}
