package com.android.fire_and_rescue_departures.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.consts.getRegionById
import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureEntity
import com.android.fire_and_rescue_departures.data.DepartureEntity_
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureUnit
import com.android.fire_and_rescue_departures.helpers.convertSjtskToWgs
import com.android.fire_and_rescue_departures.helpers.getDateTimeLongFromString
import com.android.fire_and_rescue_departures.helpers.getDepartureStartDateTime
import io.objectbox.Box

class DepartureRepository(
    private val departuresApi: DeparturesApi,
    private val departuresBox: Box<DepartureEntity>,
    private val departureSubtypesRepository: DepartureSubtypesRepository
) {
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    suspend fun getDepartures(
        regionId: Int,
        fromDateTime: String,
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

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
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
                existingEntity.subtypeId = departure.subtypeId
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
                subtypeId = departure.subtypeId,
                subtypeName = departureSubtypesRepository.getDepartureSubtype(
                    departure.subtypeId,
                    regionId
                ),
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

    suspend fun getDepartureUnits(regionId: Int, id: Long): List<DepartureUnit>? {
        try {
            val region = getRegionById(regionId)

            if (region == null) {
                return null
            }

            val response =
                departuresApi.getDepartureUnits("${region.url}/api/udalosti/$id/technika")

            if (response.isSuccessful) {
                return response.body()
            }
        } catch (e: Exception) {
            Log.e("AlarmReportRepository", "Exception fetching departure units: ${e.message}")
        }

        return null
    }
}
