package com.example.logtrack_1_1.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.logtrack_1_1.di.LocalAppContainer
import com.example.logtrack_1_1.ui.vm.TrackerViewModel
import com.example.logtrack_1_1.ui.vm.TrackerViewModelFactory
import kotlin.math.abs

@Composable
fun TodayScreen(
    onOpenAddEvent: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: TrackerViewModel = viewModel(factory = TrackerViewModelFactory(container.repository))

    val categories by vm.categories.collectAsState()
    val metrics by vm.metrics.collectAsState()
    val todayValues by vm.todayValues.collectAsState()

    val valueByMetricId = remember(todayValues) {
        todayValues.associateBy({ it.metricId }, { it.value })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddEvent,
                containerColor = MaterialTheme.colorScheme.primary
            ) { Text("+") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Today", style = MaterialTheme.typography.headlineMedium)
            }

            if (metrics.isEmpty()) {
                item { Text("No metrics yet. Create some in Settings.") }
            } else {
                val catNameById = categories.associate { it.id to it.name }
                val grouped = metrics.groupBy { it.categoryId }

                val orderedKeys = run {
                    val catIdsOrdered = categories.map { it.id }
                    val keys = mutableListOf<Long?>()
                    keys.addAll(catIdsOrdered.filter { grouped.containsKey(it) })
                    if (grouped.containsKey(null)) keys.add(null)
                    keys
                }

                val cards = orderedKeys.mapNotNull { catId ->
                    val title =
                        if (catId == null) "Uncategorized" else (catNameById[catId] ?: "Category")

                    val visibleMetrics = grouped[catId].orEmpty().filter { m ->
                        val raw = valueByMetricId[m.id]
                        raw != null && raw != 0.0
                    }

                    if (visibleMetrics.isEmpty()) null else CategoryCardUi(title, visibleMetrics)
                }

                items(cards, key = { it.title }) { card ->
                    val accent = categoryAccentColor(card.title)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = tintedBackground(accent)
                        )
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .fillMaxHeight()
                                    .background(accent)
                            )

                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(card.title, style = MaterialTheme.typography.titleMedium)

                                card.metrics.forEach { m ->
                                    val raw = valueByMetricId[m.id]!!
                                    val unit = if (m.unit.isBlank()) "" else " ${m.unit}"
                                    val display = "${"%.2f".format(raw)}$unit"

                                    TodayMetricRow(
                                        metric = m,
                                        displayValue = display,
                                        onAddClick = { /* plus utilisé */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class CategoryCardUi(
    val title: String,
    val metrics: List<com.example.logtrack_1_1.data.entity.MetricEntity>
)

private fun categoryAccentColor(name: String): Color {
    val h = abs(name.hashCode())
    val r = 80 + (h % 140)
    val g = 80 + ((h / 3) % 140)
    val b = 80 + ((h / 7) % 140)
    return Color(r, g, b)
}

private fun tintedBackground(accent: Color): Color = accent.copy(alpha = 0.12f)