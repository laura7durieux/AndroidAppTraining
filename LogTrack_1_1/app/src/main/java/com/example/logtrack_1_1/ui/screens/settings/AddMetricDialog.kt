package com.example.logtrack_1_1.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.logtrack_1_1.data.entity.AggregationType
import com.example.logtrack_1_1.data.entity.CategoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMetricDialog(
    title: String,
    confirmText: String,
    categories: List<CategoryEntity>,
    initialName: String,
    initialUnit: String,
    initialAggregation: AggregationType,
    initialCategoryId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, unit: String, aggregation: AggregationType, categoryId: Long?) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var unit by remember(initialUnit) { mutableStateOf(initialUnit) }
    var aggregation by remember(initialAggregation) { mutableStateOf(initialAggregation) }

    // null = Uncategorized
    var selectedCategoryId by remember(initialCategoryId) { mutableStateOf<Long?>(initialCategoryId) }
    var catExpanded by remember { mutableStateOf(false) }

    var aggExpanded by remember { mutableStateOf(false) }

    val categoryLabel = remember(categories, selectedCategoryId) {
        if (selectedCategoryId == null) "Uncategorized"
        else categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Uncategorized"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (optional)") },
                    singleLine = true
                )

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
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
                                    selectedCategoryId = c.id // ✅ Long? correct
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                // Aggregation dropdown
                ExposedDropdownMenuBox(
                    expanded = aggExpanded,
                    onExpandedChange = { aggExpanded = !aggExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = aggregation.name,
                        onValueChange = {},
                        label = { Text("Aggregation") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = aggExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = aggExpanded,
                        onDismissRequest = { aggExpanded = false }
                    ) {
                        AggregationType.entries.forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a.name) },
                                onClick = {
                                    aggregation = a
                                    aggExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.trim().isNotEmpty(),
                onClick = {
                    onConfirm(
                        name.trim(),
                        unit.trim(),
                        aggregation,
                        selectedCategoryId
                    )
                }
            ) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
