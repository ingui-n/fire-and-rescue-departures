package com.android.fire_and_rescue_departures.repository

import com.android.fire_and_rescue_departures.data.DeparturesMapEntity
import io.objectbox.Box

class DeparturesMapRepository(private val departuresMapBox: Box<DeparturesMapEntity>) {
    //todo functions

    fun getAllDepartures(): List<DeparturesMapEntity> {
        return departuresMapBox.all
    }
}
