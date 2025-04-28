package com.android.fire_and_rescue_departures.consts

enum class UIText(val value: String) {
    DEPARTURES_LIST_TITLE("Departures"),//Výjezdy
    DEPARTURES_LIST_TITLE_SHORT("Departures"),//Výjezdy
    DEPARTURES_LIST_EMPTY_LIST("No departures found"),//Nenalezeny žádné výjezdy

    DEPARTURES_MAP_TITLE("Map of Departures"),//Mapa výjezdů
    DEPARTURES_MAP_TITLE_SHORT("Map"),//Mapa
    DEPARTURES_MAP_BOTTOM_SHEET_BUTTON("Open the departure detail"),//Otevřít podrobnosti o výjezdu

    DEPARTURES_BOOKMARKS_TITLE("Bookmarks"),//Záložky
    DEPARTURES_BOOKMARKS_TITLE_SHORT("Bookmarks"),//Záložky

    BOOKMARK_ADD("Add to Bookmarks"),//Přidat do záložek
    BOOKMARK_REMOVE("Remove from Bookmarks"),//Odebrat ze záložek

    DEPARTURE_DETAIL_TITLE("Departure detail"),//Podrobnosti o výjezdu

    OK("OK"),//OK
    BACK("Back"),//Zpět
    RENEW("Renew"),//Obnovit
    RESET("Reset"),//Resetovat
    FILTER("Filter"),//Filtry
    CANCEL("Cancel"),//Zrušit
    ALL("All"),//Vše
    FILTERS("Filters"),//Filtry
    SHARE("Share"),//Sdílet

    REGIONS("Regions"),//Kraje
    STATUS("Status"),//Stav
    STATUS_ACTIVE_CHECKBOX("Active departures"),//Aktivní výjezdy
    STATUS_CLOSED_CHECKBOX("Closed departures"),//Uzavřené výjezdy
    DEPARTURE_ADDRESS_SEARCH("Departure address"),//Adresa výjezdu
    DEPARTURE_ADDRESS_LABEL("Address"),//Adresa
    DEPARTURE_TYPE("Departure type"),//Typ výjezdu
    DATE_AND_TIME("Date and time"),//Datum a čas
    DATE_FROM("From"),//Od
    DATE_FROM_SELECT_BUTTON("Select date"),//Vybrat datum
    TIME_FROM_SELECT_BUTTON("Select time"),//Vybrat čas
    DATE_TO("To"),//Do

    DISPATCHED_DATE("Dispatched date"),//ohlášena
    DEPARTURE_STATUS_OPENED("Opened"),//otevřená
    DEPARTURE_STATUS_CLOSED("Closed"),//uzavřená

    ADDRESS_REGION("Region"),//kraj
    ADDRESS_DISTRICT("District"),//okres
    ADDRESS_MUNICIPALITY("Municipality"),//obec
    ADDRESS_MUNICIPALITY_PART("Municipality part"),//část obce
    ADDRESS_STREET("Street"),//ulice
    ADDRESS_ROAD("Road"),//silnice

    DETAIL_DISPATCHED_UNITS("Dispatched units"),//Zasahující jednotky
    DETAIL_DISPATCHED_UNITS_DATE("Date of dispatch"),//Čas ohlášení
    DETAIL_DISPATCHED_UNITS_COUNT("Quantity"),//Počet
    DETAIL_DISPATCHED_UNITS_CURRENT("Currently dispatched"),//Aktuálně zasahující
    DETAIL_OPEN_ON_GOOGLE("Open on Google Maps"),//Otevřít na Google Maps
    DETAIL_OPEN_ON_MAPY("Open on Mapy.com"),//Otevřít na Mapy.com
    DEPARTURE_PREPLANNED("Preplanned"),//Předem naplánovaná

    QUESTIONS_AND_ANSWERS_TITLE("Questions and Answers"),//Časté dotazy
    QUESTIONS_AND_ANSWERS_TITLE_SHORT("Q&A"),//Časté dotazy

    NETWORK_CONNECTION_ERROR("Cannot load\nMake sure you are connected to the internet"),//Nelze načíst\nOvěřte, že jste připojeni k internetu
}
