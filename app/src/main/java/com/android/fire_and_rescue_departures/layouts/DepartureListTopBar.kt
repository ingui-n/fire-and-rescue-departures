package com.android.fire_and_rescue_departures.layouts

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.viewmodels.DeparturesListViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextRange
import com.android.fire_and_rescue_departures.consts.regions
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.helpers.buildDateTimeFromPickers
import com.android.fire_and_rescue_departures.helpers.buildDateTimeStringFromPickers
import com.android.fire_and_rescue_departures.helpers.getFormattedDateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun DepartureListTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: DeparturesListViewModel,
    title: String,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScrollState = rememberScrollState()

    var showFromDatePicker by remember { mutableStateOf(false) }
    var showFromTimePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }
    var showToTimePicker by remember { mutableStateOf(false) }

    val selectedRegions by viewModel.filterRegions.collectAsState()
    val selectedType: Int? by viewModel.filterType.collectAsState()
    val fromDateTime by viewModel.filterFromDateTime.collectAsState()
    val toDateTime by viewModel.filterToDateTime.collectAsState()
    val address by viewModel.filterAddress.collectAsState()
    val addressState = rememberTextFieldState(address, TextRange(0, 50))

    var statusOpened by remember { mutableStateOf(true) }
    var statusClosed by remember { mutableStateOf(true) }

    val minYear = 2005
    val minDateMillis = Instant.parse("$minYear-01-01T00:00:00Z").toEpochMilli()
    val defaultFromDateMillis =
        LocalDateTime.now().with(LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC)?.toEpochMilli()
    val defaultToDateMillis =
        LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC)
            ?.toEpochMilli()

    var fromDatePickerState by remember {
        mutableStateOf(
            DatePickerState(
                locale = Locale("cs", "CZ"),
                initialSelectedDateMillis = fromDateTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli(),
            )
        )
    }
    var fromTimePickerState by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = fromDateTime?.hour ?: 0,
                initialMinute = fromDateTime?.minute ?: 0,
                is24Hour = true
            )
        )
    }
    var toDatePickerState by remember {
        mutableStateOf(
            DatePickerState(
                locale = Locale("cs", "CZ"),
                initialSelectedDateMillis = toDateTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli(),
            )
        )
    }
    var toTimePickerState by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = toDateTime?.hour ?: 23,
                initialMinute = toDateTime?.minute ?: 59,
                is24Hour = true
            )
        )
    }

    val fromSelectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis in minDateMillis..buildDateTimeFromPickers(
                toDatePickerState,
                toTimePickerState
            ).toInstant(ZoneOffset.UTC).toEpochMilli()
        }
    }

    val toSelectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis in buildDateTimeFromPickers(
                fromDatePickerState,
                fromTimePickerState
            ).toInstant(ZoneOffset.UTC).toEpochMilli()..LocalDateTime.now().plusDays(1)
                .toInstant(ZoneOffset.UTC).toEpochMilli()
        }
    }

    fun resetFromDatePickerState(millis: Long? = defaultFromDateMillis) {
        fromDatePickerState = DatePickerState(
            locale = Locale("cs", "CZ"),
            initialSelectedDateMillis = millis,
            yearRange =
                IntRange(
                    minYear,
                    Instant.ofEpochMilli(toDatePickerState.selectedDateMillis!!)
                        .atZone(ZoneId.systemDefault()).toLocalDate().year
                ),
            selectableDates = fromSelectableDates,
        )
    }

    fun resetToDatePickerState(millis: Long? = defaultToDateMillis) {
        toDatePickerState = DatePickerState(
            locale = Locale("cs", "CZ"),
            initialSelectedDateMillis = millis,
            yearRange =
                IntRange(
                    Instant.ofEpochMilli(fromDatePickerState.selectedDateMillis!!)
                        .atZone(ZoneId.systemDefault()).toLocalDate().year,
                    LocalDateTime.now().year
                ),
            selectableDates = toSelectableDates,
        )
    }

    LaunchedEffect(fromDatePickerState.selectedDateMillis) {
        if (toDatePickerState.selectedDateMillis == null || fromDatePickerState.selectedDateMillis == null)
            return@LaunchedEffect

        resetToDatePickerState(toDatePickerState.selectedDateMillis)
        viewModel.updateFilterFromDateTime(
            buildDateTimeStringFromPickers(fromDatePickerState, fromTimePickerState)
        )
    }
    LaunchedEffect(toDatePickerState.selectedDateMillis) {
        if (toDatePickerState.selectedDateMillis == null || fromDatePickerState.selectedDateMillis == null)
            return@LaunchedEffect

        resetFromDatePickerState(fromDatePickerState.selectedDateMillis)
        viewModel.updateFilterToDateTime(
            buildDateTimeStringFromPickers(toDatePickerState, toTimePickerState)
        )
    }
    LaunchedEffect(fromTimePickerState.hour, fromTimePickerState.minute) {
        viewModel.updateFilterFromDateTime(
            buildDateTimeStringFromPickers(fromDatePickerState, fromTimePickerState)
        )
    }
    LaunchedEffect(toTimePickerState.hour, toTimePickerState.minute) {
        viewModel.updateFilterToDateTime(
            buildDateTimeStringFromPickers(toDatePickerState, toTimePickerState)
        )
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = {
                coroutineScope.launch {
                    viewModel.updateDeparturesList()
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.Autorenew,
                    contentDescription = UIText.RENEW.value
                )
            }
            IconButton(onClick = {
                showBottomSheet = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.FilterAlt,
                    contentDescription = UIText.FILTER.value
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                val statuses =
                    if (statusOpened && statusClosed) DepartureStatus.getAllIds()
                    else if (statusOpened) DepartureStatus.getOpened()
                    else if (statusClosed) DepartureStatus.getClosed()
                    else null

                coroutineScope.launch {
                    viewModel.updateFilterStatuses(statuses)
                    viewModel.updateFilterAddress(addressState.text.toString())
                    viewModel.updateDeparturesList()
                }
                showBottomSheet = false
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(bottomSheetScrollState)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        UIText.FILTERS.value,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        UIText.STATUS.value,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .toggleable(
                                value = statusOpened,
                                onValueChange = { statusOpened = it }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            UIText.STATUS_ACTIVE_CHECKBOX.value,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Checkbox(
                            checked = statusOpened,
                            onCheckedChange = { statusOpened = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .toggleable(
                                value = statusClosed,
                                onValueChange = { statusClosed = it }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            UIText.STATUS_CLOSED_CHECKBOX.value,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Checkbox(
                            checked = statusClosed,
                            onCheckedChange = { statusClosed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // date and time
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        UIText.DATE_AND_TIME.value,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // from
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = UIText.DATE_FROM.value,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        if (fromDatePickerState.selectedDateMillis != null) {
                            val date =
                                getFormattedDateTime(
                                    fromDatePickerState.selectedDateMillis!!,
                                    "d. MMMM yyyy"
                                )
                            val time =
                                String.format(
                                    "%02d:%02d",
                                    fromTimePickerState.hour,
                                    fromTimePickerState.minute
                                )

                            Text(
                                text = "$date $time",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { showFromDatePicker = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(
                                UIText.DATE_FROM_SELECT_BUTTON.value,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Button(
                            onClick = { showFromTimePicker = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(
                                UIText.TIME_FROM_SELECT_BUTTON.value,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        if (showFromDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showFromDatePicker = false },
                                confirmButton = {
                                    Button(onClick = {
                                        showFromDatePicker = false
                                    }) {
                                        Text(UIText.OK.value)
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showFromDatePicker = false }) {
                                        Text(UIText.CANCEL.value)
                                    }
                                }
                            ) {
                                DatePicker(state = fromDatePickerState)
                            }
                        }
                        if (showFromTimePicker) {
                            AlertDialog(
                                onDismissRequest = { showFromTimePicker = false },
                                confirmButton = {
                                    Button(onClick = {
                                        showFromTimePicker = false
                                    }) {
                                        Text(UIText.OK.value)
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showFromTimePicker = false }) {
                                        Text(UIText.CANCEL.value)
                                    }
                                },
                                text = {
                                    TimePicker(state = fromTimePickerState)
                                }
                            )
                        }
                    }

                    // to
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = UIText.DATE_TO.value,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        if (toDatePickerState.selectedDateMillis != null) {
                            val date =
                                getFormattedDateTime(
                                    toDatePickerState.selectedDateMillis!!,
                                    "d. MMMM yyyy"
                                )
                            val time =
                                String.format(
                                    "%02d:%02d",
                                    toTimePickerState.hour,
                                    toTimePickerState.minute
                                )

                            Text(
                                text = "$date $time",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { showToDatePicker = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(
                                UIText.DATE_FROM_SELECT_BUTTON.value,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Button(
                            onClick = { showToTimePicker = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(
                                UIText.TIME_FROM_SELECT_BUTTON.value,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        if (showToDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showToDatePicker = false },
                                confirmButton = {
                                    Button(onClick = {
                                        showToDatePicker = false
                                    }) {
                                        Text(UIText.OK.value)
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showToDatePicker = false }) {
                                        Text(UIText.CANCEL.value)
                                    }
                                }
                            ) {
                                DatePicker(state = toDatePickerState)
                            }
                        }
                        if (showToTimePicker) {
                            AlertDialog(
                                onDismissRequest = { showToTimePicker = false },
                                confirmButton = {
                                    Button(onClick = {
                                        showToTimePicker = false
                                    }) {
                                        Text(UIText.OK.value)
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showToTimePicker = false }) {
                                        Text(UIText.CANCEL.value)
                                    }
                                },
                                text = {
                                    TimePicker(state = toTimePickerState)
                                }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        UIText.REGIONS.value,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        regions.forEach { region ->
                            FilterChip(
                                modifier = Modifier.padding(end = 8.dp),
                                onClick = {
                                    viewModel.updateFilterRegions(
                                        if (selectedRegions.contains(region.id)) {
                                            selectedRegions.minus(region.id)
                                        } else {
                                            selectedRegions.plus(region.id)
                                        }
                                    )
                                },
                                label = {
                                    Text(
                                        region.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                enabled = region.available,
                                selected = selectedRegions.contains(region.id),
                                leadingIcon = if (selectedRegions.contains(region.id)) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        UIText.DEPARTURE_ADDRESS_SEARCH.value,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        state = addressState,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        label = {
                            Text(
                                UIText.DEPARTURE_ADDRESS_LABEL.value,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // type
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        UIText.DEPARTURE_TYPE.value,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        FilterChip(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { viewModel.updateFilterType(null) },
                            label = {
                                Text(
                                    UIText.ALL.value,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            selected = selectedType == null,
                            leadingIcon = if (selectedType == null) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                        DepartureTypes.all.forEach { type ->
                            FilterChip(
                                modifier = Modifier.padding(end = 8.dp),
                                onClick = { viewModel.updateFilterType(type.id) },
                                label = {
                                    Text(
                                        type.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                selected = selectedType == type.id,
                                leadingIcon = if (selectedType == type.id) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                // reset button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.resetFilters()
                                statusOpened = true
                                statusClosed = true
                                addressState.clearText()
                                resetFromDatePickerState()
                                fromTimePickerState = TimePickerState(
                                    initialHour = 0,
                                    initialMinute = 0,
                                    is24Hour = true
                                )
                                resetToDatePickerState()
                                toTimePickerState = TimePickerState(
                                    initialHour = 0,
                                    initialMinute = 0,
                                    is24Hour = true
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(UIText.RESET.value)
                    }
                }
            }
        }
    }
}
