package com.android.fire_and_rescue_departures.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.security.MessageDigest

@Entity
data class DepartureEntity(
    @Id var id: Long = 0,
    val departureId: Long,
    val regionId: Int,
    val reportedDateTime: Long,
//    val startDateTime: String?,
    var state: Int,
    var type: Int,
    var subtypeId: Int,
    var subtypeName: String? = null,
    var description: String?,
    val regionName: String?,
    val districtId: Int,
    val districtName: String?,
    val municipality: String?,
    val municipalityPart: String?,
    var municipalityWithExtendedCompetence: String?,
    var street: String?,
    var coordinateX: Double?,
    var coordinateY: Double?,
    val preplanned: Boolean,
    var road: String?,
    var isBookmarked: Boolean = false,
    var wasReported: Boolean = false,
) {
    fun contentChecksum(): String {
        val toHash = listOf(
            departureId,
            state,
            type,
            subtypeId,
            description,
            municipalityWithExtendedCompetence,
            street,
            road
        ).joinToString("|")

        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(toHash.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
