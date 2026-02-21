package com.example.logtrack_1_1.ui.nav

sealed class Route(val path: String) {
    data object Today : Route("today")
    data object Insights : Route("insights")
    data object Settings : Route("settings")

    data object AddEvent : Route("add_event")
}