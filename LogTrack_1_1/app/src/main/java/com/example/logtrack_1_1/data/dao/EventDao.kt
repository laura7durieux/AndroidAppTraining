package com.example.logtrack_1_1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.example.logtrack_1_1.data.entity.EventEntity
import com.example.logtrack_1_1.data.entity.EventImpactEntity

@Dao
interface EventDao {

    @Insert
    suspend fun insertEvent(event: EventEntity): Long

    @Insert
    suspend fun insertImpacts(impacts: List<EventImpactEntity>): List<Long>

    @Transaction
    suspend fun insertEventWithImpacts(
        event: EventEntity,
        impacts: List<EventImpactEntity>
    ): Long {
        val eventId = insertEvent(event)
        if (impacts.isNotEmpty()) {
            insertImpacts(impacts.map { it.copy(eventId = eventId) })
        }
        return eventId
    }
}
