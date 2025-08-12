package com.android.fire_and_rescue_departures.viewmodels

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.android.fire_and_rescue_departures.data.DepartureEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

data class MarkerData(val id: String, val position: GeoPoint, val icon: Drawable, val departure: DepartureEntity)

class DeparturesMapViewModel: ViewModel() {
    private val _markers = MutableStateFlow<List<MarkerData>>(listOf())
    val markers: StateFlow<List<MarkerData>> = _markers.asStateFlow()

    private val _zoomLevel = MutableStateFlow<Double>(8.0)
    val zoomLevel: StateFlow<Double> = _zoomLevel.asStateFlow()

    private val _mapCenter = MutableStateFlow<GeoPoint>(GeoPoint(49.8135236, 15.4353594)) // center to Czechia
    val mapCenter: StateFlow<GeoPoint> = _mapCenter.asStateFlow()

    fun addMarker(marker: MarkerData) {
        _markers.value = markers.value.plus(marker)
    }

    fun resetMarkers() {
        _markers.value = listOf()
    }

    fun setZoomLevel(zoomLevel: Double) {
        _zoomLevel.value = zoomLevel
    }

    fun setMapCenter(mapCenter: GeoPoint) {
        _mapCenter.value = mapCenter
    }
}
