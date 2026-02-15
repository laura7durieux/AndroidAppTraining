package com.example.logtrack_1_1.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.logtrack_1_1.data.model.DailyMetricValue
import kotlinx.coroutines.flow.Flow

@Dao
interface TodayDao {

    @Query("""
        SELECT 
            m.id AS metricId,
            SUM(CASE WHEN e.id IS NOT NULL THEN i.value END) AS value,
            m.aggregation AS aggregation
        FROM metrics m
        LEFT JOIN event_impacts i ON i.metricId = m.id
        LEFT JOIN events e 
            ON e.id = i.eventId
            AND e.timestampMillis >= :dayStartMillis 
            AND e.timestampMillis < :dayEndMillis
        WHERE m.aggregation = 'SUM'
        GROUP BY m.id
        ORDER BY m.sortOrder ASC, m.name ASC
    """)
    fun observeTodaySum(dayStartMillis: Long, dayEndMillis: Long): Flow<List<DailyMetricValue>>

    @Query("""
        SELECT 
            m.id AS metricId,
            AVG(CASE WHEN e.id IS NOT NULL THEN i.value END) AS value,
            m.aggregation AS aggregation
        FROM metrics m
        LEFT JOIN event_impacts i ON i.metricId = m.id
        LEFT JOIN events e 
            ON e.id = i.eventId
            AND e.timestampMillis >= :dayStartMillis 
            AND e.timestampMillis < :dayEndMillis
        WHERE m.aggregation = 'AVG'
        GROUP BY m.id
        ORDER BY m.sortOrder ASC, m.name ASC
    """)
    fun observeTodayAvg(dayStartMillis: Long, dayEndMillis: Long): Flow<List<DailyMetricValue>>

    @Query("""
    SELECT 
        e.id AS eventId,
        e.timestampMillis AS timestampMillis,
        e.title AS title,
        e.note AS note,
        i.value AS impactValue
    FROM events e
    INNER JOIN event_impacts i ON i.eventId = e.id
    WHERE i.metricId = :metricId
      AND e.timestampMillis >= :dayStartMillis
      AND e.timestampMillis < :dayEndMillis
    ORDER BY e.timestampMillis DESC
""")
    fun observeTodayEventsForMetric(
        metricId: Long,
        dayStartMillis: Long,
        dayEndMillis: Long
    ): Flow<List<com.example.logtrack_1_1.data.model.MetricEventRow>>

}
