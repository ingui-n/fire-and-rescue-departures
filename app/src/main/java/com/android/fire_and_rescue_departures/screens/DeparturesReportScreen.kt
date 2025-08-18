package com.android.fire_and_rescue_departures.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.fire_and_rescue_departures.items.ReportCardItem
import com.android.fire_and_rescue_departures.viewmodels.DeparturesReportViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesReportScreen(
    viewModel: DeparturesReportViewModel = koinViewModel(),
) {
//    val scope = rememberCoroutineScope()
//    scope.launch { listState.animateScrollToItem(0) }

    val listState = rememberLazyListState()
    val reportsList by viewModel.reports.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (reportsList.isNotEmpty()) {
            LazyColumn(
                state = listState,
            ) {
                items(reportsList.size) { index ->
                    ReportCardItem(viewModel, reportsList[index])
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
