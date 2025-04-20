package com.android.fire_and_rescue_departures.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DeparturesListViewModel(private val departuresApi: DeparturesApi) : ViewModel() {
    private val _departuresList = MutableStateFlow<ApiResult<List<Departure>>>(ApiResult.Loading)
    val departuresList: StateFlow<ApiResult<List<Departure>>> = _departuresList.asStateFlow()

    private val _departure = MutableStateFlow<ApiResult<Departure>>(ApiResult.Loading)
    val departure: StateFlow<ApiResult<Departure>> = _departure.asStateFlow()

    private val _departureUnits = MutableStateFlow<ApiResult<List<DepartureUnit>>>(ApiResult.Loading)
    val departureUnits: StateFlow<ApiResult<List<DepartureUnit>>> = _departureUnits.asStateFlow()

    fun getDeparturesList(
        fromDateTime: String? = null,
        toDateTime: String? = null,
        status: List<Int>? = null,
    ) {
        viewModelScope.launch {
            _departuresList.value = ApiResult.Loading
            try {
                val response = departuresApi.getDepartures(
                    fromDateTime,
                    toDateTime,
                    status ?: DepartureStatus.getAllIds()
                )
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _departuresList.value = ApiResult.Success(data)
                        Log.d("DeparturesListViewModel", "getDeparturesList: ${response.body()}")
                    } else {
                        _departuresList.value = ApiResult.Error("Data is null")
                        Log.e("DeparturesListViewModel", "Data is null")
                    }
                } else {
                    _departuresList.value =
                        ApiResult.Error("Error fetching departures list: ${response.message()}")
                    Log.e(
                        "DeparturesListViewModel",
                        "Error fetching departures list: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _departuresList.value =
                    ApiResult.Error("Exception fetching departures list: ${e.message}")
                Log.e("DeparturesListViewModel", "Exception fetching departures list: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDeparture(
        id: Long,
        fromDateTime: String,
        toDateTime: String? = fromDateTime,
        yearsIteration: Long = 0,
    ) {
        viewModelScope.launch {
            _departure.value = ApiResult.Loading

            if (LocalDateTime.parse(fromDateTime).year < 2007) {
                _departure.value = ApiResult.Error("Departure not found")
                Log.e("DeparturesListViewModel", "Departure not found")
                return@launch
            }

            try {
                val response = departuresApi.getDepartures(
                    fromDateTime,
                    toDateTime,
                    DepartureStatus.getAllIds()
                )
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val departure = data.find { it.id == id }

                        if (departure != null) {
                            _departure.value = ApiResult.Success(departure)
                            Log.d("DeparturesListViewModel", "getDeparture: ${response.body()}")
                        } else {
                            //todo change to get /technika and by sent date time use range
                            getDeparture(
                                id,
                                LocalDateTime.now().minusYears(yearsIteration + 1).format(DateTimeFormatter.ISO_DATE_TIME),
                                LocalDateTime.now().minusYears(yearsIteration).format(DateTimeFormatter.ISO_DATE_TIME),
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

    fun getDepartureUnits(id: Long) {
        viewModelScope.launch {
            _departureUnits.value = ApiResult.Loading
            try {
                val response = departuresApi.getDepartureUnits(id)
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
}
