package com.android.fire_and_rescue_departures.data

import com.google.gson.annotations.SerializedName

data class Region(
    @SerializedName("id") val id: Int,
    @SerializedName("nazev") val name: String,
)
