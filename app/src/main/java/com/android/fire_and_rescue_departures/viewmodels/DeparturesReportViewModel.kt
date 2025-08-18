package com.android.fire_and_rescue_departures.viewmodels

import androidx.lifecycle.ViewModel
import com.android.fire_and_rescue_departures.data.OSMAddress
import com.android.fire_and_rescue_departures.data.ReportEntity
import com.android.fire_and_rescue_departures.repository.AlarmReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeparturesReportViewModel(
    private val alarmReportRepository: AlarmReportRepository
): ViewModel() {
    private val _reports = MutableStateFlow<List<ReportEntity>>(emptyList())
    val reports: StateFlow<List<ReportEntity>> = _reports.asStateFlow()

    init {
        setReports()
    }

    fun setReports() {
        _reports.value = alarmReportRepository.getReports()
    }

    fun toggleEnableReport(reportId: Long) {
        alarmReportRepository.toggleEnableReport(reportId)
    }

    fun addReport(osmAddress: OSMAddress, regionId: Int, typeId: Int) {
        alarmReportRepository.addReport(osmAddress, regionId, typeId)
    }

    fun removeReport(reportId: Long) {
        alarmReportRepository.removeReport(reportId)
    }
}
