package com.android.fire_and_rescue_departures.helpers

import android.annotation.SuppressLint
import org.locationtech.proj4j.*
import kotlin.math.abs

fun convertSjtskToWgs(gis1: Double, gis2: Double): ProjCoordinate {
    val jtskParams = """
        +proj=krovak
        +lat_0=49.5
        +lon_0=24.83333333333333
        +alpha=30.28813972222222
        +k=0.9999
        +x_0=0
        +y_0=0
        +ellps=bessel
        +towgs84=570.8,85.7,462.8,4.998,1.587,5.261,3.56
        +units=m
        +no_defs
        +axis=wsu
    """.trimIndent().replace("\n", " ")

    val crsFactory = CRSFactory()

    val jtskCRS = crsFactory.createFromParameters("JTSK", jtskParams)
    val wgs84CRS = crsFactory.createFromName("EPSG:4326")

    val transform = CoordinateTransformFactory().createTransform(jtskCRS, wgs84CRS)

    val srcCoord = ProjCoordinate(gis1, gis2)
    val dstCoord = ProjCoordinate()

    transform.transform(srcCoord, dstCoord)

    return dstCoord
}

@SuppressLint("DefaultLocale")
fun decimalToDMS(decimal: Double, latitude: Boolean = false): String {
    val degrees = decimal.toInt()
    val minutesDecimal = abs((decimal - degrees) * 60)
    val minutes = minutesDecimal.toInt()
    val seconds = (minutesDecimal - minutes) * 60

    val direction = if (latitude) {
        if (decimal >= 0) "N" else "S"
    } else {
        if (decimal >= 0) "E" else "W"
    }

    return "%d°%d'%s\"%s".format(
        abs(degrees),
        minutes,
        String.format("%.1f", abs(seconds)),
        direction
    )
}
