package com.example.logtrack_1_1.data.repo

import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.data.entity.EventEntity
import com.example.logtrack_1_1.data.entity.EventImpactEntity
import com.example.logtrack_1_1.data.entity.MetricEntity
import com.example.logtrack_1_1.data.model.DailyMetricValue
import kotlinx.coroutines.flow.Flow

interface TrackerRepository {
    fun observeCategories(): Flow<List<CategoryEntity>>
    fun observeMetrics(): Flow<List<MetricEntity>>

    suspend fun upsertCategory(category: CategoryEntity): Long
    suspend fun upsertMetric(metric: MetricEntity): Long

    suspend fun insertEventWithImpacts(event: EventEntity, impacts: List<EventImpactEntity>): Long

    fun observeTodayValues(dayStartMillis: Long, dayEndMillis: Long): Flow<List<DailyMetricValue>>

    suspend fun deleteMetric(id: Long)
    suspend fun deleteCategory(id: Long)

    suspend fun updateCategory(category: CategoryEntity)
    suspend fun deleteCategory(category: CategoryEntity)

    suspend fun updateMetric(metric: MetricEntity)
    suspend fun deleteMetric(metric: MetricEntity)
}
