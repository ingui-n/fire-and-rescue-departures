package com.android.fire_and_rescue_departures.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.fire_and_rescue_departures.BuildConfig
import com.android.fire_and_rescue_departures.api.OSMApi
import com.android.fire_and_rescue_departures.data.DepartureEntity
import com.android.fire_and_rescue_departures.data.DepartureEntity_
import com.android.fire_and_rescue_departures.data.DepartureStatus
import com.android.fire_and_rescue_departures.data.OSMAddress
import com.android.fire_and_rescue_departures.data.ReportEntity
import com.android.fire_and_rescue_departures.data.ReportEntity_
import com.android.fire_and_rescue_departures.helpers.NotificationHelper
import com.android.fire_and_rescue_departures.helpers.ScheduleAlarmHelper
import com.android.fire_and_rescue_departures.helpers.getDateTimeLongFromString
import io.objectbox.Box
import io.objectbox.kotlin.equal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class AlarmReportRepository(
    private val reportBox: Box<ReportEntity>,
    private val departuresBox: Box<DepartureEntity>,
    private val scheduleAlarmHelper: ScheduleAlarmHelper,
    private val osmApi: OSMApi,
    private val departureRepository: DepartureRepository,
    private val notificationHelper: NotificationHelper
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
                        60_000L * (BuildConfig.REPORT_INTERVAL_MINUTES.toLong() + 2),
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
                    if (report.county == reversed.details.county) {
                        if (report.municipality == null && report.town == null && report.suburb == null && report.village == null && report.road == null) {
                            //todo build for county
                            sendNotification(reversed, departure)
                            break
                        }

                        if (report.municipality == reversed.details.municipality) {
                            if (report.town == null && report.suburb == null && report.village == null && report.road == null) {
                                //todo build for municipality
                                sendNotification(reversed, departure)
                                break
                            }

                            if (report.town == reversed.details.town) {
                                if (report.suburb == null && report.village == null && report.road == null) {
                                    //todo build for town
                                    sendNotification(reversed, departure)
                                    break
                                }

                                if (report.suburb == reversed.details.suburb) {
                                    if (report.village == null && report.road == null) {
                                        //todo build for suburb
                                        sendNotification(reversed, departure)
                                        break
                                    }

                                    if (report.road == reversed.details.road) {
                                        //todo build for road
                                        sendNotification(reversed, departure)
                                        break
                                    }
                                }
                            } else if (report.village == reversed.details.village) {
                                if (report.road == null) {
                                    //todo build for village
                                    sendNotification(reversed, departure)
                                    break
                                }

                                if (report.road == reversed.details.road) {
                                    //todo build for road
                                    sendNotification(reversed, departure)
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun addReport(osmAddress: OSMAddress, regionId: Int, typeId: Int) {
        val reportEntity = ReportEntity(
            regionId = regionId,
            county = osmAddress.details.county,
            municipality = osmAddress.details.municipality,
            town = osmAddress.details.town,
            suburb = osmAddress.details.suburb,
            village = osmAddress.details.village,
            road = osmAddress.details.road,
            typeId = typeId,
            isEnabled = true
        )

        val existingReport = reportBox.query().run {
            equal(ReportEntity_.regionId, regionId)
            and()
            equal(ReportEntity_.typeId, typeId)
            and()

            if (osmAddress.details.county == null) {
                isNull(ReportEntity_.county)
            } else {
                ReportEntity_.county.equal(osmAddress.details.county)
            }

            and()

            if (osmAddress.details.municipality == null) {
                isNull(ReportEntity_.county)
            } else {
                ReportEntity_.municipality.equal(osmAddress.details.municipality)
            }

            and()

            if (osmAddress.details.town == null) {
                isNull(ReportEntity_.county)
            } else {
                ReportEntity_.town.equal(osmAddress.details.town)
            }

            and()

            if (osmAddress.details.suburb == null) {
                isNull(ReportEntity_.county)
            } else {
                ReportEntity_.suburb.equal(osmAddress.details.suburb)
            }

            and()

            if (osmAddress.details.village == null) {
                isNull(ReportEntity_.county)
            } else {
                ReportEntity_.village.equal(osmAddress.details.village)
            }

            and()

            if (osmAddress.details.road == null) {
                isNull(ReportEntity_.county)
            } else {
                ReportEntity_.road.equal(osmAddress.details.road)
            }

            build()
        }
            .findFirst()

        if (existingReport == null) {
            reportBox.put(reportEntity)
        } else {
            existingReport.isEnabled = true
            reportBox.put(existingReport)
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendNotification(reversed: OSMAddress, departure: DepartureEntity) {
        //todo build the message
        val units = departureRepository.getDepartureUnits(departure.regionId, departure.departureId)

        notificationHelper.showNotification("test", "this is test notifi: ${reversed.displayName}")

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
                lat = coordinateX,
                lot = coordinateY
            )

            if (response.isSuccessful) {
                return response.body()
            } else {
                Log.e("AlarmReportRepository", "Error reversing coordinates: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("AlarmReportRepository", "Error reversing coordinates: ${e.message}")
        }
        return null
    }
}
