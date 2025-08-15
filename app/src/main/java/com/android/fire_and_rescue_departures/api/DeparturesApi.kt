package com.android.fire_and_rescue_departures.api

import com.android.fire_and_rescue_departures.data.Departure
import com.android.fire_and_rescue_departures.data.DepartureSubtype
import com.android.fire_and_rescue_departures.data.DepartureUnit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface DeparturesApi {
    @GET
    suspend fun getSubtypes(
        @Url url: String
    ): Response<List<DepartureSubtype>>

    @GET
    suspend fun getDepartures(
        @Url url: String,
        @Query("casOd") fromDateTime: String? = null,
        @Query("casDo") toDateTime: String? = null,
        @Query("stavIds") status: List<Int>? = null,
        @Query("typId") type: Int? = null,
        @Query("adresa") address: String? = null,
    ): Response<List<Departure>>

    @GET
    suspend fun getDepartureUnits(
        @Url url: String,
    ): Response<List<DepartureUnit>>

    @GET
    suspend fun test(
        @Url url: String,
    ): Response<String>
}
