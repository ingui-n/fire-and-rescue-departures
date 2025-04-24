package com.android.fire_and_rescue_departures.screens

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.helpers.convertSjtskToWgs
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import org.koin.androidx.compose.koinViewModel
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesMapScreen(
    navController: NavHostController,
    departuresViewModel: DeparturesListViewModel = koinViewModel()
) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route //todo

    Configuration.getInstance().userAgentValue = "Chrome/120.0.0.0 Safari/537.36"
    val context = LocalContext.current

    val departuresList by departuresViewModel.departuresList.collectAsState()

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
        departuresViewModel.getDeparturesList(
            LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ISO_DATE_TIME),
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        )
    }

    when (departuresList) {
        is ApiResult.Loading -> {}
        is ApiResult.Success -> {
            val departuresList = (departuresList as ApiResult.Success).data
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

                mapView.overlays.add(marker)
                mapView.invalidate()
            }
        }

        is ApiResult.Error -> {}
    }

    AndroidView(factory = { mapView })
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
