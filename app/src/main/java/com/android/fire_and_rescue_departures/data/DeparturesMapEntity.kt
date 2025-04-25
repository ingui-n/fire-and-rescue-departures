package com.android.fire_and_rescue_departures.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class DeparturesMapEntity(
    @Id var id: Long = 0,
    var type: String,
    var departureTime: String,
)
