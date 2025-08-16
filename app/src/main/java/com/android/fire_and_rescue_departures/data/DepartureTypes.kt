package com.android.fire_and_rescue_departures.data

sealed class DepartureTypes(val id: Int, val name: String) {
    object Fire : DepartureTypes(3100, "Požár")
    object CarAccident : DepartureTypes(3200, "Dopravní nehoda")
    object LeakageOfDangerousSubstances : DepartureTypes(3400, "Únik nebezpečných látek")
    object TechnicalHelp : DepartureTypes(3500, "Technická pomoc")
    object RescueOfPersonsAndAnimals : DepartureTypes(3550, "Záchrana osob a zvířat")
    object OtherEvent : DepartureTypes(3700, "Jiná událost")
    object FormallyCreatedEvent : DepartureTypes(3600, "Formálně založená událost")
    object TechnologicalTest : DepartureTypes(3900, "Technologický test")
    object FalseAlarm : DepartureTypes(3800, "Planý poplach")
    object EventOnObject : DepartureTypes(5000, "Událost na objekt")

    companion object {
        val all = listOf(
            Fire,
            CarAccident,
            LeakageOfDangerousSubstances,
            TechnicalHelp,
            RescueOfPersonsAndAnimals,
            OtherEvent,
            FalseAlarm,
            FormallyCreatedEvent,
            EventOnObject,
            TechnologicalTest
        )

        fun fromId(id: Int): DepartureTypes? = all.find { it.id == id }
        fun getAllIds(): List<Int> = all.map { it.id }
    }
}
