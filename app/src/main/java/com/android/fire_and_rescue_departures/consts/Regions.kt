package com.android.fire_and_rescue_departures.consts

data class Region(val id: Int, val name: String, var available: Boolean, val url: String)

val regions = listOf(
    Region(1, "Jihomoravský", true, "https://udalosti.firebrno.cz"),
    Region(2, "Královéhradecký", true, "https://udalostikhk.hzscr.cz"),
    Region(3, "Moravskoslezský", true, "http://webohled.hzsmsk.cz"),
    Region(4, "Plzeňský", true, "https://zasahy.hzspk.cz"),
    Region(5, "Ústecký", true, "https://udalosti.hzsulk.cz"),
    Region(6, "Vysočina", true, "https://webohled.hasici-vysocina.cz/udalosti"),
    Region(7, "Zlínský", true, "https://webohledzlk.hzscr.cz"),
)

fun getRegionById(id: Int): Region? {
    return regions.find { id == it.id }
}

fun getRegionByName(name: String): Int? {
    return regions.find { it.name == name }?.id
}
