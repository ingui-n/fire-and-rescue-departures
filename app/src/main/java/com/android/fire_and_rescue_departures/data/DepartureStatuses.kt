package com.android.fire_and_rescue_departures.data

sealed class DepartureStatus(val id: Int, val name: String) {
    object ExportedZozOnline : DepartureStatus(620, "Exportovaná ZOZ Online")
    object ExportedToSk : DepartureStatus(520, "Exportovaná do SK")
    object Initialized : DepartureStatus(200, "Inicializovaná")
    object Liquidated : DepartureStatus(440, "Likvidovaná")
    object Localized : DepartureStatus(430, "Lokalizovaná")
    object LastUnitDeparted : DepartureStatus(450, "Odjezd poslední jednotky")
    object Postponed : DepartureStatus(300, "Odložená")
    object Rejected : DepartureStatus(120, "Odmítnuto")
    object OpenEnRoute : DepartureStatus(410, "Otevřená SaP na cestě")
    object OpenOnSite : DepartureStatus(420, "Otevřená, SaP na místě")
    object OpenNoSap : DepartureStatus(400, "Otevřená, bez SaP")
    object ConfirmedByRegional : DepartureStatus(780, "Potvrzená krajským garantem")
    object TakenOver : DepartureStatus(210, "Převzatá")
    object TakenOverVz : DepartureStatus(600, "Převzatá VZ")
    object TakenOverZpp : DepartureStatus(700, "Převzatá ZPP")
    object TakenOverBySsu : DepartureStatus(750, "Převzatá garantem za SSU")
    object ReadyForArchiving : DepartureStatus(800, "Připravená k archivaci")
    object FinishedOs : DepartureStatus(500, "Ukončená OS")
    object FinishedVz : DepartureStatus(610, "Ukončená VZ")
    object FinishedZpp : DepartureStatus(710, "Ukončená ZPP")
    object FinishedBySsu : DepartureStatus(760, "Ukončená garantem za SSU")
    object ClosedOs : DepartureStatus(510, "Uzavřená OS")
    object LinkedToSolvedEvent : DepartureStatus(130, "Zapracováno k řešené události")
    object WaitingRead : DepartureStatus(110, "Čekající - přečteno")
    object WaitingForProcessing : DepartureStatus(100, "Čekající na odbavení")

    companion object {
        val all = listOf(
            ExportedZozOnline,
            ExportedToSk,
            Initialized,
            Liquidated,
            Localized,
            LastUnitDeparted,
            Postponed,
            Rejected,
            OpenEnRoute,
            OpenOnSite,
            OpenNoSap,
            ConfirmedByRegional,
            TakenOver,
            TakenOverVz,
            TakenOverZpp,
            TakenOverBySsu,
            ReadyForArchiving,
            FinishedOs,
            FinishedVz,
            FinishedZpp,
            FinishedBySsu,
            ClosedOs,
            LinkedToSolvedEvent,
            WaitingRead,
            WaitingForProcessing
        )

        fun fromId(id: Int): DepartureStatus? = all.find { it.id == id }
        fun getAllIds(): List<Int> = all.map { it.id }
    }
}
