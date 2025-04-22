package com.android.fire_and_rescue_departures.layouts

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.buildDepartureShareText
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartureDetailTopBar(
    navController: NavHostController,
    viewModel: DeparturesListViewModel
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val departureDetailResult by viewModel.departure.collectAsState()

    var topBarTitle by remember { mutableStateOf("") }
    var topBarShareText by remember { mutableStateOf("") }

    when (departureDetailResult) {
        is ApiResult.Loading -> {}
        is ApiResult.Success -> {
            val departure = (departureDetailResult as ApiResult.Success).data
            val departureType = DepartureTypes.fromId(departure.type)

            if (departureType !== null)
                topBarTitle = departureType.name
            topBarShareText = buildDepartureShareText(departure)

            println("departure loaded")
        }

        is ApiResult.Error -> {}
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = topBarTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            if (topBarShareText.isNotEmpty()) {
                IconButton(onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
//                                val url = buildDeparturesUrl(
//                                    BuildConfig.HZS_API_URL,
//                                    fromDateTime = departureDateTime,
//                                    toDateTime = departureDateTime,
//                                )
                        putExtra(Intent.EXTRA_TEXT, topBarShareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share"
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
