package com.android.fire_and_rescue_departures.repository

import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureBookmarkEntity
import com.android.fire_and_rescue_departures.data.DepartureBookmarkEntity_
import io.objectbox.Box
import io.objectbox.query.QueryBuilder

class DepartureBookmarksRepository(private val departureBookmarksBox: Box<DepartureBookmarkEntity>) {
    fun addDepartureBookmark(departure: Departure) {
        val existingEntry = departureBookmarksBox.query()
            .equal(DepartureBookmarkEntity_.departureId, departure.id.toString(), QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build().findFirst()

        if (existingEntry != null) {
            existingEntry.regionId = departure.regionId!!
            existingEntry.departureId = departure.id.toString()
            existingEntry.dateTime = (departure.startDateTime ?: departure.reportedDateTime).toString()
            departureBookmarksBox.put(existingEntry)
        } else {
            val departureBookmarkEntity = DepartureBookmarkEntity(
                regionId = departure.regionId!!,
                departureId = departure.id.toString(),
                dateTime = (departure.startDateTime ?: departure.reportedDateTime).toString()
            )
            departureBookmarksBox.put(departureBookmarkEntity)
        }
    }

    fun removeDepartureBookmark(departureId: String) {
        val query = departureBookmarksBox.query()
            .equal(DepartureBookmarkEntity_.departureId, departureId, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val result = query.findFirst()
        if (result != null) {
            departureBookmarksBox.remove(result)
        }
        query.close()
    }

    fun getAllDepartureBookmarks(): List<DepartureBookmarkEntity> {
        return departureBookmarksBox.all
    }
}
