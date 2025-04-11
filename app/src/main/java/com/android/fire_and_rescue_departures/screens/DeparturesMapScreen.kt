package com.android.fire_and_rescue_departures.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ovh.plrapps.mapcompose.ui.MapUI
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.viewmodels.DeparturesMapViewModel
import com.android.fire_and_rescue_departures.viewmodels.OsmVM
import ovh.plrapps.mapcompose.api.centroidX

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesMapScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: OsmVM = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    print(viewModel.state.centroidX)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(UIText.MAP_TITLE.value) },
            )
        }
    ) { padding ->
        MapUI(
            modifier.padding(padding),
            state = viewModel.state
        )
    }
}
