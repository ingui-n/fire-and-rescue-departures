package com.android.fire_and_rescue_departures.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.fire_and_rescue_departures.viewmodels.DeparturesMapViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.items.DepartureCardItem
import com.android.fire_and_rescue_departures.items.DeparturesSearchItem
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import androidx.compose.foundation.lazy.items
import com.android.fire_and_rescue_departures.consts.UIText
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DeparturesListViewModel = koinViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route //todo

    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val departuresList by viewModel.departuresList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDeparturesList(
            LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ISO_DATE_TIME),
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DeparturesSearchItem(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                scope = scope,
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Spacer(modifier = Modifier.height(16.dp))
            when (departuresList) {
                is ApiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is ApiResult.Success -> {
                    val departuresList = (departuresList as ApiResult.Success).data
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
                    //val errorMessage = (departuresList as ApiResult.Error).message
                    Text(text = UIText.DEPARTURES_LIST_EMPTY_LIST.value)
                }
            }
        }
    }
}
