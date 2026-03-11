package com.example.immofollow.data.model

data class SpecDefinition(
    val id: String,
    val defaultName: String,
    val category: String,
    val flexibility: String,
    val inputType: SpecInputType,
    val weight: Int,
    val bands: List<ScoreBand>,
    val options: List<String> = emptyList(),
    val visitOnly: Boolean = false
)