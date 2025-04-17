package com.android.fire_and_rescue_departures.data

sealed class DepartureSubtypes(val id: Int, val name: String) {
    object Aed : DepartureSubtypes(3530, "AED")
    object PestDisposal : DepartureSubtypes(3542, "Likvidace obtížného hmyzu")
    object OnRoad : DepartureSubtypes(3401, "Na pozemní komunikaci")
    object FalseAlarm : DepartureSubtypes(3811, "Planý poplach")
    object RadiationAccident : DepartureSubtypes(3611, "Radiační nehoda, havárie")
    object FireAlarm : DepartureSubtypes(10001, "Signalizace EPS")
    object PersonRescue : DepartureSubtypes(3211, "Vyproštění osob")
    object NaturalDisaster : DepartureSubtypes(3602, "Živelní pohroma")
    object IntoSoil : DepartureSubtypes(3402, "Do půdy")
    object MassEvacuation : DepartureSubtypes(3711, "Evakuace a ochrana obyvatel plošná")
    object HumanitarianAid : DepartureSubtypes(3603, "Humanitární pomoc")
    object LowBuildings : DepartureSubtypes(3101, "Nízké budovy")
    object TreeRemoval : DepartureSubtypes(3505, "Odstranění stromu")
    object WithInjury : DepartureSubtypes(3214, "Se zraněním")
    object TechTest : DepartureSubtypes(3921, "Technologický test")
    object PatientTransport : DepartureSubtypes(3543, "Transport pacienta")
    object Other : DepartureSubtypes(3712, "Jiné")
    object IntoWater : DepartureSubtypes(3403, "Na (do) vodní plochu(y)")
    object ObstacleRemoval : DepartureSubtypes(3526, "Odstraňování překážek")
    object OtherEvent : DepartureSubtypes(3601, "Ostatní formálně založená událost")
    object RoadClearance : DepartureSubtypes(3212, "Uvolnění komunikace, odtažení")
    object ClosedSpaces : DepartureSubtypes(3523, "Uzavřené prostory, výtah")
    object HighBuildings : DepartureSubtypes(3102, "Výškové budovy")
    object MaliciousCall : DepartureSubtypes(3931, "Zlomyslné volání")
    object IntoAir : DepartureSubtypes(3404, "Do ovzduší")
    object TrafficInfo : DepartureSubtypes(3713, "Informace pro dopravu")
    object HazardRemoval : DepartureSubtypes(3501, "Odstranění nebezpečných stavů")
    object ChimneySoot : DepartureSubtypes(3117, "Sazí v komíně")
    object FromHeight : DepartureSubtypes(3522, "Z výšky")
    object RoadCleaning : DepartureSubtypes(3213, "Úklid vozovky")
    object TransportVehicles : DepartureSubtypes(3108, "Dopravní prostředky")
    object OpenLockedArea : DepartureSubtypes(3525, "Otevření uzavřených prostor")
    object MassTransport : DepartureSubtypes(3215, "Prostředek hromadné přepravy osob")
    object FromDepth : DepartureSubtypes(3529, "Z hloubky")
    object Air : DepartureSubtypes(3241, "Letecká")
    object Railway : DepartureSubtypes(3231, "Železniční")
    object IndustrialBuildings : DepartureSubtypes(3103, "Průmyslové,zemědělské objekty,sklady")
    object FromWater : DepartureSubtypes(3521, "Z vody")
    object FieldVegetation : DepartureSubtypes(3106, "Polní porost, tráva")
    object EmergencyCooperation : DepartureSubtypes(3502, "Spolupráce se složkami IZS")
    object FromTerrain : DepartureSubtypes(3531, "Z terénu")
    object Forest : DepartureSubtypes(3110, "Lesní porost")
    object Buried : DepartureSubtypes(3524, "Zasypané,zavalené")
    object WaterPumping : DepartureSubtypes(3527, "Čerpání vody")
    object EquipmentReplacement : DepartureSubtypes(3504, "Náhrada nefunkčního zařízení")
    object Trash : DepartureSubtypes(3109, "Popelnice, kontejner")
    object MedicalRequest : DepartureSubtypes(3533, "Žádost zdravotnického zařízení")
    object ObjectDestruction : DepartureSubtypes(3503, "Destrukce objektu")
    object Waste : DepartureSubtypes(3111, "Odpad, ostatní")
    object Sheds : DepartureSubtypes(3112, "Kůlny, přístřešky")
    object GasMeasurement : DepartureSubtypes(3528, "Měření koncentrací")
    object Monitoring : DepartureSubtypes(3541, "Monitoring")
    object Transformer : DepartureSubtypes(3107, "Trafostanice, rozvodny")
    object Landfill : DepartureSubtypes(3113, "Skládka")
    object ChemicalIndustry : DepartureSubtypes(3114, "Chemický průmysl")
    object Hospital : DepartureSubtypes(3115, "Nemocnice, LDN, domovy důchodců")
    object Underground : DepartureSubtypes(3105, "Podzemní prostory,tunely")
    object GatheringPlace : DepartureSubtypes(3104, "Shromaždiště osob")
    object CableCanals : DepartureSubtypes(3116, "Kabelové kanály, kolektory")

    companion object {
        val all = listOf(
            Aed,
            PestDisposal,
            OnRoad,
            FalseAlarm,
            RadiationAccident,
            FireAlarm,
            PersonRescue,
            NaturalDisaster,
            IntoSoil,
            MassEvacuation,
            HumanitarianAid,
            LowBuildings,
            TreeRemoval,
            WithInjury,
            TechTest,
            PatientTransport,
            Other,
            IntoWater,
            ObstacleRemoval,
            OtherEvent,
            RoadClearance,
            ClosedSpaces,
            HighBuildings,
            MaliciousCall,
            IntoAir,
            TrafficInfo,
            HazardRemoval,
            ChimneySoot,
            FromHeight,
            RoadCleaning,
            TransportVehicles,
            OpenLockedArea,
            MassTransport,
            FromDepth,
            Air,
            Railway,
            IndustrialBuildings,
            FromWater,
            FieldVegetation,
            EmergencyCooperation,
            FromTerrain,
            Forest,
            Buried,
            WaterPumping,
            EquipmentReplacement,
            Trash,
            MedicalRequest,
            ObjectDestruction,
            Waste,
            Sheds,
            GasMeasurement,
            Monitoring,
            Transformer,
            Landfill,
            ChemicalIndustry,
            Hospital,
            Underground,
            GatheringPlace,
            CableCanals,
        )

        fun fromId(id: Int): DepartureSubtypes? = all.find { it.id == id }
        fun getAllIds(): List<Int> = all.map { it.id }
    }
}
