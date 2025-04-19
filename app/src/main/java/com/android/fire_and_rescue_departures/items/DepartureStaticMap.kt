package com.android.fire_and_rescue_departures.items

import android.content.res.Resources
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.android.fire_and_rescue_departures.BuildConfig
import com.android.fire_and_rescue_departures.helpers.convertSjtskToWgs

@Composable
fun DepartureStaticMap(gis1: Double, gis2: Double) {
    val coordinates = convertSjtskToWgs(gis1, gis2)

    //val width = Resources.getSystem().displayMetrics.widthPixels //todo view size based on device width

    val url =
        "${BuildConfig.MAPS_COM_API_URL}/static/map?lon=${coordinates.x}&lat=${coordinates.y}&zoom=14&width=800&height=600&scale=2&mapset=outdoor&markers=color:red;size:normal;${coordinates.x},${coordinates.y}&apikey=${BuildConfig.MAPS_COM_API}"

    AsyncImage(
        model = url,
        contentDescription = "Map of Departure",
        modifier = Modifier
            .fillMaxWidth(),
            //.height(500.dp),
//        contentScale = ContentScale.FillBounds
                contentScale = ContentScale.Fit
    )
}
