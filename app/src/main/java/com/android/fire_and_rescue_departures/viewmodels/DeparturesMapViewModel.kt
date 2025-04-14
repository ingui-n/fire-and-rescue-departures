package com.android.fire_and_rescue_departures.viewmodels

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import com.android.fire_and_rescue_departures.BuildConfig
import com.android.fire_and_rescue_departures.repository.DeparturesMapRepository
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.enableRotation
import ovh.plrapps.mapcompose.api.onLongPress
import ovh.plrapps.mapcompose.api.onTap
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.setScrollOffsetRatio
import ovh.plrapps.mapcompose.api.shouldLoopScale
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.layout.Forced
import ovh.plrapps.mapcompose.ui.state.MapState
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.pow

class DeparturesMapViewModel(/*private val departuresMapRepository: DeparturesMapRepository*/) : ViewModel() {
    private val tileStreamProvider = makeTileStreamProvider()

    private val maxLevel = 19
    private val minLevel = 0
    private val mapSize = mapSizeAtLevel(maxLevel, tileSize = 256)
    val state = MapState(levelCount = maxLevel + 1, mapSize, mapSize, workerCount = 16) {
        minimumScaleMode(Forced((1 / 2.0.pow(maxLevel - minLevel)).toFloat()))
        scroll(0.5412764549255371, 0.33914846181869507)  // Czechia
        scale(0.0005f)
    }.apply {
        addLayer(tileStreamProvider)
        //scale = 0f // to zoom out initially
        onTap { x, y ->
            println("on tap $x $y")
        }
        enableRotation()
        //setScrollOffsetRatio(0.5f, 0.5f)
    }
}

private fun mapSizeAtLevel(wmtsLevel: Int, tileSize: Int): Int {
    return tileSize * 2.0.pow(wmtsLevel).toInt()
}

/*class DeparturesMapViewModel : ViewModel() {
    private val tileStreamProvider = makeTileStreamProvider()

    val state = MapState(
        levelCount = 20,
        fullWidth = 4096,
        fullHeight = 4096,
        workerCount = 16,  // Notice how we increase the worker count when performing HTTP requests
        initialValuesBuilder = {}
    ).apply {
        addLayer(tileStreamProvider)
        scale = 0f
        shouldLoopScale = true
    }
}*/

/**
 * A [TileStreamProvider] which performs HTTP requests.
 */
private fun makeTileStreamProvider() =
    TileStreamProvider { row, col, zoomLvl ->
        try {
            val url = URL("https://api.mapy.cz/v1/maptiles/outdoor/256/$zoomLvl/$col/$row?apikey=${BuildConfig.MAPS_COM_API}")
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            BufferedInputStream(connection.inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
