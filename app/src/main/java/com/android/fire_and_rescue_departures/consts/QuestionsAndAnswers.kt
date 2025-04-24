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
)
