package com.example.logtrack_1_1.data.repo

import com.example.logtrack_1_1.data.dao.CategoryDao
import com.example.logtrack_1_1.data.dao.EventDao
import com.example.logtrack_1_1.data.dao.MetricDao
import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.data.entity.EventEntity
import com.example.logtrack_1_1.data.entity.MetricEntity
import kotlinx.coroutines.flow.Flow
import com.example.logtrack_1_1.data.dao.TodayDao
import com.example.logtrack_1_1.data.model.DailyMetricValue
import kotlinx.coroutines.flow.combine
import com.example.logtrack_1_1.data.entity.EventImpactEntity

class TrackerRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val metricDao: MetricDao,
    private val eventDao: EventDao,
    private val todayDao: TodayDao
) : TrackerRepository {

    override fun observeCategories(): Flow<List<CategoryEntity>> = categoryDao.observeAll()
    override fun observeMetrics(): Flow<List<MetricEntity>> = metricDao.observeAll()

    override suspend fun upsertCategory(category: CategoryEntity): Long =
        categoryDao.upsert(category)

    override suspend fun upsertMetric(metric: MetricEntity): Long =
        metricDao.upsert(metric)

    override suspend fun insertEventWithImpacts(event: EventEntity, impacts: List<EventImpactEntity>): Long =
        eventDao.insertEventWithImpacts(event, impacts)

    override suspend fun deleteMetric(id: Long) {
        metricDao.deleteById(id)
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.deleteById(id)
    }

    override fun observeTodayValues(dayStartMillis: Long, dayEndMillis: Long): Flow<List<DailyMetricValue>> {
        return combine(
            todayDao.observeTodaySum(dayStartMillis, dayEndMillis),
            todayDao.observeTodayAvg(dayStartMillis, dayEndMillis)
        ) { sums, avgs ->
            // On concatène : chaque metric est soit SUM soit AVG
            sums + avgs
        }
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.update(category)
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        // SAFE: on détache les metrics avant de delete la catégorie
        metricDao.detachFromCategory(category.id)
        categoryDao.delete(category)
    }

    override suspend fun updateMetric(metric: MetricEntity) {
        metricDao.update(metric)
    }

    override suspend fun deleteMetric(metric: MetricEntity) {
        metricDao.delete(metric)
    }

}