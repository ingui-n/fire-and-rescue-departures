package com.android.fire_and_rescue_departures.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.fire_and_rescue_departures.api.ApiResult
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.consts.getRegionById
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureEntity
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.helpers.getDateTimeFromString
import com.android.fire_and_rescue_departures.repository.DepartureBookmarksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DeparturesBookmarksViewModel(
    private val departureBookmarksRepository: DepartureBookmarksRepository,
    private val departuresApi: DeparturesApi
) : ViewModel() {
    private val _departureBookmarks =
        MutableStateFlow<ApiResult<List<DepartureEntity>>>(ApiResult.Loading)
    val departureBookmarks: StateFlow<ApiResult<List<DepartureEntity>>> =
        _departureBookmarks.asStateFlow()

    init {
        loadDepartureBookmarks()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDepartureBookmark(departure: Departure) {
        viewModelScope.launch {
            departureBookmarksRepository.addDepartureBookmark(departure)
            loadDepartureBookmarks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeDepartureBookmark(departureId: Long) {
        viewModelScope.launch {
            departureBookmarksRepository.removeDepartureBookmark(departureId.toString())
            loadDepartureBookmarks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDepartureBookmarks() {
        viewModelScope.launch {
            //todo add flag is bookmarked to DepartureEntity
            /*try {
                val departureBookmarks: List<DepartureBookmarkEntity> =
                    departureBookmarksRepository.getAllDepartureBookmarks()
                if (departureBookmarks.isEmpty()) {
                    _departureBookmarks.value = ApiResult.Success(emptyList())
                } else {
                    val departures = mutableListOf<DepartureEntity>()

                    for (departureBookmark in departureBookmarks) {
                        val departure =
                            getDeparture(
                                departureBookmark.regionId.toInt(),
                                departureBookmark.departureId.toLong(),
                                departureBookmark.dateTime
                            )

                        if (departure != null) {
                            departure.regionId = departureBookmark.regionId
                            departures.add(departure)
                        }
                    }

                    departures.reverse()
                    _departureBookmarks.value = ApiResult.Success(departures)
                }
            } catch (e: Exception) {
                _departureBookmarks.value =
                    ApiResult.Error("Exception fetching departure bookmarks: ${e.message}")
            }*/
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDeparture(
        regionId: Int,
        id: Long,
        fromDateTime: String,
        toDateTime: String? = fromDateTime,
        yearsIteration: Long = 0,
    ): Departure? {
        if (getDateTimeFromString(fromDateTime).year < 2007) {
            return null
        }

        try {
            val region = getRegionById(regionId)

            if (region == null) {
                return null
            }

            val response = departuresApi.getDepartures(
                region.url + "/api",
                fromDateTime,
                toDateTime,
                DepartureStatus.getAllIds()
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    val departure = data.find { it.id == id }

                    return departure
                        ?: //todo change to get /technika and by sent date time use range
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
            }
        } catch (_: Exception) {
            return null
        }

        return null
    }
}
