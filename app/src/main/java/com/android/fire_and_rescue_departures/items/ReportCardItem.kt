package com.android.fire_and_rescue_departures.items

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.data.ReportEntity
import com.android.fire_and_rescue_departures.screens.getTypeIcon
import com.android.fire_and_rescue_departures.viewmodels.DeparturesReportViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportCardItem(
    viewModel: DeparturesReportViewModel,
    report: ReportEntity,
) {
    val context = LocalContext.current

    val type = DepartureTypes.fromId(report.typeId)

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
            onClick = { viewModel.toggleEnableReport(report.id) }
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
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    val icon = getTypeIcon(context, report.typeId)

                    Image(
                        painter = rememberDrawablePainter(icon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (report.county != null) {
                    AddressPartChip(
                        text = report.county,
                        onClick = { viewModel.toggleEnableReport(report.id) }
                    )
                }

                if (report.municipality != null) {
                    AddressPartChip(
                        text = report.municipality,
                        onClick = { viewModel.toggleEnableReport(report.id) }
                    )
                }

                if (report.town != null) {
                    AddressPartChip(
                        text = report.town,
                        onClick = { viewModel.toggleEnableReport(report.id) }
                    )
                }

                if (report.suburb != null) {
                    AddressPartChip(
                        text = report.suburb,
                        onClick = { viewModel.toggleEnableReport(report.id) }
                    )
                }

                if (report.village != null) {
                    AddressPartChip(
                        text = report.village,
                        onClick = { viewModel.toggleEnableReport(report.id) }
                    )
                }

                if (report.road != null) {
                    AddressPartChip(
                        text = report.road,
                        onClick = { viewModel.toggleEnableReport(report.id) }
                    )
                }
            }

            //todo add toggle button
            //todo add remove button with confirmation dialog
        }
    }
}

@Composable
fun AddressPartChip(
    text: String,
    onClick: () -> Unit,
) {
    SuggestionChip(
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ChipElevation(2.dp, 2.dp, 2.dp, 2.dp, 2.dp, 2.dp),
        onClick = { onClick() },
    )
}
