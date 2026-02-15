package com.example.logtrack_1_1.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.logtrack_1_1.data.entity.AggregationType
import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.data.entity.MetricEntity
import com.example.logtrack_1_1.di.LocalAppContainer
import com.example.logtrack_1_1.ui.components.SectionCard
import com.example.logtrack_1_1.ui.vm.TrackerViewModel
import com.example.logtrack_1_1.ui.vm.TrackerViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val container = LocalAppContainer.current
    val vm: TrackerViewModel = viewModel(factory = TrackerViewModelFactory(container.repository))

    val categories by vm.categories.collectAsState()
    val metrics by vm.metrics.collectAsState()

    // ---------- STATE ----------
    var showAddCategory by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    var showAddMetric by remember { mutableStateOf(false) }
    var editingMetric by remember { mutableStateOf<MetricEntity?>(null) }
    var metricToDelete by remember { mutableStateOf<MetricEntity?>(null) }

    // map id -> name (pour afficher le nom de catégorie dans la liste des metrics)
    val catNameById = remember(categories) { categories.associate { it.id to it.name } }
    fun categoryName(categoryId: Long?): String =
        if (categoryId == null) "Uncategorized" else (catNameById[categoryId] ?: "Unknown")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ----------------------------
            // Categories (ton composant existant)
            // ----------------------------
            ManageCategoriesSection(
                categories = categories,
                onAdd = { showAddCategory = true },
                onEdit = { c -> editingCategory = c },
                onDelete = { c -> categoryToDelete = c }
            )

            // ----------------------------
            // Metrics (on le fait ici direct, pas besoin de ton ManageMetricsSection)
            // ----------------------------
            SectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Metrics", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showAddMetric = true }) { Text("Add") }
                }

                Spacer(Modifier.height(8.dp))

                if (metrics.isEmpty()) {
                    Text("No metrics yet.")
                } else {
                    metrics.forEachIndexed { idx, m ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(m.name)
                                val sub = buildString {
                                    if (m.unit.isNotBlank()) append(m.unit).append(" • ")
                                    append(m.aggregation.name)
                                    append(" • ")
                                    append(categoryName(m.categoryId))
                                }
                                Text(sub, style = MaterialTheme.typography.bodySmall)
                            }

                            Row {
                                IconButton(onClick = { editingMetric = m }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit metric")
                                }
                                IconButton(onClick = { metricToDelete = m }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete metric")
                                }
                            }
                        }

                        if (idx != metrics.lastIndex) {
                            Spacer(Modifier.height(6.dp))
                            // Divider() si tu veux, mais pas obligatoire
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
            }

            // (Optionnel) bouton sample data
            SectionCard {
                Text("Dev tools", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { vm.addSampleData() }) {
                    Text("Add sample data")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // ----------------------------
    // ADD CATEGORY
    // ----------------------------
    if (showAddCategory) {
        AddCategoryDialog(
            initialName = "",
            initialColorArgb = null,
            onDismiss = { showAddCategory = false },
            onConfirm = { name, colorArgbInt ->
                // vm veut Long? -> conversion
                vm.addCategory(name = name, colorArgb = colorArgbInt.toLong())
                showAddCategory = false
            }
        )
    }

    // ----------------------------
    // EDIT CATEGORY
    // ----------------------------
    if (editingCategory != null) {
        val c = editingCategory!!

        // ton CategoryEntity a colorArgb en Long? (vu dans VM)
        val initialArgbInt: Int? = c.colorArgb?.toInt()

        AddCategoryDialog(
            initialName = c.name,
            initialColorArgb = initialArgbInt,
            onDismiss = { editingCategory = null },
            onConfirm = { name, colorArgbInt ->
                vm.updateCategory(
                    c.copy(
                        name = name,
                        colorArgb = colorArgbInt.toLong()
                    )
                )
                editingCategory = null
            }
        )
    }

    // ----------------------------
    // DELETE CATEGORY
    // ----------------------------
    if (categoryToDelete != null) {
        val c = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Delete category?") },
            text = { Text("Metrics inside will become Uncategorized.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCategory(c)
                    categoryToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // ----------------------------
    // ADD METRIC
    // ----------------------------
    if (showAddMetric) {
        AddMetricDialog(
            title = "Add metric",
            confirmText = "Add",
            categories = categories,
            initialName = "",
            initialUnit = "",
            initialAggregation = AggregationType.SUM,
            initialCategoryId = null,
            onDismiss = { showAddMetric = false },
            onConfirm = { name, unit, aggregation, categoryId ->
                vm.addMetric(
                    name = name,
                    unit = unit,
                    aggregation = aggregation,
                    categoryId = categoryId
                )
                showAddMetric = false
            }
        )
    }

    // ----------------------------
    // EDIT METRIC
    // ----------------------------
    if (editingMetric != null) {
        val m = editingMetric!!
        AddMetricDialog(
            title = "Edit metric",
            confirmText = "Save",
            categories = categories,
            initialName = m.name,
            initialUnit = m.unit,
            initialAggregation = m.aggregation,
            initialCategoryId = m.categoryId,
            onDismiss = { editingMetric = null },
            onConfirm = { name, unit, aggregation, categoryId ->
                vm.updateMetric(
                    m.copy(
                        name = name,
                        unit = unit,
                        aggregation = aggregation,
                        categoryId = categoryId
                    )
                )
                editingMetric = null
            }
        )
    }

    // ----------------------------
    // DELETE METRIC
    // ----------------------------
    if (metricToDelete != null) {
        val m = metricToDelete!!
        AlertDialog(
            onDismissRequest = { metricToDelete = null },
            title = { Text("Delete metric?") },
            text = { Text("This will remove it from your tracking list.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteMetric(m)
                    metricToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { metricToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
