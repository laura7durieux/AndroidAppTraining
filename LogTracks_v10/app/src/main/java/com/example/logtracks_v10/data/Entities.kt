package com.example.logtracks_v10.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class MetricType { NUMBER, MONEY, DURATION_MIN, BOOLEAN, TEXT }

@Entity
data class MetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                // "Décisions prises"
    val type: MetricType,            // NUMBER / MONEY / ...
    val unit: String = "",           // "€", "min", "count"
    val active: Boolean = true,
    val order: Int = 0,               // pour trier dans l'écran du jour
    val categoryId: Long? = null     // Gestions des categories
)

@Entity(
    indices = [Index(value = ["date", "metricId"], unique = true)]
)
data class DailyValueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                // ISO "2026-02-14"
    val metricId: Long,

    // Stockage polyvalent :
    val numberValue: Double? = null,
    val textValue: String? = null,
    val boolValue: Boolean? = null
)
