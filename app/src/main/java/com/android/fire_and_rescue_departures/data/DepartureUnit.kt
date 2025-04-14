package com.android.fire_and_rescue_departures.data

import com.google.gson.annotations.SerializedName

data class DepartureUnit(
    @SerializedName("typ") val type: String?,
    @SerializedName("jednotka") val unit: String?,
    @SerializedName("pocet") val count: Int?,
    @SerializedName("aktualniPocet") val currentCount: Int?,
    @SerializedName("casOhlaseni") val callDateTime: String?,
)
