package com.android.fire_and_rescue_departures.api

import retrofit2.http.GET
import retrofit2.http.Url
import com.android.fire_and_rescue_departures.BuildConfig
import com.android.fire_and_rescue_departures.data.OSMAddress
import retrofit2.http.Query
import retrofit2.Response

interface OSMApi {
    @GET
    suspend fun getAddresses(
        @Url url: String = BuildConfig.OPEN_STREET_MAP_API_URL + "/search",
        @Query("q") address: String,
        @Query("limit") limit: Int = 10,
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("countrycodes") countryCodes: String = "cz",
        @Query("accept-language") acceptedLanguage: String = "cz",
        @Query("format") format: String = "json"
    ): Response<List<OSMAddress>>

    @GET
    suspend fun reverseAddresses(
        @Url url: String = BuildConfig.OPEN_STREET_MAP_API_URL + "/reverse",
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("countrycodes") countryCodes: String = "cz",
        @Query("accept-language") acceptedLanguage: String = "cz",
        @Query("format") format: String = "json"
    ): Response<OSMAddress>
}
