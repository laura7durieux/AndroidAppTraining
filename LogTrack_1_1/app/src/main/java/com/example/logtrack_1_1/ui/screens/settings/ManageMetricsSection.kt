package com.example.logtrack_1_1.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.data.entity.MetricEntity
import com.example.logtrack_1_1.ui.components.SectionCard

@Composable
fun ManageMetricsSection(
    metrics: List<MetricEntity>,
    categories: List<CategoryEntity>,
    onAddClick: () -> Unit
) {
    val catNameById = categories.associate { it.id to it.name }

    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Metrics", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onAddClick) { Text("Add") }
        }

        Spacer(Modifier.height(8.dp))

        if (metrics.isEmpty()) {
            Text("No metrics yet.")
        } else {
            metrics.forEach { m ->
                val cat = m.categoryId?.let { catNameById[it] } ?: "Uncategorized"
                val unit = if (m.unit.isBlank()) "" else " (${m.unit})"
                Text("• ${m.name}$unit — ${m.aggregation} — $cat")
            }
        }
    }
}
