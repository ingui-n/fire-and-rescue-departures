package com.android.fire_and_rescue_departures.screens

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.android.fire_and_rescue_departures.items.FullScreenAsyncImage
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import androidx.core.net.toUri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.DepartureUnit
import com.android.fire_and_rescue_departures.helpers.formatDescription
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.jvziyaoyao.scale.zoomable.previewer.rememberPreviewerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DepartureDetailScreen(
    regionId: Int,
    departureId: Long,
    departureDateTime: String,
    viewModel: DeparturesListViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val departureDetailResult by viewModel.departure.collectAsState()
    val departureUnitsResult by viewModel.departureUnits.collectAsState()

    var departureUnits: List<DepartureUnit> by remember { mutableStateOf(listOf()) }

    val fullScreenImagePreviewerState = rememberPreviewerState(pageCount = { 1 })
    val fullScreenImageScope = rememberCoroutineScope()
    val fullScreenImageUrl = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getDepartureUnits(regionId, departureId)
    }

    var refreshing by remember { mutableStateOf(false) }

    fun refreshData() {
        refreshing = true
        viewModel.getDeparture(regionId, departureId, departureDateTime)
        viewModel.getDepartureUnits(regionId, departureId)
        refreshing = false
    }

    when (departureUnitsResult) {
        is ApiResult.Loading -> {}
        is ApiResult.Success -> departureUnits = (departureUnitsResult as ApiResult.Success).data
        is ApiResult.Error -> {}
    }

    FullScreenAsyncImage(
        imageUrl = fullScreenImageUrl.value,
        previewerState = fullScreenImagePreviewerState,
        scope = fullScreenImageScope
    )

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { refreshData() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                when (departureDetailResult) {
                    is ApiResult.Loading -> {}

                    is ApiResult.Success -> {
                        val departure = (departureDetailResult as ApiResult.Success).data
                        val departureStatus = DepartureStatus.fromId(departure.state)
                        val departureType = DepartureTypes.fromId(departure.type)
                        val departureSubtype = DepartureSubtypes.fromId(departure.subType)
                        val departureStartDateTime =
                            getFormattedDepartureStartDateTime(departure)

                        val coordinates = if (departure.gis1 != null && departure.gis2 != null) {
                            convertSjtskToWgs(
                                departure.gis1.toDouble(),
                                departure.gis2.toDouble()
                            )
                        } else null

                        val departureStatusState =
                            if (DepartureStatus.getOpened().contains(departureStatus!!.id)) {
                                UIText.DEPARTURE_STATUS_OPENED.value
                            } else {
                                UIText.DEPARTURE_STATUS_CLOSED.value
                            }

                        val mapUrl = if (coordinates != null) {
                            "https://api.mapy.cz/v1/static/map?lon=${coordinates.x}&lat=${coordinates.y}&zoom=14&width=800&height=600&scale=2&mapset=outdoor&markers=color:red;size:normal;${coordinates.x},${coordinates.y}&apikey=${BuildConfig.MAPS_COM_API}"
                        } else null
                        val streetUrl = if (coordinates != null) {
                            "https://api.mapy.cz/v1/static/pano?width=800&height=600&lon=${coordinates.x}&lat=${coordinates.y}&yaw=point&fov=1.5&apikey=${BuildConfig.MAPS_COM_API}"
                        } else null

                        val backgroundColor = MaterialTheme.colorScheme.background

                        val gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                backgroundColor,
                                backgroundColor,
                                Color.Transparent
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            if (mapUrl != null) {
                                AsyncImage(
                                    model = mapUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                                        .drawWithContent {
                                            drawContent()
                                            drawRect(brush = gradient, blendMode = BlendMode.DstIn)
                                        }
                                        .clickable {
                                            fullScreenImageScope.launch {
                                                fullScreenImageUrl.value = mapUrl
                                                fullScreenImagePreviewerState.open(index = 1)
                                            }
                                        },
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }

                        val departureIcon = getTypeIcon(context, departure.type)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-48).dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = rememberDrawablePainter(departureIcon),
                                contentDescription = null,
                                modifier = Modifier.size(86.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (departureType != null) {
                                Text(
                                    text = departureType.name,
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (departureSubtype != null) {
                                Text(
                                    text = departureSubtype.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            Text(
                                text = departureStartDateTime,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${departureStatus.name} (${departureStatusState})",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            if (departure.description != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = formatDescription(departure.description),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                            if (departure.preplanned) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = UIText.DEPARTURE_PREPLANNED.value,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        if (departureUnits.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                // Header Section
                                Text(
                                    text = UIText.DETAIL_DISPATCHED_UNITS.value,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Units List
                                departureUnits.forEachIndexed { index, unit ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = if (index != departureUnits.lastIndex) 12.dp else 0.dp),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.4f
                                            )
                                        ),
                                        onClick = {}
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            // Unit Information
                                            unit.unit?.let {
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                            }

                                            // Type Information
                                            unit.type?.let {
                                                Text(
                                                    text = capitalizeFirstLetter(it),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.Medium,
                                                        letterSpacing = 0.5.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.9f
                                                    ),
                                                    modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                            }

                                            // Data Rows
                                            val dataItems = listOfNotNull(
                                                unit.callDateTime?.let {
                                                    Pair(
                                                        UIText.DETAIL_DISPATCHED_UNITS_DATE.value,
                                                        it
                                                    )
                                                },
                                                unit.count?.let {
                                                    Pair(
                                                        UIText.DETAIL_DISPATCHED_UNITS_COUNT.value,
                                                        it.toString()
                                                    )
                                                },
                                                Pair(
                                                    UIText.DETAIL_DISPATCHED_UNITS_CURRENT.value,
                                                    "${unit.currentCount ?: 0}"
                                                )
                                            )

                                            dataItems.forEach { (label, value) ->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = label,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            letterSpacing = 0.5.sp
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                            alpha = 0.7f
                                                        )
                                                    )
                                                    Spacer(Modifier.weight(1f))
                                                    Text(
                                                        text = value,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = FontWeight.SemiBold,
                                                            letterSpacing = 0.5.sp
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Address
                        if (coordinates != null && streetUrl != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                // Section Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = UIText.DEPARTURE_ADDRESS_LABEL.value,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Row(
                                        modifier = Modifier.padding(end = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val googleMapsUrl =
                                            "https://www.google.com/maps/place/${
                                                decimalToDMS(
                                                    coordinates.x
                                                )
                                            }+${
                                                decimalToDMS(
                                                    coordinates.y,
                                                    true
                                                )
                                            }/@${coordinates.x},${coordinates.y}"

                                        val mapyCzUrl =
                                            "https://mapy.com/turisticka?q=${coordinates.y},${coordinates.x}"

                                        IconButton(onClick = {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, googleMapsUrl.toUri())
                                            context.startActivity(intent)
                                        }) {
                                            Image(
                                                painter = painterResource(id = R.drawable.google_maps_icon),
                                                contentDescription = UIText.DETAIL_OPEN_ON_GOOGLE.value
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        IconButton(onClick = {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, mapyCzUrl.toUri())
                                            context.startActivity(intent)
                                        }) {
                                            Image(
                                                painter = painterResource(id = R.drawable.mapy_com_icon),
                                                contentDescription = UIText.DETAIL_OPEN_ON_MAPY.value
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Address Items
                                val addressComponents = listOfNotNull(
                                    departure.region.name?.let {
                                        Pair(UIText.ADDRESS_REGION.value, it)
                                    },
                                    departure.district.name?.let {
                                        Pair(UIText.ADDRESS_DISTRICT.value, it)
                                    },
                                    departure.municipality?.let {
                                        Pair(UIText.ADDRESS_MUNICIPALITY.value, it)
                                    },
                                    departure.municipalityPart?.takeIf { it != departure.municipality }
                                        ?.let {
                                            Pair(UIText.ADDRESS_MUNICIPALITY_PART.value, it)
                                        },
                                    departure.street?.let {
                                        Pair(UIText.ADDRESS_STREET.value, it)
                                    },
                                    departure.road?.let {
                                        Pair(UIText.ADDRESS_ROAD.value, it)
                                    }
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                            alpha = 0.4f
                                        )
                                    ),
                                    onClick = {}
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        addressComponents.forEach { (label, value) ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                            alpha = 0.9f
                                                        )
                                                    ),
                                                    modifier = Modifier.widthIn(min = 140.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = value,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.SemiBold,
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            AsyncImage(
                                model = streetUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        fullScreenImageScope.launch {
                                            fullScreenImageUrl.value = streetUrl
                                            fullScreenImagePreviewerState.open(index = 1)
                                        }
                                    },
                                contentScale = ContentScale.Crop,
                            )

                            Spacer(modifier = Modifier.height(91.dp))
                        }
                    }

                    is ApiResult.Error -> {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = UIText.NETWORK_CONNECTION_ERROR.value,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
