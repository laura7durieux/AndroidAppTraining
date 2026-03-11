package com.example.immofollow.data.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

data class House(
    val id: String,
    var name: String,
    var city: String = "",
    val values: SnapshotStateMap<String, HouseSpecValue> = mutableStateMapOf()
)