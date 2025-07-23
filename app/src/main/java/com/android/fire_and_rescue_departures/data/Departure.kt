package com.android.fire_and_rescue_departures.data

import com.google.gson.annotations.SerializedName

data class Departure(
    @SerializedName("id") val id: Long,
    @SerializedName("casOhlaseni") val reportedDateTime: String?,
    @SerializedName("casVzniku") val startDateTime: String?,
    @SerializedName("stavId") val state: Int,
    @SerializedName("typId") val type: Int,
    @SerializedName("podtypId") val subType: Int,
    @SerializedName("poznamkaProMedia") val description: String?,
    @SerializedName("kraj") val region: Region,
    @SerializedName("okres") val district: District,
    @SerializedName("obec") val municipality: String?,
    @SerializedName("castObce") val municipalityPart: String?,
    @SerializedName("ORP") val municipalityWithExtendedCompetence: String?,
    @SerializedName("ulice") val street: String?,
    @SerializedName("gis1") val gis1: String?,
    @SerializedName("gis2") val gis2: String?,
    @SerializedName("zoc") val preplanned: Boolean,
    @SerializedName("silnice") val road: String?,
    var regionId: Int?,
)
