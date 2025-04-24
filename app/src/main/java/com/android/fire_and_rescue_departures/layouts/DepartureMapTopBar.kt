package com.android.fire_and_rescue_departures.layouts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartureMapTopBar(
    viewModel: DeparturesListViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = UIText.DEPARTURES_MAP_TITLE.value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
                IconButton(onClick = {
                    viewModel.getDeparturesList()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Autorenew,
                        contentDescription = UIText.RENEW.value
                    )
                }
        },
        scrollBehavior = scrollBehavior,
    )
}
