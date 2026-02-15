package com.example.logtrack_1_1.data.model

import com.example.logtrack_1_1.data.entity.AggregationType

data class DailyMetricValue(
    val metricId: Long,
    val value: Double?,
    val aggregation: AggregationType
)
