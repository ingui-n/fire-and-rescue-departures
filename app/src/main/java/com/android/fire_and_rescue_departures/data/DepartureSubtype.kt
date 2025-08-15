package com.android.fire_and_rescue_departures.data

import com.google.gson.annotations.SerializedName

data class DepartureSubtype(
    @SerializedName("id") val id: Int,
    @SerializedName("nazev") var name: String,
    @SerializedName("typId") val typeId: Int,
)
