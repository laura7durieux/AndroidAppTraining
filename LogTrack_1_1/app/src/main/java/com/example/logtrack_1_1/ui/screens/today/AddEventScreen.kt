package com.example.logtrack_1_1.ui.screens.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.logtrack_1_1.data.entity.EventImpactEntity
import com.example.logtrack_1_1.di.LocalAppContainer
import com.example.logtrack_1_1.ui.vm.TrackerViewModel
import com.example.logtrack_1_1.ui.vm.TrackerViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    onBack: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: TrackerViewModel = viewModel(factory = TrackerViewModelFactory(container.repository))

    val categories by vm.categories.collectAsState()
    val metrics by vm.metrics.collectAsState()

    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var catExpanded by remember { mutableStateOf(false) }

    val catNameById = remember(categories) { categories.associate { it.id to it.name } }
    val categoryLabel =
        if (selectedCategoryId == null) "Uncategorized" else (catNameById[selectedCategoryId] ?: "Category")

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

    fun impactsOrNull(): List<EventImpactEntity>? {
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
        return impacts
    }

    val hasAtLeastOneEnabled = enabledMap.values.any { it }
    val hasValidImpacts = impactsOrNull()?.isNotEmpty() == true

    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Event") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    // ✅ new API (no deprecation)
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
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
            } else {
                metricsForCategory.forEach { m ->
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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onBack) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(
                    enabled = hasAtLeastOneEnabled && hasValidImpacts,
                    onClick = {
                        val impacts = impactsOrNull().orEmpty()
                        vm.addEventMulti(
                            title = title.ifBlank { null },
                            note = note.ifBlank { null },
                            impacts = impacts
                        )
                        onBack()
                    }
                ) { Text("Add") }
            }
        }
    }
}