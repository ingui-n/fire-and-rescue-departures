package com.android.fire_and_rescue_departures.consts

data class Region(val id: Int, val name: String, val available: Boolean, val url: String)

val regions = listOf(//todo test connection and update active
    Region(1, "Jihomoravský", true, "https://udalosti.firebrno.cz"),
    Region(2, "Královéhradecký", true, "https://udalostikhk.hzscr.cz"),// currently false
    Region(3, "Moravskoslezský", true, "http://webohled.hzsmsk.cz"),
    Region(4, "Plzeňský", true, "https://zasahy.hzspk.cz"),
    Region(5, "Ústecký", true, "https://udalosti.hzsulk.cz"),
    Region(6, "Vysočina", true, "https://webohled.hasici-vysocina.cz/udalosti"),
    Region(7, "Zlínský", true, "https://webohledzlk.hzscr.cz/udalosti"),// currently false
)


fun getRegionById(id: Int): Region? {
    return regions.find { id == it.id }
}
