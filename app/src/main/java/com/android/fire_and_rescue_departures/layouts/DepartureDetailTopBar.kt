package com.android.fire_and_rescue_departures.layouts

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.buildDepartureShareText
import com.android.fire_and_rescue_departures.viewmodels.DeparturesBookmarksViewModel
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartureDetailTopBar(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: DeparturesListViewModel,
    bookmarksViewModel: DeparturesBookmarksViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val departureDetailResult by viewModel.departure.collectAsState()
    val departureBookmarks by bookmarksViewModel.departureBookmarks.collectAsState()

    var topBarTitle by remember { mutableStateOf("") }
    var topBarShareText by remember { mutableStateOf("") }
    var isInBookmarks by remember { mutableStateOf(false) }
    var departure: Departure? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
        scrollBehavior.state.contentOffset = 0f
    }

    when (departureDetailResult) {
        is ApiResult.Loading -> {}
        is ApiResult.Success -> {
            val localDeparture = (departureDetailResult as ApiResult.Success).data
            departure = localDeparture

            val departureType = DepartureTypes.fromId(localDeparture.type)

            if (departureType != null)
                topBarTitle = departureType.name
            topBarShareText = buildDepartureShareText(localDeparture)

            isInBookmarks =
                if (departureBookmarks is ApiResult.Success) {
                    (departureBookmarks as ApiResult.Success).data.any { it.id == localDeparture.id }
                } else {
                    false
                }
        }

        is ApiResult.Error -> {}
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = scrollBehavior.state.collapsedFraction
            ),
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            /*Text(
                text = topBarTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )*/
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = UIText.BACK.value
                )
            }
        },
        actions = {
            if (topBarShareText.isNotEmpty()) {
                IconButton(onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, topBarShareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = UIText.SHARE.value
                    )
                }
            }

            if (departure != null) {
                if (isInBookmarks) {
                    IconButton(onClick = { bookmarksViewModel.removeDepartureBookmark(departure!!.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = UIText.BOOKMARK_REMOVE.value,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    IconButton(onClick = { bookmarksViewModel.addDepartureBookmark(departure!!) }) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = UIText.BOOKMARK_ADD.value,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
