package com.android.fire_and_rescue_departures.screens

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.BuildConfig
import com.android.fire_and_rescue_departures.R
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureSubtypes
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.capitalizeFirstLetter
import com.android.fire_and_rescue_departures.helpers.getFormattedDepartureStartDateTime
import com.android.fire_and_rescue_departures.helpers.convertSjtskToWgs
import com.android.fire_and_rescue_departures.helpers.decimalToDMS
import com.android.fire_and_rescue_departures.items.FullScreenableImage
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import androidx.core.net.toUri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.android.fire_and_rescue_departures.helpers.buildDepartureShareText

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DepartureDetailScreen(
    departureId: Long,
    departureDateTime: String,
    viewModel: DeparturesListViewModel
) {
    val context = LocalContext.current
    val departureDetailResult by viewModel.departure.collectAsState()
    val departureUnitsResult by viewModel.departureUnits.collectAsState()

    var topBarTitle by remember { mutableStateOf("") }
    var topBarShareText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getDepartureUnits(departureId)
    }

    var refreshing by remember { mutableStateOf(false) }

    fun refreshData() {
        refreshing = true
        viewModel.getDeparture(departureId, departureDateTime)
        viewModel.getDepartureUnits(departureId)
        refreshing = false
    }

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { refreshData() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            //.padding(16.dp),//todo set some padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                when (departureDetailResult) {
                    is ApiResult.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    is ApiResult.Success -> {
                        val departure = (departureDetailResult as ApiResult.Success).data
                        val departureStatus = DepartureStatus.fromId(departure.state)
                        val departureType = DepartureTypes.fromId(departure.type)
                        val departureSubtype = DepartureSubtypes.fromId(departure.subType)
                        val departureStartDateTime =
                            getFormattedDepartureStartDateTime(departure)
                        val coordinates = convertSjtskToWgs(
                            departure.gis1.toDouble(),
                            departure.gis2.toDouble()
                        )

                        if (departureType !== null)
                            topBarTitle = departureType.name
                        topBarShareText = buildDepartureShareText(departure)

                        if (departureSubtype !== null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = departureSubtype.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (departureStatus !== null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = departureStatus.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Čas ohlášení:"
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = departureStartDateTime,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (departure.description !== null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = capitalizeFirstLetter(departure.description),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Obec:"
                            )
                            Text(text = departure.region.name)
                            Text(text = departure.district.name)

                            if (departure.municipality !== null)
                                Text(text = departure.municipality)
                            if (departure.municipalityPart !== null)
                                Text(text = departure.municipalityPart)
                            if (departure.municipalityWithExtendedCompetence !== null)
                                Text(text = departure.municipalityWithExtendedCompetence)
                            if (departure.street !== null)
                                Text(text = departure.street)
                            if (departure.road !== null)
                                Text(text = departure.road)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (departure.preplanned) {
                            Text(text = "Předem naplánovaná")
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        OutlinedCard {
                            val url =
                                "${BuildConfig.MAPS_COM_API_URL}/static/map?lon=${coordinates.x}&lat=${coordinates.y}&zoom=14&width=800&height=600&scale=2&mapset=outdoor&markers=color:red;size:normal;${coordinates.x},${coordinates.y}&apikey=${BuildConfig.MAPS_COM_API}"

                            FullScreenableImage(url)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val googleMapsUrl =
                                "https://www.google.com/maps/place/${decimalToDMS(coordinates.x)}+${
                                    decimalToDMS(
                                        coordinates.y,
                                        true
                                    )
                                }/@${coordinates.x},${coordinates.y}"

                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, googleMapsUrl.toUri())
                                context.startActivity(intent)
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.google_maps_icon),
                                    contentDescription = "Open on Google Maps"
                                )
                            }

                            val mapyCzUrl =
                                "https://mapy.com/turisticka?q=${coordinates.y},${coordinates.x}"

                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, mapyCzUrl.toUri())
                                context.startActivity(intent)
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.mapy_com_icon),
                                    contentDescription = "Open on Mapy.com"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedCard {
                            val url =
                                "${BuildConfig.MAPS_COM_API_URL}/static/pano?width=800&height=600&lon=${coordinates.x}&lat=${coordinates.y}&yaw=point&fov=1.5&apikey=${BuildConfig.MAPS_COM_API}"

                            FullScreenableImage(url)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    is ApiResult.Error -> {
                        val errorMessage = (departureDetailResult as ApiResult.Error).message
                        Text(
                            text = "Error: $errorMessage",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            item {
                when (departureUnitsResult) {
                    is ApiResult.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    is ApiResult.Success -> {
                        val departureUnits = (departureUnitsResult as ApiResult.Success).data

                        if (departureUnits.isNotEmpty()) {
                            Text(text = "Zasahující jednotky")
                        }

                        departureUnits.forEach { unit ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (unit.type != null)
                                    Text(text = capitalizeFirstLetter(unit.type))
                                if (unit.unit != null)
                                    Text(text = unit.unit)
                                if (unit.count != null)
                                    Text(text = "Počet: ${unit.count}")
                                Text(text = "Aktuálně zasahující: ${unit.currentCount ?: 0}")
                                if (unit.callDateTime != null)
                                    Text(text = "Čas ohlášení: ${unit.callDateTime}")

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    is ApiResult.Error -> {
                        val errorMessage = (departureUnitsResult as ApiResult.Error).message
                        Text(
                            text = "Error: $errorMessage",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
