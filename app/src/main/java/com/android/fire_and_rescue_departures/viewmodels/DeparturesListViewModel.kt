package com.android.fire_and_rescue_departures.viewmodels

import android.content.Context
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
import kotlinx.coroutines.withTimeout
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.core.content.edit
import com.android.fire_and_rescue_departures.data.DepartureEntity
import com.android.fire_and_rescue_departures.data.DepartureEntity_
import com.android.fire_and_rescue_departures.helpers.convertSjtskToWgs
import com.android.fire_and_rescue_departures.helpers.getDateTimeLongFromString
import com.android.fire_and_rescue_departures.helpers.getDepartureStartDateTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.objectbox.Box
import io.objectbox.kotlin.greaterOrEqual
import io.objectbox.kotlin.lessOrEqual
import io.objectbox.query.QueryBuilder
import kotlin.run

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
class DeparturesListViewModel(
    private val departuresApi: DeparturesApi,
    private val departuresBox: Box<DepartureEntity>,
    context: Context
) : ViewModel() {
    val isLoading = MutableStateFlow(false)

    private val filters = context.getSharedPreferences("filters", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _isFilterChanged = MutableStateFlow(false)
    val isFilterChanged: StateFlow<Boolean> = _isFilterChanged.asStateFlow()

    private val _statusOpened = MutableStateFlow(
        filters.getBoolean("statusOpened", true)
    )

    val statusOpened: StateFlow<Boolean> = _statusOpened.asStateFlow()

    private val _statusClosed = MutableStateFlow(
        filters.getBoolean("statusClosed", true)
    )

    val statusClosed: StateFlow<Boolean> = _statusClosed.asStateFlow()

    private val _filterFromDateTime = MutableStateFlow<LocalDateTime>(
        filters.getString("filterFromDateTime", null).let {
            if (it != null) {
                LocalDateTime.parse(it)
            } else LocalDateTime.now().with(LocalTime.MIDNIGHT)
        }
    )
    val filterFromDateTime: StateFlow<LocalDateTime> = _filterFromDateTime.asStateFlow()

    private val _filterToDateTime = MutableStateFlow<LocalDateTime>(
        filters.getString("filterToDateTime", null).let {
            if (it != null) {
                LocalDateTime.parse(it)
            } else LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT)
        }
    )
    val filterToDateTime: StateFlow<LocalDateTime> = _filterToDateTime.asStateFlow()

    private val _filterAddress = MutableStateFlow(
        filters.getString("filterAddress", "") as String
    )
    val filterAddress: StateFlow<String> = _filterAddress.asStateFlow()

    private val _filterRegions = MutableStateFlow<List<Int>>(
        gson.fromJson(
            filters.getString("filterRegions", null),
            object : TypeToken<List<Int>>() {}.type
        ) ?: listOf()
    )
    val filterRegions: StateFlow<List<Int>> = _filterRegions.asStateFlow()

    private val _filterType = MutableStateFlow(
        filters.getInt("filterType", 0).let { if (it == 0) null else it }
    )
    val filterType: StateFlow<Int?> = _filterType.asStateFlow()

    private val _filterStatuses = MutableStateFlow<List<Int>?>(null)
    val filterStatuses: StateFlow<List<Int>?> = _filterStatuses.asStateFlow()

    private val _departuresList = MutableStateFlow<ApiResult<List<DepartureEntity>>>(
        ApiResult.Success(selectDeparturesWithFilters())
    )
    val departuresList: StateFlow<ApiResult<List<DepartureEntity>>> = _departuresList.asStateFlow()

    private val _departure = MutableStateFlow<ApiResult<Departure>>(ApiResult.Loading)
    val departure: StateFlow<ApiResult<Departure>> = _departure.asStateFlow()

    private val _departureUnits =
        MutableStateFlow<ApiResult<List<DepartureUnit>>>(ApiResult.Loading)
    val departureUnits: StateFlow<ApiResult<List<DepartureUnit>>> = _departureUnits.asStateFlow()


    init {
        val filterStatusesJson = filters.getString("filterStatuses", null)
        val filterStatusesList: List<Int>? = try {
            filterStatusesJson?.let { gson.fromJson(it, object : TypeToken<List<Int>>() {}.type) }
        } catch (_: Exception) {
            null
        }

        _filterStatuses.value = filterStatusesList

//        clearIntervals()
//        clearDeparturesStore()
    }

    fun updateIsFilterChanged(boolean: Boolean) {
        _isFilterChanged.value = boolean
    }

    fun updateStatusOpened(boolean: Boolean) {
        if (_statusOpened.value != boolean) {
            _isFilterChanged.value = true
        }

        _statusOpened.value = boolean
        filters.edit { putBoolean("statusOpened", boolean) }
    }

    fun updateStatusClosed(boolean: Boolean) {
        if (_statusClosed.value != boolean) {
            _isFilterChanged.value = true
        }

        _statusClosed.value = boolean
        filters.edit { putBoolean("statusClosed", boolean) }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun updateFilterFromDateTime(dateTime: String) {
        if (_filterFromDateTime.value != getDateTimeFromString(dateTime)) {
            _isFilterChanged.value = true
        }

        _filterFromDateTime.value = getDateTimeFromString(dateTime)
//        filters.edit { putString("filterFromDateTime", dateTime) }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun updateFilterToDateTime(dateTime: String) {
        if (_filterToDateTime.value != getDateTimeFromString(dateTime)) {
            _isFilterChanged.value = true
        }

        _filterToDateTime.value = getDateTimeFromString(dateTime)
//        filters.edit { putString("filterToDateTime", dateTime) }
    }

    fun updateFilterAddress(address: String) {
        if (_filterAddress.value != address) {
            _isFilterChanged.value = true
        }

        _filterAddress.value = address
        filters.edit { putString("filterAddress", address) }
    }

    fun updateFilterRegions(regions: List<Int>) {
        if (_filterRegions.value != regions) {
            _isFilterChanged.value = true
        }

        _filterRegions.value = regions
        filters.edit { putString("filterRegions", gson.toJson(regions)) }
    }

    fun updateFilterType(type: Int?) {
        if (_filterType.value != type) {
            _isFilterChanged.value = true
        }

        _filterType.value = type
        filters.edit { putInt("filterType", type ?: 0) }
    }

    fun updateFilterStatuses(statuses: List<Int>) {
        if (_filterStatuses.value != statuses) {
            _isFilterChanged.value = true
        }

        _filterStatuses.value = statuses
        filters.edit { putString("filterStatuses", gson.toJson(statuses)) }
    }

    fun resetFilters() {
        _isFilterChanged.value = true
        _statusOpened.value = true
        _statusClosed.value = true
        _filterFromDateTime.value = LocalDateTime.now().with(LocalTime.MIDNIGHT)
        _filterToDateTime.value = LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT)
        _filterAddress.value = ""
        _filterRegions.value = listOf()
        _filterType.value = null
        _filterStatuses.value = DepartureStatus.getAllIds()

        filters.edit {
            putBoolean("statusOpened", true)
            putBoolean("statusClosed", true)
            putString("filterFromDateTime", LocalDateTime.now().with(LocalTime.MIDNIGHT).toString())
            putString(
                "filterToDateTime",
                LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT).toString()
            )
            putString("filterAddress", "")
            putString("filterRegions", gson.toJson(listOf<Int>()))
            putInt("filterType", 0)
            putString("filterStatuses", gson.toJson(DepartureStatus.getAllIds()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun updateDeparturesList() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true

            if (filterRegions.value.isEmpty()) {
                _departuresList.value = ApiResult.Error("No regions selected")
                isLoading.value = false
                return@launch
            }

            filterRegions.value.forEach { regionId ->
                getDepartures(
                    regionId,
                    filterFromDateTime.value.format(DateTimeFormatter.ISO_DATE_TIME),
                    filterToDateTime.value.format(DateTimeFormatter.ISO_DATE_TIME),
                    filterStatuses.value,
                    filterType.value,
                    filterAddress.value
                )
            }

            _departuresList.value = ApiResult.Success(selectDeparturesWithFilters())
            isLoading.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun getDeparture(
        regionId: Int,
        id: Long,
        fromDateTime: String,
        toDateTime: String? = fromDateTime,
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
                            return@launch
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

            _departure.value = ApiResult.Error("Departure not found")
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    suspend fun getDepartures(
        regionId: Int,
        fromDateTime: String?,
        toDateTime: String? = fromDateTime,
        statuses: List<Int>? = DepartureStatus.getAllIds(),
        type: Int? = null,
        address: String? = null
    ) {
        try {
            val region = getRegionById(regionId)

            if (region == null)
                return

            val response = departuresApi.getDepartures(
                region.url + "/api/",
                fromDateTime,
                toDateTime,
                statuses,
                type,
                address
            )
            if (response.isSuccessful) {
                val departures = response.body()

                if (departures != null) {
                    departures.map { departure -> storeDeparture(departure, regionId) }

                    if (departures.size >= 2000) {
                        getDepartures(
                            regionId,
                            fromDateTime,
                            getDepartureStartDateTime(departures[departures.size - 1])
                        )
                    }
                }
            } else {
                Log.e(
                    "DeparturesListViewModel",
                    "Error fetching departure: ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e("DeparturesListViewModel", "Exception fetching departure: ${e.message}")
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
                        departuresApi.test("${region.url}/api/")
                        region.available = true
                    }
                } catch (_: Exception) {
                    region.available = false
                    _filterRegions.value = _filterRegions.value.filter { it != region.id }
                }
            }
        }
    }

    fun storeDeparture(departure: Departure, regionId: Int) {
        val existingEntity = departuresBox.query()
            .equal(
                DepartureEntity_.departureId,
                departure.id
            )
            .build()
            .findFirst()

        if (existingEntity != null) {
            val checksum1 = existingEntity.contentChecksum()
            val checksum2 = departure.contentChecksum()

            if (checksum1 != checksum2) {
                existingEntity.state = departure.state
                existingEntity.type = departure.type
                existingEntity.subType = departure.subType
                existingEntity.description = departure.description
                existingEntity.municipalityWithExtendedCompetence =
                    departure.municipalityWithExtendedCompetence
                existingEntity.street = departure.street
                existingEntity.road = departure.road

                if (departure.gis1 != null && departure.gis2 != null) {
                    val coordinates = convertSjtskToWgs(
                        departure.gis1.toDouble(),
                        departure.gis2.toDouble()
                    )
                    existingEntity.coordinateX = coordinates.x
                    existingEntity.coordinateY = coordinates.y
                }

                departuresBox.put(existingEntity)
            }
        } else {
            val coordinates = if (departure.gis1 != null && departure.gis2 != null) {
                convertSjtskToWgs(
                    departure.gis1.toDouble(),
                    departure.gis2.toDouble()
                )
            } else null

            val newDepartureEntity = DepartureEntity(
                departureId = departure.id,
                reportedDateTime = getDateTimeLongFromString(
                    departure.reportedDateTime ?: departure.startDateTime ?: ""
                ),
//                startDateTime = departure.startDateTime,
                state = departure.state,
                type = departure.type,
                subType = departure.subType,
                description = departure.description,
                regionId = regionId,
                regionName = departure.region.name,
                districtId = departure.district.id,
                districtName = departure.district.name,
                municipality = departure.municipality,
                municipalityPart = departure.municipalityPart,
                municipalityWithExtendedCompetence = departure.municipalityWithExtendedCompetence,
                street = departure.street,
                coordinateX = coordinates?.x,
                coordinateY = coordinates?.y,
                preplanned = departure.preplanned,
                road = departure.road
            )

            departuresBox.put(newDepartureEntity)
        }
    }

    fun selectDeparturesWithFilters(): List<DepartureEntity> {
        return departuresBox.query().run {
            between(
                DepartureEntity_.reportedDateTime,
                getDateTimeLongFromString(filterFromDateTime.value.toString()),
                getDateTimeLongFromString(filterToDateTime.value.toString())
            )

            if (filterAddress.value.isNotBlank()) {
                contains(
                    DepartureEntity_.districtName,
                    filterAddress.value,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
                or()
                contains(
                    DepartureEntity_.municipality,
                    filterAddress.value,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
                or()
                contains(
                    DepartureEntity_.municipalityPart,
                    filterAddress.value,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
                or()
                contains(
                    DepartureEntity_.municipalityWithExtendedCompetence,
                    filterAddress.value,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
                or()
                contains(
                    DepartureEntity_.street,
                    filterAddress.value,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
                or()
                contains(
                    DepartureEntity_.road,
                    filterAddress.value,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
            }

            if (filterRegions.value.isNotEmpty()) {
                and()
                `in`(DepartureEntity_.regionId, filterRegions.value.toIntArray())
            }

            if (filterType.value != null) {
                and()
                greaterOrEqual(DepartureEntity_.type, filterType.value!!)
                and()
                lessOrEqual(DepartureEntity_.type, filterType.value!!)
            }

            if (filterStatuses.value != null && filterStatuses.value!!.isNotEmpty()) {
                and()
                `in`(DepartureEntity_.state, filterStatuses.value!!.toIntArray())
            }

            order(
                DepartureEntity_.reportedDateTime,
                QueryBuilder.DESCENDING
            )
            build()
        }.find()
    }
}
