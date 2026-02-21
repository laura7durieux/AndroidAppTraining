package com.example.logtrack_1_1.ui.screens.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.data.entity.EventImpactEntity
import com.example.logtrack_1_1.data.entity.MetricEntity
import androidx.compose.foundation.layout.WindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventMultiSheet(
    categories: List<CategoryEntity>,
    metrics: List<MetricEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String?, note: String?, impacts: List<EventImpactEntity>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var catExpanded by remember { mutableStateOf(false) }

    val catNameById = remember(categories) { categories.associate { it.id to it.name } }
    val categoryLabel = if (selectedCategoryId == null) "Uncategorized"
    else catNameById[selectedCategoryId] ?: "Category"

    val metricsForCategory = remember(metrics, selectedCategoryId) {
        metrics.filter { it.categoryId == selectedCategoryId }
    }

    val enabledMap = remember(selectedCategoryId, metricsForCategory) { mutableStateMapOf<Long, Boolean>() }
    val valueMap = remember(selectedCategoryId, metricsForCategory) { mutableStateMapOf<Long, String>() }

    LaunchedEffect(metricsForCategory) {
        metricsForCategory.forEach { m ->
            if (enabledMap[m.id] == null) enabledMap[m.id] = false
            if (valueMap[m.id] == null) valueMap[m.id] = "1"
        }
    }

    val hasAtLeastOneImpact = enabledMap.values.any { it }
    val listState = rememberLazyListState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        // ✅ sheet "statique" : ne suit pas le clavier (IME)
        // windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(16.dp)
                .navigationBarsPadding(), // ✅ protège le footer de la navbar
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Event", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = catExpanded,
                onExpandedChange = { catExpanded = !catExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = categoryLabel,
                    onValueChange = {},
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = catExpanded,
                    onDismissRequest = { catExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Uncategorized") },
                        onClick = {
                            selectedCategoryId = null
                            catExpanded = false
                        }
                    )
                    categories.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.name) },
                            onClick = {
                                selectedCategoryId = c.id
                                catExpanded = false
                            }
                        )
                    }
                }
            }

            Text("Impacts", style = MaterialTheme.typography.titleMedium)

            if (metricsForCategory.isEmpty()) {
                Text("No metrics in this category.")
                Spacer(Modifier.weight(1f))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    // ✅ laisse de la place pour le footer (et le clavier overlay)
                    contentPadding = PaddingValues(bottom = 140.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(metricsForCategory, key = { it.id }) { m ->
                        val enabled = enabledMap[m.id] == true
                        val valueText = valueMap[m.id] ?: "1"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = enabled,
                                onCheckedChange = { enabledMap[m.id] = it }
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(m.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "${m.aggregation}${if (m.unit.isBlank()) "" else " (${m.unit})"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            OutlinedTextField(
                                value = valueText,
                                onValueChange = { valueMap[m.id] = it },
                                label = { Text("Value") },
                                singleLine = true,
                                enabled = enabled,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(
                    enabled = hasAtLeastOneImpact,
                    onClick = {
                        val impacts = metricsForCategory.mapNotNull { m ->
                            if (enabledMap[m.id] != true) return@mapNotNull null
                            val raw = (valueMap[m.id] ?: "")
                                .trim()
                                .replace(',', '.')
                                .toDoubleOrNull() ?: return@mapNotNull null

                            EventImpactEntity(
                                eventId = 0,
                                metricId = m.id,
                                value = raw
                            )
                        }
                        onConfirm(title, note, impacts)
                    }
                ) { Text("Add") }
            }
        }
    }
}
