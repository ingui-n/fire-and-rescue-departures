package com.android.fire_and_rescue_departures.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.items.DepartureCardItem
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.DepartureEntity
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesListScreen(
    navController: NavController,
    viewModel: DeparturesListViewModel
) {
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val departuresList by viewModel.departuresList.collectAsState()

    var refreshing by remember { mutableStateOf(false) }
    var departures: List<DepartureEntity>? by remember { mutableStateOf(null) }

    fun refreshData() {
        scope.launch {
            refreshing = true
            viewModel.updateDeparturesList()
            refreshing = false
        }
    }

    when (departuresList) {
        is ApiResult.Loading -> {}

        is ApiResult.Success -> {
            departures = (departuresList as ApiResult.Success).data
            scope.launch { listState.animateScrollToItem(0) }
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

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { refreshData() },
        modifier = Modifier.fillMaxSize()
    ) {
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
}
