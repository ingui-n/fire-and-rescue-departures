package com.android.fire_and_rescue_departures.items

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.capitalizeFirstLetter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartureCardItem(
    departure: Departure,
    navController: NavHostController,
) {
    val czechLocale = Locale("cs", "CZ")
    val formattedStartDateTime = DateTimeFormatter
        .ofPattern("dd. MMMM HH:mm", czechLocale)
        .format(LocalDateTime.parse((departure.reportedDateTime ?: departure.startDateTime).toString()))

    val status = DepartureStatus.fromId(departure.state)
    val type = DepartureTypes.fromId(departure.type)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(Routes.departureDetail(departure.id))
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = formattedStartDateTime)
            if (departure.description != null)
                Text(text = capitalizeFirstLetter(departure.description))
            if (type?.name != null)
                Text(text = type.name)
            if (status?.name != null)
                Text(text = status.name)
        }
    }
}
