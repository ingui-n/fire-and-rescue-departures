package com.android.fire_and_rescue_departures.consts

data class FaqItem(val title: String, val text: String)


val faqItems = listOf(
    FaqItem(
        "Proč se nezobrazují výjezdy ze všech krajů v ČR?",
        "Ne všechny kraje používají stejný veřejný systém pro zobrazení výjezdů hasičů."
    ),
    FaqItem(
        "Našli jste chybu, nebo máte nápad na zlepšení?",
        "Napište nám ho do recenze na Google Play, nebo vytvořte issue na GitHubu."
    ),
    FaqItem(
        "Proč jsou notifikace opožděné?",
        "Data o výjezdech jsou ve většině případů uveřejňována s určitým zpožděním. Může to být 5 až třeba 20 minut."
    ),
    FaqItem(
        "Proč je vyhledávání adres pomalé?",
        "Vyhledávání adres probíhá přes Open Street Map API. To je zdarma a veřejně dostupné, ale má určitá omezení na počet požadavků za minutu."
    ),
)
