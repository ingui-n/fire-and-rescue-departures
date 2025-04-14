package com.android.fire_and_rescue_departures.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.data.Departure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeparturesListViewModel(private val departuresApi: DeparturesApi) : ViewModel() {
    private val _departuresList = MutableStateFlow<ApiResult<List<Departure>>>(ApiResult.Loading)
    val departuresList: StateFlow<ApiResult<List<Departure>>> = _departuresList.asStateFlow()

    fun getDeparturesList() {
        viewModelScope.launch {
            _departuresList.value = ApiResult.Loading
            try {
                val response = departuresApi.getDepartures()//todo pass filters
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _departuresList.value = ApiResult.Success(data)
                        Log.d("DeparturesListViewModel", "getCryptoList: ${response.body()}")
                    } else {
                        _departuresList.value = ApiResult.Error("Data is null")
                        Log.e("DeparturesListViewModel", "Data is null")
                    }
                } else {
                    _departuresList.value =
                        ApiResult.Error("Error fetching crypto list: ${response.message()}")
                    Log.e(
                        "DeparturesListViewModel",
                        "Error fetching crypto list: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _departuresList.value =
                    ApiResult.Error("Exception fetching crypto list: ${e.message}")
                Log.e("DeparturesListViewModel", "Exception fetching crypto list: ${e.message}")
            }
        }
    }

}
