package com.example.immofollow.data.model

data class ScoreBand(
    val label: String,
    val score: Int,
    val minInclusive: Double? = null,
    val maxInclusive: Double? = null,
    val acceptedTexts: List<String> = emptyList(),
    val booleanValue: Boolean? = null
)