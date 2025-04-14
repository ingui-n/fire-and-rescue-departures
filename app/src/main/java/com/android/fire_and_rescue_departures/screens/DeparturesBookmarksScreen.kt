package com.android.fire_and_rescue_departures.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovh.plrapps.mapcompose.ui.MapUI
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.viewmodels.DeparturesMapViewModel
import org.koin.androidx.compose.koinViewModel
import ovh.plrapps.mapcompose.api.centroidX

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesBookmarksScreen(//todo bookmarks
    navController: NavController,
    modifier: Modifier = Modifier,
    //viewModel: OsmVM = koinViewModel()
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(UIText.DEPARTURES_BOOKMARKS_TITLE.value) },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
