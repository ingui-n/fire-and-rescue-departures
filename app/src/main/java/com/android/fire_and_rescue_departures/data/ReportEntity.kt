package com.android.fire_and_rescue_departures.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ReportEntity(
    @Id var id: Long = 0,
    val regionId: Int,

    val county: String? = null,
    val municipality: String? = null,
    val town: String? = null,
    val suburb: String? = null,
    val village: String? = null,
    val road: String? = null,

    val typeId: Int,
    var isEnabled: Boolean = true
)
