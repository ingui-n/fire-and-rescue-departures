package com.android.fire_and_rescue_departures.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.consts.getRegionById
import com.android.fire_and_rescue_departures.consts.regions
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureUnit
import com.android.fire_and_rescue_departures.helpers.getDateTimeFromString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DeparturesListViewModel(private val departuresApi: DeparturesApi) : ViewModel() {
    private val _departuresList = MutableStateFlow<ApiResult<List<Departure>>>(ApiResult.Loading)
    val departuresList: StateFlow<ApiResult<List<Departure>>> = _departuresList.asStateFlow()

    private val _departure = MutableStateFlow<ApiResult<Departure>>(ApiResult.Loading)
    val departure: StateFlow<ApiResult<Departure>> = _departure.asStateFlow()

    private val _departureUnits =
        MutableStateFlow<ApiResult<List<DepartureUnit>>>(ApiResult.Loading)
    val departureUnits: StateFlow<ApiResult<List<DepartureUnit>>> = _departureUnits.asStateFlow()

    private val _filterFromDateTime = MutableStateFlow<LocalDateTime?>(
        LocalDateTime.now().with(LocalTime.MIDNIGHT)
    )
    val filterFromDateTime: StateFlow<LocalDateTime?> = _filterFromDateTime.asStateFlow()

    private val _filterToDateTime = MutableStateFlow<LocalDateTime?>(
        LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT)
    )
    val filterToDateTime: StateFlow<LocalDateTime?> = _filterToDateTime.asStateFlow()

    private val _filterAddress = MutableStateFlow<String>("")
    val filterAddress: StateFlow<String> = _filterAddress.asStateFlow()

    private val _filterRegions = MutableStateFlow<List<Int>>(listOf<Int>(1))//todo add to settings
    val filterRegions: StateFlow<List<Int>> = _filterRegions.asStateFlow()

    private val _filterType = MutableStateFlow<Int?>(null)
    val filterType: StateFlow<Int?> = _filterType.asStateFlow()

    private val _filterStatuses = MutableStateFlow<List<Int>?>(DepartureStatus.getAllIds())
    val filterStatuses: StateFlow<List<Int>?> = _filterStatuses.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFilterFromDateTime(dateTime: String) {
        _filterFromDateTime.value = getDateTimeFromString(dateTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFilterToDateTime(dateTime: String) {
        _filterToDateTime.value = getDateTimeFromString(dateTime)
    }

    fun updateFilterAddress(address: String) {
        _filterAddress.value = address
    }

    fun updateFilterRegions(regions: List<Int>) {
        _filterRegions.value = regions
    }

    fun updateFilterType(type: Int?) {
        _filterType.value = type
    }

    fun updateFilterStatuses(statuses: List<Int>?) {
        _filterStatuses.value = statuses
    }

    fun resetFilters() {
        _filterFromDateTime.value = LocalDateTime.now().with(LocalTime.MIDNIGHT)
        _filterToDateTime.value = LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT)
        _filterAddress.value = ""
        _filterRegions.value = listOf<Int>()
        _filterType.value = null
        _filterStatuses.value = DepartureStatus.getAllIds()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDeparturesList() {
        viewModelScope.launch(Dispatchers.IO) {
            _departuresList.value = ApiResult.Loading

            val mergedResults = mutableListOf<Departure>()
            var hadError = false
            var errorMessage: String? = null

            filterRegions.value.forEach { regionId ->
                val region = getRegionById(regionId)
                if (region == null) return@forEach

                try {
                    val response = departuresApi.getDepartures(
                        region.url + "/api/",
                        filterFromDateTime.value?.format(DateTimeFormatter.ISO_DATE_TIME),
                        filterToDateTime.value?.format(DateTimeFormatter.ISO_DATE_TIME),
                        filterStatuses.value,
                        filterType.value,
                        filterAddress.value,
                    )

                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            data.forEach { departure -> departure.regionId = regionId }
                            mergedResults.addAll(data)
                            Log.d(
                                "DeparturesListViewModel",
                                "Fetched departures for region $regionId: $data"
                            )
                        } else {
                            hadError = true
                            errorMessage = "Data is null for region $regionId"
                            Log.e("DeparturesListViewModel", errorMessage)
                        }
                    } else {
                        hadError = true
                        errorMessage =
                            "Error fetching departures for region $regionId: ${response.message()}"
                        Log.e("DeparturesListViewModel", errorMessage)
                    }
                } catch (e: Exception) {
                    hadError = true
                    errorMessage =
                        "Exception fetching departures for region $regionId: ${e.message}"
                    Log.e("DeparturesListViewModel", errorMessage)
                }
            }

            withContext(Dispatchers.Main) {
                if (mergedResults.isNotEmpty()) {
                    mergedResults.sortWith(
                        compareByDescending<Departure> { departure ->
                            getDateTimeFromString(
                                departure.reportedDateTime ?: departure.startDateTime ?: ""
                            )
                        }
                    )
                    _departuresList.value = ApiResult.Success(mergedResults)
                } else if (hadError) {
                    _departuresList.value = ApiResult.Error(errorMessage ?: "Unknown error")
                } else {
                    _departuresList.value = ApiResult.Error("No data found")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDeparture(
        regionId: Int,
        id: Long,
        fromDateTime: String,
        toDateTime: String? = fromDateTime,
        yearsIteration: Long = 0,
    ) {
        if (departure.value is ApiResult.Success &&
            (departure.value as ApiResult.Success).data.id == id
        ) {
            return
        }

        viewModelScope.launch {
            _departure.value = ApiResult.Loading

            if (getDateTimeFromString(fromDateTime).year < 2007) {
                _departure.value = ApiResult.Error("Departure not found")
                Log.e("DeparturesListViewModel", "Departure not found")
                return@launch
            }

            try {
                val region = getRegionById(regionId)

                if (region == null) {
                    _departure.value = ApiResult.Error("Region not found")
                    Log.e("DeparturesListViewModel", "Region not found")
                    return@launch
                }

                val response = departuresApi.getDepartures(
                    region.url + "/api/",
                    fromDateTime,
                    toDateTime,
                    DepartureStatus.getAllIds()
                )
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val departure = data.find { it.id == id }

                        if (departure != null) {
                            departure.regionId = regionId
                            _departure.value = ApiResult.Success(departure)
                            Log.d("DeparturesListViewModel", "getDeparture: ${response.body()}")
                        } else {
                            //todo change to get /technika and by sent date time use range
                            getDeparture(
                                regionId,
                                id,
                                LocalDateTime.now().minusYears(yearsIteration + 1)
                                    .format(DateTimeFormatter.ISO_DATE_TIME),
                                LocalDateTime.now().minusYears(yearsIteration)
                                    .format(DateTimeFormatter.ISO_DATE_TIME),
                                yearsIteration + 1
                            )
                        }
                    } else {
                        _departure.value = ApiResult.Error("Data is null")
                        Log.e("DeparturesListViewModel", "Data is null")
                    }
                } else {
                    _departure.value =
                        ApiResult.Error("Error fetching departure: ${response.message()}")
                    Log.e(
                        "DeparturesListViewModel",
                        "Error fetching departure: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _departure.value =
                    ApiResult.Error("Exception fetching departure: ${e.message}")
                Log.e("DeparturesListViewModel", "Exception fetching departure: ${e.message}")
            }
        }
    }

    fun getDepartureUnits(regionId: Int, id: Long) {
        viewModelScope.launch {
            _departureUnits.value = ApiResult.Loading
            try {
                val region = getRegionById(regionId)

                if (region == null) {
                    _departureUnits.value = ApiResult.Error("Region not found")
                    Log.e("DeparturesListViewModel", "Region not found")
                    return@launch
                }

                val response =
                    departuresApi.getDepartureUnits("${region.url}/api/udalosti/$id/technika")

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _departureUnits.value = ApiResult.Success(data)
                        Log.d("DeparturesListViewModel", "getDepartureUnits: ${response.body()}")
                    } else {
                        _departureUnits.value = ApiResult.Error("Data is null")
                        Log.e("DeparturesListViewModel", "Data is null")
                    }
                } else {
                    _departureUnits.value =
                        ApiResult.Error("Error fetching departure units: ${response.message()}")
                    Log.e(
                        "DeparturesListViewModel",
                        "Error fetching departure units: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _departureUnits.value =
                    ApiResult.Error("Exception fetching departure units: ${e.message}")
                Log.e("DeparturesListViewModel", "Exception fetching departure units: ${e.message}")
            }
        }
    }

    fun testApi() {
        regions.forEach { region ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeout(5_000L) {
                        departuresApi.test("${region.url}/api")
                    }
                } catch (_: Exception) {
                    region.available = false
                    _filterRegions.value = _filterRegions.value.filter { it != region.id }
                }
            }
        }
    }
}
