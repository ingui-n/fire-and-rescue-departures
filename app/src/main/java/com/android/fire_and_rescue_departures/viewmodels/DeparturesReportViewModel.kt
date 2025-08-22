package com.android.fire_and_rescue_departures.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fire_and_rescue_departures.consts.getRegionByName
import com.android.fire_and_rescue_departures.data.OSMAddress
import com.android.fire_and_rescue_departures.data.ReportEntity
import com.android.fire_and_rescue_departures.helpers.ToastHelper
import com.android.fire_and_rescue_departures.helpers.trimRegionFromString
import com.android.fire_and_rescue_departures.repository.AlarmReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeparturesReportViewModel(
    private val alarmReportRepository: AlarmReportRepository,
    private val toastHelper: ToastHelper
) : ViewModel() {
    private val _reports = MutableStateFlow<List<ReportEntity>>(emptyList())
    val reports: StateFlow<List<ReportEntity>> = _reports.asStateFlow()

    private val _searchedAddresses = MutableStateFlow<List<OSMAddress>>(emptyList())
    val searchedAddresses: StateFlow<List<OSMAddress>> = _searchedAddresses.asStateFlow()

    init {
        setReports()
    }

    fun clearSearchedAddresses() {
        _searchedAddresses.value = emptyList()
    }

    fun setReports() {
        _reports.value = alarmReportRepository.getReports()
    }

    fun toggleEnableReport(reportId: Long) {
        alarmReportRepository.toggleEnableReport(reportId)

        _reports.value = _reports.value.map { report ->
            if (report.id == reportId) {
                report.copy(isEnabled = !report.isEnabled)
            } else report
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun addReport(osmAddress: OSMAddress, typeId: Int, enabled: Boolean): Boolean {
        if (osmAddress.details.county == null) {
            toastHelper.showLongToast("Address region out of reach. Select only addresses from regions in filters.")
            return false
        }

        val regionId = getRegionByName(trimRegionFromString(osmAddress.details.county))

        if (regionId == null) {
            toastHelper.showLongToast("Address region out of reach. Select only addresses from regions in filters.")
            return false
        }

        val (isNew, reportEntity) = alarmReportRepository.addReport(
            osmAddress,
            regionId,
            typeId,
            enabled
        )

        if (isNew) {
            _reports.value = _reports.value.plus(reportEntity)
        } else {
            _reports.value = _reports.value.map { report ->
                if (report.id == reportEntity.id) {
                    reportEntity
                } else report
            }
        }

        return true
    }

    fun removeReport(reportId: Long) {
        alarmReportRepository.removeReport(reportId)

        _reports.value = _reports.value.filter { report ->
            report.id != reportId
        }
    }

    fun getAddressesByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val addresses = alarmReportRepository.getAddressByName(name)

            _searchedAddresses.value = addresses.filter { address ->
                address.details.county != null && getRegionByName(trimRegionFromString(address.details.county)) != null
            }
        }
    }
}
