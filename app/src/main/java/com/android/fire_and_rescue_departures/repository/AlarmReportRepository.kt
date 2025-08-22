package com.android.fire_and_rescue_departures.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.BuildConfig
import com.android.fire_and_rescue_departures.api.OSMApi
import com.android.fire_and_rescue_departures.data.DepartureEntity
import com.android.fire_and_rescue_departures.data.DepartureEntity_
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.DepartureTypes.Companion.getDepartureTypeFromId
import com.android.fire_and_rescue_departures.data.OSMAddress
import com.android.fire_and_rescue_departures.data.ReportEntity
import com.android.fire_and_rescue_departures.data.ReportEntity_
import com.android.fire_and_rescue_departures.helpers.NotificationHelper
import com.android.fire_and_rescue_departures.helpers.ScheduleAlarmHelper
import com.android.fire_and_rescue_departures.helpers.ToastHelper
import com.android.fire_and_rescue_departures.helpers.getDateTimeLongFromString
import com.android.fire_and_rescue_departures.helpers.getDrawableByType
import com.android.fire_and_rescue_departures.helpers.getFormattedDateTime
import com.android.fire_and_rescue_departures.helpers.longToIsoString
import io.objectbox.Box
import io.objectbox.kotlin.equal
import io.objectbox.query.QueryBuilder
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class AlarmReportRepository(
    private val reportBox: Box<ReportEntity>,
    private val departuresBox: Box<DepartureEntity>,
    private val departureSubtypesRepository: DepartureSubtypesRepository,
    private val scheduleAlarmHelper: ScheduleAlarmHelper,
    private val osmApi: OSMApi,
    private val departureRepository: DepartureRepository,
    private val notificationHelper: NotificationHelper,
    private val toastHelper: ToastHelper
) {
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    suspend fun triggerReport() {
        val reports = reportBox.query()
            .equal(ReportEntity_.isEnabled, true)
            .build()
            .find()

        if (reports.isEmpty()) {
            scheduleAlarmHelper.cancelReportAlarm()
            return
        }
        val groupedReports: Map<Int, List<ReportEntity>> = reports.groupBy { it.regionId }

        for ((regionId, reportList) in groupedReports) {
            val departures = getDepartures(
                regionId,
                LocalDateTime
                    .now()
                    .minus(
                        BuildConfig.REPORT_INTERVAL_MINUTES.toLong() + 2,
                        ChronoUnit.MINUTES
                    ).toString(),
                LocalDateTime.now().toString(),
            )

            if (departures.isEmpty())
                continue

            for (departure in departures) {
                if (departure.coordinateX == null || departure.coordinateY == null)
                    continue

                val reversed = reverseCoordinates(
                    departure.coordinateX!!,
                    departure.coordinateY!!,
                )

                if (reversed == null)
                    continue

                for (report in reportList) {
                    if (report.typeId != 0 && report.typeId != departure.type)
                        continue

                    if (report.county == reversed.details.county) {
                        if (report.municipality == null && report.town == null && report.suburb == null && report.village == null && report.road == null) {
                            // county
                            sendNotification(reversed, departure)
                            break
                        }

                        if (report.municipality == reversed.details.municipality) {
                            if (report.town == null && report.suburb == null && report.village == null && report.road == null) {
                                // municipality
                                sendNotification(reversed, departure)
                                break
                            }

                            if (report.town == reversed.details.town) {
                                if (report.suburb == null && report.village == null && report.road == null) {
                                    // town
                                    sendNotification(reversed, departure)
                                    break
                                }

                                if (report.suburb == reversed.details.suburb) {
                                    if (report.village == null && report.road == null) {
                                        // suburb
                                        sendNotification(reversed, departure)
                                        break
                                    }

                                    if (report.road == reversed.details.road) {
                                        // road
                                        sendNotification(reversed, departure)
                                        break
                                    }
                                }
                            } else if (report.village == reversed.details.village) {
                                if (report.road == null) {
                                    // village
                                    sendNotification(reversed, departure)
                                    break
                                }

                                if (report.road == reversed.details.road) {
                                    // road
                                    sendNotification(reversed, departure)
                                    break
                                }
                            }
                        }
                    }
                }

                delay(1_000L) // To avoid hitting API rate limits
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun addReport(
        osmAddress: OSMAddress,
        regionId: Int,
        typeId: Int,
        enabled: Boolean = true
    ): Pair<Boolean, ReportEntity> {
        val reportEntity = ReportEntity(
            regionId = regionId,
            county = osmAddress.details.county,
            municipality = osmAddress.details.municipality,
            town = osmAddress.details.town,
            suburb = osmAddress.details.suburb,
            village = osmAddress.details.village,
            road = osmAddress.details.road,
            typeId = typeId,
            isEnabled = enabled
        )

        val existingReport = reportBox.query().run {
            equal(ReportEntity_.regionId, regionId)
            and()
            equal(ReportEntity_.typeId, typeId)
            and()

            if (osmAddress.details.county == null) {
                isNull(ReportEntity_.county)
            } else {
                equal(
                    ReportEntity_.county,
                    osmAddress.details.county,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
            }

            and()

            if (osmAddress.details.municipality == null) {
                isNull(ReportEntity_.municipality)
            } else {
                equal(
                    ReportEntity_.municipality,
                    osmAddress.details.municipality,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
            }

            and()

            if (osmAddress.details.town == null) {
                isNull(ReportEntity_.town)
            } else {
                equal(
                    ReportEntity_.town,
                    osmAddress.details.town,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
            }

            and()

            if (osmAddress.details.suburb == null) {
                isNull(ReportEntity_.suburb)
            } else {
                equal(
                    ReportEntity_.suburb,
                    osmAddress.details.suburb,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
            }

            and()

            if (osmAddress.details.village == null) {
                isNull(ReportEntity_.village)
            } else {
                equal(
                    ReportEntity_.village,
                    osmAddress.details.village,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
            }

            and()

            if (osmAddress.details.road == null) {
                isNull(ReportEntity_.road)
            } else {
                equal(
                    ReportEntity_.road,
                    osmAddress.details.road,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
            }

            build()
        }
            .findFirst()

        if (enabled) {
            scheduleAlarmHelper.scheduleReportAlarm()
        }

        if (existingReport == null) {
            reportBox.put(reportEntity)

            return Pair(true, reportEntity)
        } else {
            existingReport.isEnabled = true
            reportBox.put(existingReport)

            return Pair(false, existingReport)
        }
    }

    fun removeReport(id: Long) {
        reportBox.remove(id)
    }

    fun toggleEnableReport(id: Long) {
        val report = reportBox.get(id)

        if (report != null) {
            report.isEnabled = !report.isEnabled
            reportBox.put(report)
        }
    }

    fun getReports(): List<ReportEntity> {
        return reportBox.query()
            .build()
            .find()
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    suspend fun sendNotification(reversed: OSMAddress, departure: DepartureEntity) {
        val units = departureRepository.getDepartureUnits(departure.regionId, departure.departureId)

        val departureType = getDepartureTypeFromId(departure.type)?.name ?: "Hasičský výjezd"
        val departureSubtype = departureSubtypesRepository.getDepartureSubtype(
            departure.subtypeId,
            departure.regionId
        )
        val departureDateTime = getFormattedDateTime(departure.reportedDateTime, "HH:mm")

        var message = ""

        if (units != null && units.isNotEmpty()) {
            message = "${units.size} unit" + if (units.size > 1) "s" else "" + " "
        }

        if (departureSubtype != null) {
            if (message.isNotEmpty())
                message += " | "
            message += departureSubtype
        }

        message += " on address: "

        val address = mutableListOf<String?>()

        address.add(reversed.details.county)
        address.add(reversed.details.town)
        address.add(reversed.details.village)
        address.add(reversed.details.road)
        address.add(reversed.details.houseNumber)

        val addressString = address.filterNotNull().joinToString(", ")

        notificationHelper.showNotification(
            "$departureDateTime | $departureType",
            message + addressString,
            "departureDetail/${departure.regionId}-${departure.departureId}-${
                longToIsoString(
                    departure.reportedDateTime
                )
            }",
            getDrawableByType(departure.type)
        )

        val departureEntity = departuresBox.query().run {
            equal(DepartureEntity_.departureId, departure.departureId)
            build()
        }.findFirst()

        if (departureEntity != null) {
            departureEntity.wasReported = true
            departuresBox.put(departureEntity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    suspend fun getDepartures(
        regionId: Int,
        fromDateTime: String,
        toDateTime: String = fromDateTime,
        statuses: List<Int>? = DepartureStatus.getAllIds(),
        type: Int? = null,
        address: String? = null
    ): List<DepartureEntity> {
        departureRepository.getDepartures(
            regionId,
            fromDateTime,
            toDateTime,
            statuses,
            type,
            address
        )

        return departuresBox.query().run {
            between(
                DepartureEntity_.reportedDateTime,
                getDateTimeLongFromString(fromDateTime),
                getDateTimeLongFromString(toDateTime)
            )
            and()
            equal(DepartureEntity_.regionId, regionId)
            and()
            equal(DepartureEntity_.wasReported, false)
            build()
        }.find()
    }

    suspend fun reverseCoordinates(
        coordinateX: Double,
        coordinateY: Double
    ): OSMAddress? {
        try {
            val response = osmApi.reverseAddresses(
                lat = coordinateY,
                lon = coordinateX
            )

            if (response.isSuccessful) {
                return response.body()
            } else {
                Log.e("AlarmReportRepository", "Error reversing coordinates: ${response.message()}, API failure code: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AlarmReportRepository", "Error reversing coordinates: ${e.message}")
        }
        return null
    }

    suspend fun getAddressByName(
        address: String
    ): List<OSMAddress> {
        try {
            val response = osmApi.getAddresses(
                address = address
            )

            if (response.isSuccessful) {
                return response.body() ?: emptyList()
            } else {
                Log.e("AlarmReportRepository", "Error fetching address: ${response.message()}")

                if (response.code() == 403) {
                    toastHelper.showShortToast("API access blocked.")
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmReportRepository", "Error fetching address: ${e.message}")
        }
        return emptyList()
    }
}
