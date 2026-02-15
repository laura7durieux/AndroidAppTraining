package com.example.logtrack_1_1.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logtrack_1_1.data.entity.AggregationType
import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.data.entity.EventEntity
import com.example.logtrack_1_1.data.entity.MetricEntity
import com.example.logtrack_1_1.data.repo.TrackerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import com.example.logtrack_1_1.data.entity.EventImpactEntity



class TrackerViewModel(
    private val repo: TrackerRepository
) : ViewModel() {

    val categories = repo.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val metrics = repo.observeMetrics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayValues = run {
        val (start, end) = todayRangeMillis()
        repo.observeTodayValues(start, end)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    fun addSampleData() {
        viewModelScope.launch {
            val catId = repo.upsertCategory(CategoryEntity(name = "Health", sortOrder = 0))

            val waterId = repo.upsertMetric(
                MetricEntity(
                    name = "Water",
                    unit = "L",
                    aggregation = AggregationType.SUM,
                    categoryId = catId,
                    sortOrder = 0
                )
            )

            val focusId = repo.upsertMetric(
                MetricEntity(
                    name = "Focus",
                    unit = "min",
                    aggregation = AggregationType.SUM,
                    categoryId = catId,
                    sortOrder = 1
                )
            )

            val now = System.currentTimeMillis()

            // Event 1: Water +0.5
            repo.insertEventWithImpacts(
                event = EventEntity(timestampMillis = now, title = "Drink", note = null),
                impacts = listOf(
                    EventImpactEntity(eventId = 0, metricId = waterId, value = 0.5)
                )
            )

            // Event 2: Water +0.33
            repo.insertEventWithImpacts(
                event = EventEntity(timestampMillis = now, title = "Drink", note = null),
                impacts = listOf(
                    EventImpactEntity(eventId = 0, metricId = waterId, value = 0.33)
                )
            )

            // Event 3: Focus +25
            repo.insertEventWithImpacts(
                event = EventEntity(timestampMillis = now, title = "Work block", note = "Pomodoro"),
                impacts = listOf(
                    EventImpactEntity(eventId = 0, metricId = focusId, value = 25.0)
                )
            )
        }
    }

    fun addCategory(name: String, colorArgb: Long?) {
        viewModelScope.launch {
            repo.upsertCategory(
                CategoryEntity(
                    name = name,
                    sortOrder = categories.value.size,
                    colorArgb = colorArgb
                )
            )
        }
    }

    fun addMetric(
        name: String,
        unit: String,
        aggregation: AggregationType,
        categoryId: Long?
    ) {
        viewModelScope.launch {
            repo.upsertMetric(
                MetricEntity(
                    name = name,
                    unit = unit,
                    aggregation = aggregation,
                    categoryId = categoryId,
                    sortOrder = metrics.value.size
                )
            )
        }
    }

    private fun todayRangeMillis(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val end = cal.timeInMillis
        return start to end
    }

    fun addEvent(metricId: Long, value: Double, note: String?) {
        viewModelScope.launch {
            val event = EventEntity(
                timestampMillis = System.currentTimeMillis(),
                title = null,
                note = note?.takeIf { it.isNotBlank() }
            )
            val impacts = listOf(
                EventImpactEntity(
                    eventId = 0, // sera remplacé dans la transaction
                    metricId = metricId,
                    value = value
                )
            )
            repo.insertEventWithImpacts(event, impacts)
        }
    }

    fun addEventMulti(
        title: String?,
        note: String?,
        impacts: List<EventImpactEntity>
    ) {
        viewModelScope.launch {
            val cleanTitle = title?.trim()?.takeIf { it.isNotBlank() }
            val cleanNote = note?.trim()?.takeIf { it.isNotBlank() }

            val event = EventEntity(
                timestampMillis = System.currentTimeMillis(),
                title = cleanTitle,
                note = cleanNote
            )

            repo.insertEventWithImpacts(event, impacts)
        }
    }

    fun updateCategory(category: CategoryEntity) = viewModelScope.launch {
        repo.updateCategory(category)
    }

    fun deleteCategory(category: CategoryEntity) = viewModelScope.launch {
        repo.deleteCategory(category)
    }

    fun updateMetric(metric: MetricEntity) = viewModelScope.launch {
        repo.updateMetric(metric)
    }

    fun deleteMetric(metric: MetricEntity) = viewModelScope.launch {
        repo.deleteMetric(metric)
    }

}