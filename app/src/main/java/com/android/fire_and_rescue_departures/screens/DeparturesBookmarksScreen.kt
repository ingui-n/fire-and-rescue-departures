package com.android.fire_and_rescue_departures.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.items.DepartureCardItem
import com.android.fire_and_rescue_departures.viewmodels.DeparturesBookmarksViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesBookmarksScreen(
    navController: NavController,
    viewModel: DeparturesBookmarksViewModel = koinViewModel()
) {
    val departureBookmarks by viewModel.departureBookmarks.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadDepartureBookmarks()
    }

    when (departureBookmarks) {
        is ApiResult.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is ApiResult.Success -> {
            val departuresList = (departureBookmarks as ApiResult.Success).data
            LazyColumn(state = listState) {
                items(departuresList) { departure ->
                    DepartureCardItem(
                        departure = departure,
                        navController = navController,
                    )
                }
            }
        }

        is ApiResult.Error -> {
            Text(text = UIText.DEPARTURES_LIST_EMPTY_LIST.value)
        }
    }
}
