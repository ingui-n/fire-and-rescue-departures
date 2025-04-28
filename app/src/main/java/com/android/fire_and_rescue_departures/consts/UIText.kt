package com.android.fire_and_rescue_departures.consts

enum class UIText(val value: String) {
    DEPARTURES_LIST_TITLE("Departures"),
    DEPARTURES_LIST_TITLE_SHORT("Departures"),
    DEPARTURES_LIST_EMPTY_LIST("No departures found"),

    DEPARTURES_MAP_TITLE("Map of Departures"),
    DEPARTURES_MAP_TITLE_SHORT("Map"),
    DEPARTURES_MAP_BOTTOM_SHEET_BUTTON("Open the departure detail"),

    DEPARTURES_BOOKMARKS_TITLE("Bookmarks"),
    DEPARTURES_BOOKMARKS_TITLE_SHORT("Bookmarks"),

    BOOKMARK_ADD("Add to Bookmarks"),//Přidat do záložek
    BOOKMARK_REMOVE("Remove from Bookmarks"),//Odebrat ze záložek

    DEPARTURE_DETAIL_TITLE("Departure detail"),

    OK("OK"),
    BACK("Back"),
    RENEW("Renew"),
    RESET("Reset"),
    FILTER("Filter"),
    CANCEL("Cancel"),
    ALL("All"),
    FILTERS("Filters"),
    SHARE("Share"),//Sdílet

    REGIONS("Regions"),
    STATUS("Status"),
    STATUS_ACTIVE_CHECKBOX("Active departures"),
    STATUS_CLOSED_CHECKBOX("Closed departures"),
    DEPARTURE_ADDRESS_SEARCH("Departure address"),
    DEPARTURE_ADDRESS_LABEL("Address"),
    DEPARTURE_TYPE("Departure type"),
    DATE_AND_TIME("Date and time"),
    DATE_FROM("From"),
    DATE_FROM_SELECT_BUTTON("Select date"),
    TIME_FROM_SELECT_BUTTON("Select time"),
    DATE_TO("To"),

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

    QUESTIONS_AND_ANSWERS_TITLE("Questions and Answers"),
    QUESTIONS_AND_ANSWERS_TITLE_SHORT("Q&A"),

    NETWORK_CONNECTION_ERROR("Cannot load\nMake sure you are connected to the internet"),//Nelze načíst\nOvěřte, že jste připojeni k internetu
}
