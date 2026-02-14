package com.example.logtracks_v10.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.logtracks_v10.data.*
import com.example.logtracks_v10.domain.todayIso
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.logtracks_v10.data.CategoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TrackerViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = TrackerDb.get(app).dao()

    private val _selectedDate = MutableStateFlow(todayIso())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val todayItems: StateFlow<List<MetricWithTodayValue>> =
        _selectedDate.flatMapLatest { date -> dao.observeMetricsWithValueForDate(date) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> =
        dao.observeCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCategory(name: String) {
        viewModelScope.launch {
            val n = name.trim()
            if (n.isNotEmpty()) dao.insertCategory(CategoryEntity(name = n))
        }
    }

    fun deleteCategory(c: CategoryEntity) {
        viewModelScope.launch {
            dao.deleteCategory(c)
        }
    }

    fun setDate(iso: String) { _selectedDate.value = iso }

    fun addMetric(name: String, type: MetricType, unit: String = "", categoryId: Long? = null) {
        viewModelScope.launch {
            dao.insertMetric(
                MetricEntity(
                    name = name.trim(),
                    type = type,
                    unit = unit,
                    categoryId = categoryId
                )
            )
        }
    }

    fun deleteMetric(metric: MetricEntity) {
        viewModelScope.launch {
            dao.deleteMetric(metric)
        }
    }


    fun saveValue(metric: MetricEntity, number: Double? = null, text: String? = null, bool: Boolean? = null) {
        val date = _selectedDate.value
        viewModelScope.launch {
            val existing = dao.getValue(date, metric.id)
            val newValue = DailyValueEntity(
                id = existing?.id ?: 0,
                date = date,
                metricId = metric.id,
                numberValue = number,
                textValue = text,
                boolValue = bool
            )
            if (existing == null) dao.insertValue(newValue) else dao.updateValue(newValue)
        }
    }

    fun history(metricId: Long): Flow<List<DailyValueEntity>> = dao.observeHistory(metricId)

    // Bonus : seed de départ (optionnel)
    fun seedDefaultsIfEmpty() {
        viewModelScope.launch {
            // s’il n’y a aucune métrique, on en crée 3
            val oneShot = dao.observeActiveMetrics().first()
            if (oneShot.isNotEmpty()) return@launch

            dao.insertMetric(MetricEntity(name="Décisions exécutées", type=MetricType.NUMBER, unit="count", order=1))
            dao.insertMetric(MetricEntity(name="Dépenses inutiles", type=MetricType.MONEY, unit="€", order=2))
            dao.insertMetric(MetricEntity(name="Sport", type=MetricType.DURATION_MIN, unit="min", order=3))
        }
    }
}
