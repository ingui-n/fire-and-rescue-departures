package com.android.fire_and_rescue_departures.api

import retrofit2.http.GET
import retrofit2.http.Url
import com.android.fire_and_rescue_departures.BuildConfig
import com.android.fire_and_rescue_departures.data.OSMAddress
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

interface OSMApi {
    @GET
    suspend fun getAddresses(
        @Url url: String = BuildConfig.OPEN_STREET_MAP_API_URL,
        @Path ("search") search: String = "search",
        @Query("q") address: String,
        @Query("limit") limit: Int = 15,
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("countrycodes") countryCodes: String = "cz",
        @Query("accept-language") acceptedLanguage: String = "cz",
        @Query("format") format: String = "json"
    ): Response<List<OSMAddress>>

    @GET
    suspend fun reverseAddresses(
        @Url url: String = BuildConfig.OPEN_STREET_MAP_API_URL,
        @Path ("reverse") reverse: String = "reverse",
        @Query("lat") lat: Double,
        @Query("lot") lot: Double,
        @Query("countrycodes") countryCodes: String = "cz",
        @Query("accept-language") acceptedLanguage: String = "cz",
        @Query("format") format: String = "json"
    ): Response<OSMAddress>
}
