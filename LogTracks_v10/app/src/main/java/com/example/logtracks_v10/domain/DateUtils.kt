package com.example.logtracks_v10.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun todayIso(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val d = now.date
    return "${d.year.toString().padStart(4,'0')}-${d.monthNumber.toString().padStart(2,'0')}-${d.dayOfMonth.toString().padStart(2,'0')}"
}
