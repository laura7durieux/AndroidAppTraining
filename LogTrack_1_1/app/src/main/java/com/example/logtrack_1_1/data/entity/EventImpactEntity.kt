package com.example.logtrack_1_1.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "event_impacts",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MetricEntity::class,
            parentColumns = ["id"],
            childColumns = ["metricId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("eventId"),
        Index("metricId"),
        Index(value = ["metricId", "eventId"])
    ]
)
data class EventImpactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: Long,
    val metricId: Long,
    val value: Double
)
