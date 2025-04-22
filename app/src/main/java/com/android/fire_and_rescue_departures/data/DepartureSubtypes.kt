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
    object IndustrialBuildings : DepartureSubtypes(3103, "Průmyslové, zemědělské objekty, sklady")
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
    object Underground : DepartureSubtypes(3105, "Podzemní prostory, tunely")
    object GatheringPlace : DepartureSubtypes(3104, "Shromaždiště osob")
    object CableCanals : DepartureSubtypes(3116, "Kabelové kanály, kolektory")

    object Covid19 : DepartureSubtypes(10082, "Covid-19")
    object AirAccident : DepartureSubtypes(10092, "Letecká nehoda")
    object AtNosoviceStation : DepartureSubtypes(11001, "Na stanici Nošovice")
    object OpeningClosedSpaces : DepartureSubtypes(3525, "Otevření uzavřených prostor")
    object FullReadiness : DepartureSubtypes(10012, "Plná pohotovost")
    object RescueOfPersons : DepartureSubtypes(3211, "Vyproštění osob")
    object EvacuationAndProtection : DepartureSubtypes(3711, "Evakuace a ochrana obyvatel plošná")
    object LocalReadiness : DepartureSubtypes(10011, "Místní pohotovost")
    object AtOwnStation : DepartureSubtypes(11002, "Na vlastní zbrojnici")
    object NonUrgentOpening : DepartureSubtypes(5011, "Neakutní otevření uzavřených prostor")
    object UndergroundSpaces : DepartureSubtypes(5007, "Podzemní prostory")
    object HighRiseBuildings : DepartureSubtypes(3102, "Výškové budovy")
    object OutsideOwnStation : DepartureSubtypes(11003, "Mimo vlastní zbrojnici")
    object LocalReadinessSdl : DepartureSubtypes(9999, "Místní pohotovost k Sdl")
    object OnWaterSurface : DepartureSubtypes(3403, "Na (do) vodní plochu(y)")
    object OtherFormalEvent : DepartureSubtypes(3601, "Ostatní formálně založená událost")
    object ClosedSpacesElevator : DepartureSubtypes(3523, "Uzavřené prostory, výtah")
    object AtIvcMosnov : DepartureSubtypes(10080, "Na Ivc Mošnov")
    object FluidLeak : DepartureSubtypes(5004, "Únik provozních kapalin")
    object Helicopter : DepartureSubtypes(10077, "Vrtulník")
    object AgriculturalBuildings : DepartureSubtypes(10054, "Zemědělské objekty")
    object InsectRemoval : DepartureSubtypes(3542, "Likvidace obtížného hmyzu")
    object ObjectDisturbance : DepartureSubtypes(10013, "Narušení objektu")
    object FireBrigadeVehicleAccident : DepartureSubtypes(10078, "Nehoda vozidla HZS")
    object MethaneSensorAlert : DepartureSubtypes(5005, "Signalizace metanového čidla")
    object BuriedPersons : DepartureSubtypes(3524, "Zasypané,zavalené")
    object EmergencyInsectRemoval : DepartureSubtypes(5012, "Akutní likvidace hmyzu")
    object MosnovAirport : DepartureSubtypes(10001, "Letiště Mošnov")
    object GasSensorAlert : DepartureSubtypes(5006, "Signalizace plynového čidla")
    object KlimkoviceTunnel : DepartureSubtypes(10003, "Tunel Klimkovice")
    object FromWaterLargeArea : DepartureSubtypes(10059, "Z vody - velká plocha")
    object Highway : DepartureSubtypes(10009, "Dálnice")
    object WasteOther : DepartureSubtypes(3111, "Odpad, ostatní")
    object LysuvkyTunnel : DepartureSubtypes(10068, "Tunel Lysůvky")
    object AnimalRescue : DepartureSubtypes(10075, "Záchrana, odchyt zvířat")
    object Aviation : DepartureSubtypes(3241, "Letecká")
    object SearchMissingPerson : DepartureSubtypes(10064, "Pátrání po pohřešované osobě")
    object BinContainer : DepartureSubtypes(3109, "Popelnice, kontejner")
    object PrchalovTunnel : DepartureSubtypes(10086, "Tunel Prchalov")
    object MedicalFacilityRequest : DepartureSubtypes(3533, "Žádost zdravotnického zařízení")
    object ForestArea : DepartureSubtypes(3110, "Lesní porost")
    object FireAssistanceRefueling : DepartureSubtypes(10018, "Požární asistence - tankování")
    object Ppp : DepartureSubtypes(10079, "Ppp")
    object ProbesDrills : DepartureSubtypes(10090, "Sondy, vrty")
    object KlimkoviceTunnelRescue : DepartureSubtypes(10004, "Tunel Klimkovice - vyproštění osob")
    object FieldCropGrass : DepartureSubtypes(3106, "Polní porost, tráva")
    object FireAssistanceOther : DepartureSubtypes(10019, "Požární asistence - pož.neb.prac")
    object SuicideIntent : DepartureSubtypes(10060, "Sebevražedný úmysl")
    object KlimkoviceTunnelNoRescue :
        DepartureSubtypes(10005, "Tunel Klimkovice - bez vyproštění osob")

    object ShedsShelters : DepartureSubtypes(3112, "Kůlny, přístřešky")
    object ConcentrationMeasurement : DepartureSubtypes(3528, "Měření koncentrací")
    object LysuvkyTunnelRescue : DepartureSubtypes(10066, "Tunel Lysůvky - vyproštění osob")
    object PreMedicalAid : DepartureSubtypes(10021, "Předlékařská pomoc")
    object IzsCooperation : DepartureSubtypes(3502, "Spolupráce se složkami IZS")
    object LysuvkyTunnelNoRescue : DepartureSubtypes(10067, "Tunel Lysůvky - bez vyproštění osob")
    object EventAssistance : DepartureSubtypes(5010, "Asistence u sportovních a kulturních akcí")
    object UndergroundSpaces2 : DepartureSubtypes(3105, "Podzemní prostory")
    object PrchalovTunnelRescue : DepartureSubtypes(10084, "Tunel Prchalov - vyproštění osob")
    object HospitalsNursingHomes : DepartureSubtypes(3115, "Nemocnice, LDN, domovy důchodců")
    object PrmAssistance : DepartureSubtypes(10023, "Prm -  asistence Prm")
    object PrchalovTunnelNoRescue : DepartureSubtypes(10085, "Tunel Prchalov - bez vyproštění osob")
    object Substations : DepartureSubtypes(3107, "Trafostanice, rozvodny")
    object WaterShutdown : DepartureSubtypes(10057, "Uzavření tekoucí vody")
    object AssistanceNvz : DepartureSubtypes(10058, "Asistence při hledání Nvz")
    object Highway2 : DepartureSubtypes(10008, "Dálnice")
    object FireAssistance : DepartureSubtypes(10081, "Požární asistence")
    object KlimkoviceTunnel2 : DepartureSubtypes(10002, "Tunel Klimkovice")
    object LysuvkyTunnel2 : DepartureSubtypes(10065, "Tunel Lysůvky")
    object PrchalovTunnel2 : DepartureSubtypes(10083, "Tunel Prchalov")
    object ProbesDrills2 : DepartureSubtypes(10089, "Sondy, vrty")
    object HighlyContagiousDisease : DepartureSubtypes(10053, "Vysoce nakažlivá nákaza")


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
            Covid19,
            AirAccident,
            AtNosoviceStation,
            OpeningClosedSpaces,
            FullReadiness,
            RescueOfPersons,
            EvacuationAndProtection,
            LocalReadiness,
            AtOwnStation,
            NonUrgentOpening,
            UndergroundSpaces,
            HighRiseBuildings,
            OutsideOwnStation,
            LocalReadinessSdl,
            OnWaterSurface,
            OtherFormalEvent,
            ClosedSpacesElevator,
            AtIvcMosnov,
            FluidLeak,
            Helicopter,
            AgriculturalBuildings,
            InsectRemoval,
            ObjectDisturbance,
            FireBrigadeVehicleAccident,
            MethaneSensorAlert,
            BuriedPersons,
            EmergencyInsectRemoval,
            MosnovAirport,
            GasSensorAlert,
            KlimkoviceTunnel,
            FromWaterLargeArea,
            Highway,
            WasteOther,
            LysuvkyTunnel,
            AnimalRescue,
            Aviation,
            SearchMissingPerson,
            BinContainer,
            PrchalovTunnel,
            MedicalFacilityRequest,
            ForestArea,
            FireAssistanceRefueling,
            Ppp,
            ProbesDrills,
            KlimkoviceTunnelRescue,
            FieldCropGrass,
            FireAssistanceOther,
            SuicideIntent,
            KlimkoviceTunnelNoRescue,
            ShedsShelters,
            ConcentrationMeasurement,
            LysuvkyTunnelRescue,
            PreMedicalAid,
            IzsCooperation,
            LysuvkyTunnelNoRescue,
            EventAssistance,
            UndergroundSpaces2,
            PrchalovTunnelRescue,
            HospitalsNursingHomes,
            PrmAssistance,
            PrchalovTunnelNoRescue,
            Substations,
            WaterShutdown,
            AssistanceNvz,
            Highway2,
            FireAssistance,
            KlimkoviceTunnel2,
            LysuvkyTunnel2,
            PrchalovTunnel2,
            ProbesDrills2,
            HighlyContagiousDisease,
        )

        fun fromId(id: Int): DepartureSubtypes? = all.find { it.id == id }
        fun getAllIds(): List<Int> = all.map { it.id }
    }
}
