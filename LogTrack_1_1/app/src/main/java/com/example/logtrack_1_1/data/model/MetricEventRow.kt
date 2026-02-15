package com.example.logtrack_1_1.data.model

data class MetricEventRow(
    val eventId: Long,
    val timestampMillis: Long,
    val title: String?,
    val note: String?,
    val impactValue: Double
)
