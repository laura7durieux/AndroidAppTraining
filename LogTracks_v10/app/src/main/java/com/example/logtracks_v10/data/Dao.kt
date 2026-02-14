package com.example.logtracks_v10.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class MetricWithTodayValue(
    @Embedded val metric: MetricEntity,
    @ColumnInfo(name = "todayId") val todayId: Long?,
    @ColumnInfo(name = "todayNumber") val todayNumber: Double?,
    @ColumnInfo(name = "todayText") val todayText: String?,
    @ColumnInfo(name = "todayBool") val todayBool: Boolean?
)

@Dao
interface TrackerDao {

    @Query("SELECT * FROM MetricEntity WHERE active = 1 ORDER BY `order`, id")
    fun observeActiveMetrics(): Flow<List<MetricEntity>>

    @Insert
    suspend fun insertMetric(metric: MetricEntity): Long

    @Update
    suspend fun updateMetric(metric: MetricEntity)

    @Delete
    suspend fun deleteMetric(metric: MetricEntity)

    // Liste des métriques + valeur du jour (JOIN)
    @Query("""
        SELECT m.*,
               v.id as todayId,
               v.numberValue as todayNumber,
               v.textValue as todayText,
               v.boolValue as todayBool
        FROM MetricEntity m
        LEFT JOIN DailyValueEntity v
          ON v.metricId = m.id AND v.date = :date
        WHERE m.active = 1
        ORDER BY m.`order`, m.id
    """)
    fun observeMetricsWithValueForDate(date: String): Flow<List<MetricWithTodayValue>>


    // Upsert “maison” : si valeur existe (unique date+metric), update sinon insert
    @Query("SELECT * FROM DailyValueEntity WHERE date = :date AND metricId = :metricId LIMIT 1")
    suspend fun getValue(date: String, metricId: Long): DailyValueEntity?

    @Insert
    suspend fun insertValue(value: DailyValueEntity): Long

    @Update
    suspend fun updateValue(value: DailyValueEntity)

    @Query("SELECT * FROM DailyValueEntity WHERE metricId = :metricId ORDER BY date DESC")
    fun observeHistory(metricId: Long): Flow<List<DailyValueEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE active = 1 ORDER BY `order`, id")
    fun observeCategories(): kotlinx.coroutines.flow.Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(c: CategoryEntity): Long

    @Update
    suspend fun updateCategory(c: CategoryEntity)

    @Delete
    suspend fun deleteCategory(c: CategoryEntity)


}
