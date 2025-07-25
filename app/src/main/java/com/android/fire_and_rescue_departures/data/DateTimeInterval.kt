package com.android.fire_and_rescue_departures.data

import java.time.LocalDateTime

data class DateTimeInterval(
    val region: Int,
    val from: LocalDateTime,
    val to: LocalDateTime
)
