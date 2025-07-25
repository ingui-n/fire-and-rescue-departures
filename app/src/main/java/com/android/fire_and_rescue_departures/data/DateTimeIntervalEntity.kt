package com.android.fire_and_rescue_departures.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class DateTimeIntervalEntity(
    @Id var id: Long = 0,
    val from: String,
    val to: String,
    val region: Int
)
