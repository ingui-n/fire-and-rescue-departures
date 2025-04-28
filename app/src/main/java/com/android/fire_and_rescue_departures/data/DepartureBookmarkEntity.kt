package com.android.fire_and_rescue_departures.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class DepartureBookmarkEntity(
    @Id var id: Long = 0,
    var regionId: Int,
    var departureId: String,
    var dateTime: String,
)
