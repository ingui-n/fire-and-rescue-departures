package com.android.fire_and_rescue_departures.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class DepartureSubtypeEntity(
    @Id var id: Long = 0,
    var name: String,
    val subtypeId: Int,
    var typeId: Int,
    var regionId: Int? = 0,
)
