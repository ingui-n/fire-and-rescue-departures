package com.android.fire_and_rescue_departures.items

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.fire_and_rescue_departures.consts.Routes
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureSubtypes
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.formatDescription
import com.android.fire_and_rescue_departures.helpers.getFormattedDepartureStartDateTime
import com.android.fire_and_rescue_departures.screens.getTypeIcon
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DepartureCardItem(
    departure: Departure,
    navController: NavController,
) {
    val context = LocalContext.current

    val subtype = DepartureSubtypes.fromId(departure.subType)
    val startDateTime = getFormattedDepartureStartDateTime(departure)
    val isOpened = DepartureStatus.getOpened().contains(departure.state)
    val type = DepartureTypes.fromId(departure.type)

    fun handleOpenDetail() {
        if (departure.regionId == null) return

        navController.navigate(
            Routes.departureDetail(
                departure.regionId!!,
                departure.id,
                (departure.reportedDateTime ?: departure.startDateTime).toString()
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp, start = 8.dp, end = 8.dp),
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.4f
                )
            ),
            onClick = { handleOpenDetail() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (type != null) {
                        Text(
                            text = type.name,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (subtype != null) {
                        Text(
                            text = subtype.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    val icon = getTypeIcon(context, departure.type)

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
                Text(
                    text = startDateTime,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = if (isOpened) UIText.DEPARTURE_STATUS_OPENED.value else UIText.DEPARTURE_STATUS_CLOSED.value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (departure.description != null) {
                Text(
                    text = formatDescription(departure.description),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (departure.region.name != null)
                    SuggestionChip(
                        label = {
                            Text(
                                text = departure.region.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        elevation = ChipElevation(2.dp, 2.dp, 2.dp, 2.dp, 2.dp, 2.dp),
                        onClick = { handleOpenDetail() },
                    )
                if (departure.municipality != null)
                    SuggestionChip(
                        label = {
                            Text(
                                text = departure.municipality,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        elevation = ChipElevation(2.dp, 2.dp, 2.dp, 2.dp, 2.dp, 2.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        onClick = { handleOpenDetail() }
                    )
                if (departure.street != null)
                    SuggestionChip(
                        label = {
                            Text(
                                text = departure.street,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        elevation = ChipElevation(2.dp, 2.dp, 2.dp, 2.dp, 2.dp, 2.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        onClick = { handleOpenDetail() }
                    )
                if (departure.road != null)
                    SuggestionChip(
                        label = {
                            Text(
                                text = departure.road,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        elevation = ChipElevation(2.dp, 2.dp, 2.dp, 2.dp, 2.dp, 2.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        onClick = { handleOpenDetail() }
                    )
            }
        }
    }
}
