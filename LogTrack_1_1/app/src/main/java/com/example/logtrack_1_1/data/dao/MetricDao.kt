package com.example.logtrack_1_1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.logtrack_1_1.data.entity.MetricEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete
import androidx.room.Update

@Dao
interface MetricDao {

    @Query("SELECT * FROM metrics ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<MetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metric: MetricEntity): Long

    @Query("DELETE FROM metrics WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Update
    suspend fun update(metric: MetricEntity)

    @Delete
    suspend fun delete(metric: MetricEntity)

    // Option “safe” : si tu supprimes une catégorie, on met les metrics dedans en Uncategorized (categoryId = null)
    @Query("UPDATE metrics SET categoryId = NULL WHERE categoryId = :categoryId")
    suspend fun detachFromCategory(categoryId: Long)
}