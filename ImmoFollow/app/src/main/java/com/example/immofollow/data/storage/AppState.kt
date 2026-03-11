package com.example.immofollow.data.storage

import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.SpecDefinition

data class AppState(
    val houses: List<House> = emptyList(),
    val specs: List<SpecDefinition> = emptyList()
)