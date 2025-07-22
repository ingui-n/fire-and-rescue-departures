package com.android.fire_and_rescue_departures.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.Departure
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

    var departures: List<Departure>? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.loadDepartureBookmarks()
    }

    when (departureBookmarks) {
        is ApiResult.Loading -> {
        }

        is ApiResult.Success -> {
            departures = (departureBookmarks as ApiResult.Success).data
        }

        is ApiResult.Error -> {
            departures = null

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = UIText.DEPARTURES_LIST_EMPTY_LIST.value,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (departures != null) {
            LazyColumn(
                state = listState,
            ) {
                departures?.let {
                    items(it.size) { index ->
                        DepartureCardItem(
                            departure = it[index],
                            navController = navController,
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
