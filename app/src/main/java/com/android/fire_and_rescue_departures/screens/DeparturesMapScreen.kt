package com.android.fire_and_rescue_departures.screens

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.helpers.convertSjtskToWgs
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker
import com.android.fire_and_rescue_departures.R
import androidx.core.graphics.scale
import androidx.core.graphics.drawable.toDrawable
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureSubtypes
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.getFormattedDepartureStartDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesMapScreen(
    navController: NavHostController,
    departuresViewModel: DeparturesListViewModel
) {
    Configuration.getInstance().userAgentValue = "Chrome/120.0.0.0 Safari/537.36"
    val context = LocalContext.current

    val departuresList by departuresViewModel.departuresList.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var departureDetail: Departure? by remember { mutableStateOf(null) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(8.0)
            controller.setCenter(GeoPoint(49.8135236, 15.4353594))
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            departuresViewModel.getDeparturesList(
                LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            )
            delay(60_000L)
        }
    }

    when (departuresList) {
        is ApiResult.Loading -> {}
        is ApiResult.Success -> {
            val departuresList = (departuresList as ApiResult.Success).data
            mapView.overlays.clear()

            departuresList.forEach { departure ->
                val coordinates = convertSjtskToWgs(
                    departure.gis1.toDouble(),
                    departure.gis2.toDouble()
                )

                val marker = Marker(mapView)
                marker.position = GeoPoint(coordinates.y, coordinates.x)
                marker.icon = getTypeIcon(context, departure.type)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.setInfoWindow(null)
                marker.setOnMarkerClickListener { clickedMarker, mapView ->
                    showBottomSheet = true
                    departureDetail = departure
                    true
                }

                mapView.overlays.add(marker)
                mapView.invalidate()
            }
        }

        is ApiResult.Error -> {}
    }

    AndroidView(factory = { mapView })

    if (showBottomSheet && departureDetail != null) {
        val departureStatus = DepartureStatus.fromId(departureDetail!!.state)
        val departureType = DepartureTypes.fromId(departureDetail!!.type)
        val departureSubtype = DepartureSubtypes.fromId(departureDetail!!.subType)
        val departureStartDateTime = getFormattedDepartureStartDateTime(departureDetail!!)

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (departureStatus !== null)
                    Text(text = departureStatus.name)
                Text(text = departureStartDateTime)
                if (departureType !== null)
                    Text(text = departureType.name)
                if (departureSubtype !== null)
                    Text(text = departureSubtype.name)
                if (departureDetail?.description !== null)
                    Text(text = departureDetail!!.description!!)

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                navController.navigate(
                                    Routes.departureDetail(
                                        departureDetail!!.id,
                                        (departureDetail!!.reportedDateTime
                                            ?: departureDetail!!.startDateTime).toString()
                                    )
                                )
                            }
                        }
                    }) {
                    Text(UIText.DEPARTURES_MAP_BOTTOM_SHEET_BUTTON.value)
                }
            }
        }
    }
}

fun getTypeIcon(context: Context, departureType: Int): Drawable {
    data class DrawableIcon(val drawable: Int, val tint: Int)

    val drawableIcon = when (departureType) {
        3100 -> DrawableIcon(R.drawable.fire, Color.RED)
        3200 -> DrawableIcon(R.drawable.car, Color.BLUE)
        3400 -> DrawableIcon(R.drawable.water, Color.GREEN)
        3500 -> DrawableIcon(R.drawable.axe, Color.rgb(139, 69, 19))
        3550 -> DrawableIcon(R.drawable.person_standing, Color.rgb(245, 222, 179))
        3700 -> DrawableIcon(R.drawable.circle_alert, Color.MAGENTA)
        3600 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        3900 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        3800 -> DrawableIcon(R.drawable.bell_off, Color.rgb(102, 102, 0))
        5000 -> DrawableIcon(R.drawable.circle_alert, Color.GRAY)
        else -> DrawableIcon(R.drawable.siren, Color.RED)
    }

    var icon = ContextCompat.getDrawable(context, drawableIcon.drawable)!!.mutate()

    val sizePx = (15 * context.resources.displayMetrics.density).toInt()

    val bitmap = (icon as BitmapDrawable).bitmap
    val scaledBitmap = bitmap.scale(sizePx, sizePx)

    icon = scaledBitmap.toDrawable(context.resources)
    DrawableCompat.setTint(icon, drawableIcon.tint)

    return icon
}
