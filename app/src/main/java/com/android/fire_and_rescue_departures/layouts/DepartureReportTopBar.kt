package com.android.fire_and_rescue_departures.layouts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.InputMode.Companion.Keyboard
import androidx.compose.ui.input.InputMode.Companion.Touch
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.fire_and_rescue_departures.consts.UIText
import com.android.fire_and_rescue_departures.data.DepartureTypes
import com.android.fire_and_rescue_departures.data.OSMAddress
import com.android.fire_and_rescue_departures.viewmodels.DeparturesReportViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartureReportTopBar(
    departuresReportViewModel: DeparturesReportViewModel,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val inputModeManager = LocalInputModeManager.current

    val searchedAddresses by departuresReportViewModel.searchedAddresses.collectAsState()
    var isDialogOpened by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val addressState = rememberTextFieldState("", TextRange(0, 50))
    var enabled by remember { mutableStateOf(true) }
    var type by remember { mutableStateOf<DepartureTypes>(DepartureTypes.All) }

    var typeSelectExpanded by remember { mutableStateOf(false) }
    val (selectedAddress, onOptionSelected) = remember { mutableStateOf<OSMAddress?>(null) }

    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
        scrollBehavior.state.contentOffset = 0f
    }

    LaunchedEffect(addressState.text) {
        isLoading = true
        delay(2_000L)
        departuresReportViewModel.getAddressesByName(addressState.text.toString())
        delay(1_000L)
        isLoading = false
    }

    LaunchedEffect(searchedAddresses) {
        isLoading = false
    }

    fun onClose() {
        isDialogOpened = false
        addressState.clearText()
        enabled = true
        type = DepartureTypes.All
        typeSelectExpanded = false
        departuresReportViewModel.clearSearchedAddresses()
    }

    fun onAdd() {
        val res = departuresReportViewModel.addReport(selectedAddress!!, type.id, enabled)

        if (res)
            onClose()
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
                text = UIText.DEPARTURES_REPORT_TITLE.value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = {
                isDialogOpened = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = UIText.ADD.value
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )

    if (!isDialogOpened)
        return

    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = UIText.BACK.value
                        )
                    }
                    Text(
                        "New report",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    TextButton(
                        onClick = { onAdd() },
                        modifier = Modifier,
                        enabled = selectedAddress != null
                    ) {
                        Text("Save")
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { typeSelectExpanded = !typeSelectExpanded }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Departure type",
                            modifier = Modifier.padding(start = 2.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            )
                        Box {
                            AssistChip(
                                label = { Text(type.name) },
                                onClick = { typeSelectExpanded = !typeSelectExpanded },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Select type"
                                    )
                                },
                            )

                            DropdownMenu(
                                expanded = typeSelectExpanded,
                                onDismissRequest = { typeSelectExpanded = false }
                            ) {
                                DepartureTypes.all.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.name) },
                                        onClick = {
                                            type = option
                                            typeSelectExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(horizontal = 10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { enabled = !enabled }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = "Enabled",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Switch(
                            checked = enabled,
                            onCheckedChange = {
                                enabled = it
                            }
                        )
                    }
                }

                HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(horizontal = 10.dp))

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
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .focusProperties {
                            canFocus =
                                inputModeManager.inputMode == Keyboard || inputModeManager.inputMode == Touch
                        }
                        .focusTarget()
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    }
                } else {
                    Column(Modifier.selectableGroup()) {
                        searchedAddresses.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (text == selectedAddress),
                                        onClick = { onOptionSelected(text) },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (text == selectedAddress),
                                    onClick = null // null recommended for accessibility with screen readers
                                )
                                Text(
                                    text = text.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
