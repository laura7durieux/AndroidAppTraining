package com.example.logtrack_1_1.data.db

import androidx.room.TypeConverter
import com.example.logtrack_1_1.data.entity.AggregationType

class RoomConverters {
    @TypeConverter
    fun fromAggregation(value: AggregationType): String = value.name

    @TypeConverter
    fun toAggregation(value: String): AggregationType = AggregationType.valueOf(value)
}