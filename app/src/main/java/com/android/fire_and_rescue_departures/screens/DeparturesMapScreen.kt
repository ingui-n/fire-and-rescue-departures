package com.android.fire_and_rescue_departures.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.helpers.convertSjtskToWgs
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker
import com.android.fire_and_rescue_departures.R
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.NavController
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureSubtypes
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.capitalizeFirstLetter
import com.android.fire_and_rescue_departures.helpers.getFormattedDepartureStartDateTime
import com.android.fire_and_rescue_departures.viewmodels.DeparturesMapViewModel
import com.android.fire_and_rescue_departures.viewmodels.MarkerData
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import androidx.core.graphics.createBitmap
import com.android.fire_and_rescue_departures.helpers.formatDescription
import androidx.compose.ui.graphics.Color as ComposeColor

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesMapScreen(
    navController: NavHostController,
    departuresViewModel: DeparturesListViewModel,
    departuresMapViewModel: DeparturesMapViewModel = koinViewModel()
) {
    Configuration.getInstance().userAgentValue = "Chrome/120.0.0.0 Safari/537.36"
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val departuresList by departuresViewModel.departuresList.collectAsState()
    val zoomLevel by departuresMapViewModel.zoomLevel.collectAsState()
    val mapCenter by departuresMapViewModel.mapCenter.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var departureDetail: Departure? by remember { mutableStateOf(null) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(zoomLevel)
            controller.setCenter(mapCenter)
        }
    }

    DisposableEffect(navController) {
        val listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                departuresMapViewModel.setZoomLevel(mapView.zoomLevelDouble)
                departuresMapViewModel.setMapCenter(mapView.mapCenter as GeoPoint)
            }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    LaunchedEffect(configuration.orientation) {
        departuresMapViewModel.setZoomLevel(mapView.zoomLevelDouble)
        departuresMapViewModel.setMapCenter(mapView.mapCenter as GeoPoint)
    }

    suspend fun renderMarkers() {
        withContext(Dispatchers.Main) {
            mapView.overlays.clear()

            departuresMapViewModel.markers.value.forEach { marker ->
                val marker1 = Marker(mapView).apply {
                    position = marker.position
                    icon = marker.icon
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setInfoWindow(null)
                    setOnMarkerClickListener { clickedMarker, mapView ->
                        showBottomSheet = true
                        departureDetail = marker.departure
                        true
                    }
                }

                mapView.overlays.add(marker1)
            }
            mapView.invalidate()
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
    }

    LaunchedEffect(departuresList) {
        if (departuresMapViewModel.markers.value.isNotEmpty()) {
            renderMarkers()
        }

        when (departuresList) {
            is ApiResult.Loading -> {}
            is ApiResult.Success -> {
                withContext(Dispatchers.Default) {
                    val departuresList = (departuresList as ApiResult.Success).data
                    departuresMapViewModel.resetMarkers()

                    departuresList.map { departure ->
                        val coordinates =
                            convertSjtskToWgs(departure.gis1.toDouble(), departure.gis2.toDouble())

                        val icon = getTypeIcon(context, departure.type, 96)

                        departuresMapViewModel.addMarker(
                            MarkerData(
                                id = departure.id.toString(),
                                position = GeoPoint(coordinates.y, coordinates.x),
                                icon = icon,
                                departure = departure,
                            )
                        )
                    }

                    renderMarkers()
                }
            }

            is ApiResult.Error -> {}
        }
    }

    AndroidView(factory = { mapView })

    fun updateMarkersVisibility() {
        val boundingBox = mapView.boundingBox
        mapView.overlays.forEach { overlay ->
            if (overlay is Marker) {
                overlay.setVisible(boundingBox.contains(overlay.position))
            }
        }
        mapView.invalidate()
    }

    mapView.addMapListener(object : MapListener {
        override fun onZoom(event: ZoomEvent?): Boolean {
            updateMarkersVisibility()
            return true
        }

        override fun onScroll(event: ScrollEvent?): Boolean {
            updateMarkersVisibility()
            return true
        }
    })

    if (showBottomSheet && departureDetail != null) {
        val departureStatus = DepartureStatus.fromId(departureDetail!!.state)
        val departureType = DepartureTypes.fromId(departureDetail!!.type)
        val departureSubtype = DepartureSubtypes.fromId(departureDetail!!.subType)
        val departureStartDateTime = getFormattedDepartureStartDateTime(departureDetail!!)
        val isOpened = DepartureStatus.getOpened().contains(departureStatus!!.id)

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            scrimColor = ComposeColor.Transparent,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (departureType != null) {
                        Text(
                            text = departureType.name,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (departureSubtype != null) {
                        Text(
                            text = departureSubtype.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    val icon = getTypeIcon(context, departureDetail!!.type)

                    Image(
                        painter = rememberDrawablePainter(icon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(
                        text = UIText.DISPATCHED_DATE.value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = departureStartDateTime,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (isOpened) UIText.DEPARTURE_STATUS_OPENED.value else UIText.DEPARTURE_STATUS_CLOSED.value,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            ) {
                Text(
                    text = UIText.DEPARTURE_ADDRESS_LABEL.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (departureDetail!!.region.name != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            UIText.ADDRESS_REGION.value,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = departureDetail!!.region.name!!,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (departureDetail!!.district.name != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            UIText.ADDRESS_DISTRICT.value,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = departureDetail!!.district.name!!,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (departureDetail!!.municipality != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            UIText.ADDRESS_MUNICIPALITY.value,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = departureDetail!!.municipality!!,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (departureDetail!!.municipalityPart != null && departureDetail!!.municipality != departureDetail!!.municipalityPart) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            UIText.ADDRESS_MUNICIPALITY_PART.value,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = departureDetail!!.municipalityPart!!,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (departureDetail!!.street != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            UIText.ADDRESS_STREET.value,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = departureDetail!!.street!!,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (departureDetail!!.road != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            UIText.ADDRESS_ROAD.value,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = departureDetail!!.road!!,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (departureDetail?.description != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
                ) {
                    Text(
                        text = formatDescription(departureDetail!!.description!!),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            navController.navigate(
                                Routes.departureDetail(
                                    departureDetail!!.regionId!!,
                                    departureDetail!!.id,
                                    (departureDetail!!.reportedDateTime
                                        ?: departureDetail!!.startDateTime).toString()
                                )
                            )
                        }
                    }
                }) {
                Text(
                    UIText.DEPARTURES_MAP_BOTTOM_SHEET_BUTTON.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

fun getTypeIcon(context: Context, type: Int, size: Int = 48): Drawable {
    data class DrawableIcon(val drawable: Int, val tint: Int)

    val icon = when (type) {
        3100 -> DrawableIcon(R.drawable.fire, Color.RED)
        3200 -> DrawableIcon(R.drawable.car, Color.BLUE)
        3400 -> DrawableIcon(R.drawable.water, Color.GREEN)
        3500 -> DrawableIcon(R.drawable.axe, Color.rgb(139, 69, 19))
        3550 -> DrawableIcon(R.drawable.person_standing, Color.rgb(191, 143, 17))
        3700 -> DrawableIcon(R.drawable.circle_alert, Color.MAGENTA)
        3600 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        3900 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        3800 -> DrawableIcon(R.drawable.bell_off, Color.rgb(102, 102, 0))
        5000 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        else -> DrawableIcon(R.drawable.siren, Color.RED)
    }

    val drawable = ContextCompat.getDrawable(context, icon.drawable)?.mutate()
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    drawable?.setTint(icon.tint)
    drawable?.setBounds(0, 0, size, size)
    drawable?.draw(canvas)

    return bitmap.toDrawable(context.resources)
}

