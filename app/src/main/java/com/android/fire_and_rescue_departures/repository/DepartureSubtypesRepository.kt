package com.android.fire_and_rescue_departures.repository

import android.util.Log
import com.android.fire_and_rescue_departures.api.DeparturesApi
import com.android.fire_and_rescue_departures.consts.getRegionById
import com.android.fire_and_rescue_departures.data.DepartureSubtypeEntity
import com.android.fire_and_rescue_departures.data.DepartureSubtypeEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DepartureSubtypesRepository(
    private val departuresApi: DeparturesApi,
    private val departureSubtypesBox: Box<DepartureSubtypeEntity>
) {
    suspend fun fetchDepartureSubtypes(regionId: Int) {
        try {
            val region = getRegionById(regionId)

            if (region == null) {
                Log.e("DeparturesListViewModel", "Region not found")
                return
            }

            val response = departuresApi.getSubtypes("${region.url}/api/podtypy")

            if (response.isSuccessful) {
                val data = response.body()

                if (data != null) {
                    data.forEach { subtype ->
                        val existingSubtype = departureSubtypesBox.query().run {
                            equal(DepartureSubtypeEntity_.subtypeId, subtype.id)
                            and()
                            equal(DepartureSubtypeEntity_.regionId, regionId)
                            build()
                        }.findFirst()

                        if (existingSubtype == null) {
                            val subtypeEntity = DepartureSubtypeEntity(
                                name = subtype.name.lowercase().replaceFirstChar { it.uppercase() },
                                subtypeId = subtype.id,
                                typeId = subtype.typeId,
                                regionId = regionId
                            )
                            departureSubtypesBox.put(subtypeEntity)
                        }
                    }
                } else {
                    Log.e("DeparturesListViewModel", "Data is null")
                }
            } else {
                Log.e(
                    "DeparturesListViewModel",
                    "Error fetching departure subtypes: ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e("DeparturesListViewModel", "Exception fetching departure subtypes: ${e.message}")
        }
    }

    suspend fun checkoutSubtypes(regionId: Int) {
        val testSubtype = departureSubtypesBox.query().run {
            equal(DepartureSubtypeEntity_.regionId, regionId)
            build()
        }.findFirst()

        if (testSubtype == null) {
            fetchDepartureSubtypes(regionId)
        }
    }

    fun getDepartureSubtype(
        subtypeId: Int,
        regionId: Int,
    ): String? {
        val subtype = departureSubtypesBox.query().run {
            equal(DepartureSubtypeEntity_.subtypeId, subtypeId)
            and()
            equal(DepartureSubtypeEntity_.regionId, regionId)
            build()
        }.findFirst()?.name

        if (subtype == null) {
            CoroutineScope(Dispatchers.IO).launch {
                fetchDepartureSubtypes(regionId)
            }
        }

        return subtype
    }
}
