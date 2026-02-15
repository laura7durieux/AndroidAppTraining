package com.example.logtrack_1_1.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.logtrack_1_1.data.dao.CategoryDao
import com.example.logtrack_1_1.data.dao.EventDao
import com.example.logtrack_1_1.data.dao.MetricDao
import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.data.entity.EventEntity
import com.example.logtrack_1_1.data.entity.MetricEntity
import com.example.logtrack_1_1.data.dao.TodayDao
import com.example.logtrack_1_1.data.entity.EventImpactEntity


@Database(
    entities = [CategoryEntity::class, MetricEntity::class, EventEntity::class, EventImpactEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class LogTrackDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun metricDao(): MetricDao
    abstract fun eventDao(): EventDao

    abstract fun todayDao(): TodayDao
}