package com.android.fire_and_rescue_departures.data

import com.google.gson.annotations.SerializedName

data class OSMAddress(
    @SerializedName("osm_id") val id: Long,
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("osm_type") val osmType: String,
    @SerializedName("class") val osmClass: String,
    @SerializedName("type") val type: String,
    @SerializedName("addresstype") val addressType: String,
    @SerializedName("name") val name: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("address") val details: OSMAddressDetails,
)

data class OSMAddressDetails(
    @SerializedName("county") val county: String?,
    @SerializedName("municipality") val municipality: String?,
    @SerializedName("town") val town: String?,
    @SerializedName("village") val village: String?,
    @SerializedName("road") val road: String?,
    @SerializedName("suburb") val suburb: String?,
    @SerializedName("city_district") val cityDistrict: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("house_number") val houseNumber: String?,
)
