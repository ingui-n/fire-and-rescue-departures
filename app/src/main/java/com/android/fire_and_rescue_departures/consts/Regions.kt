package com.android.fire_and_rescue_departures.consts

data class Region(val id: Int, val name: String, val available: Boolean)

val regions = listOf(
    Region(1, "Jihomoravský", true),
    Region(2, "Královéhradecký", false),
    Region(3, "Moravskoslezský", true),
    Region(4, "Plzeňský", true),
    Region(5, "Ústecký", true),
    Region(6, "Vysočina", true),
    Region(7, "Zlínský", false),
)
