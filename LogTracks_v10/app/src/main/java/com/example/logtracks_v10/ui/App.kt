@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.logtracks_v10.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.logtracks_v10.data.CategoryEntity
import com.example.logtracks_v10.data.MetricType
import com.example.logtracks_v10.data.MetricWithTodayValue

// ----------------------------
// ROUTES
// ----------------------------

sealed class Route(val route: String) {
    object Today : Route("today")
    object Metrics : Route("metrics")
    object Categories : Route("categories")
    object History : Route("history/{metricId}") {
        fun create(metricId: Long) = "history/$metricId"
    }
}

// ----------------------------
// ROOT
// ----------------------------

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val vm: TrackerViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Route.Today.route
    ) {
        composable(Route.Today.route) {
            TodayScreen(
                vm = vm,
                onOpenMetrics = { navController.navigate(Route.Metrics.route) },
                onOpenCategories = { navController.navigate(Route.Categories.route) },
                onOpenHistory = { id -> navController.navigate(Route.History.create(id)) }
            )
        }

        composable(Route.Metrics.route) {
            MetricsScreen(vm = vm, onBack = { navController.popBackStack() })
        }

        composable(Route.Categories.route) {
            CategoriesScreen(vm = vm, onBack = { navController.popBackStack() })
        }

        composable("history/{metricId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("metricId")?.toLongOrNull() ?: 0L
            HistoryScreen(vm = vm, metricId = id, onBack = { navController.popBackStack() })
        }
    }
}

// ----------------------------
// TODAY
// ----------------------------

@Composable
fun TodayScreen(
    vm: TrackerViewModel,
    onOpenMetrics: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenHistory: (Long) -> Unit
) {
    val items by vm.todayItems.collectAsState()
    val categories by vm.categories.collectAsState()

    // Map id -> name
    val catNameById = remember(categories) { categories.associate { it.id to it.name } }

    // Group metrics by categoryId (null => Uncategorized)
    val grouped = remember(items, catNameById) {
        items.groupBy { it.metric.categoryId }
    }

    // Order keys: categories order first, then null at end (or start)
    val orderedKeys = remember(categories, grouped) {
        val catIdsOrdered = categories.map { it.id }
        val keys = mutableListOf<Long?>()
        keys.addAll(catIdsOrdered.filter { grouped.containsKey(it) })
        if (grouped.containsKey(null)) keys.add(null)
        keys
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today") },
                actions = {
                    TextButton(onClick = onOpenMetrics) { Text("Metrics") }
                    TextButton(onClick = onOpenCategories) { Text("Categories") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            orderedKeys.forEach { key ->
                val header = if (key == null) "Uncategorized" else (catNameById[key] ?: "Category")
                val rows = grouped[key].orEmpty()
                if (rows.isNotEmpty()) {

                    item {
                        Text(
                            text = header,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(rows, key = { it.metric.id }) { row ->
                        MetricCard(
                            row = row,
                            onSaveNumber = { vm.saveValue(row.metric, number = it) },
                            onSaveText = { vm.saveValue(row.metric, text = it) },
                            onSaveBool = { vm.saveValue(row.metric, bool = it) },
                            onOpenHistory = { onOpenHistory(row.metric.id) },
                            onDelete = { vm.deleteMetric(row.metric) }
                        )
                    }
                }
            }
        }
    }
}


// ----------------------------
// METRIC CARD
// ----------------------------

@Composable
fun MetricCard(
    row: MetricWithTodayValue,
    onSaveNumber: (Double?) -> Unit,
    onSaveText: (String) -> Unit,
    onSaveBool: (Boolean) -> Unit,
    onOpenHistory: () -> Unit,
    onDelete: () -> Unit
) {
    val metric = row.metric

    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(row.metric.name, style = MaterialTheme.typography.titleMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onOpenHistory) { Text("History") }
                    TextButton(onClick = onDelete) { Text("Delete") }
                }
            }

            when (metric.type) {
                MetricType.NUMBER,
                MetricType.MONEY,
                MetricType.DURATION_MIN -> {

                    var text by remember(row.todayNumber) {
                        mutableStateOf(row.todayNumber?.toString() ?: "")
                    }

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Value (${metric.unit})") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(onClick = {
                        val v = text.replace(",", ".").toDoubleOrNull()
                        onSaveNumber(v)
                    }) { Text("Save") }
                }

                MetricType.BOOLEAN -> {
                    val checked = row.todayBool ?: false
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { onSaveBool(it) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (checked) "Yes" else "No")
                    }
                }

                MetricType.TEXT -> {
                    var note by remember(row.todayText) { mutableStateOf(row.todayText ?: "") }

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(onClick = { onSaveText(note) }) { Text("Save") }
                }
            }
        }
    }
}

// ----------------------------
// METRICS (simple pour l’instant)
// ----------------------------

@Composable
fun MetricsScreen(
    vm: TrackerViewModel,
    onBack: () -> Unit
) {
    val categories by vm.categories.collectAsState()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(MetricType.NUMBER) }
    var unit by remember { mutableStateOf("") }

    // choix catégorie
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var catExpanded by remember { mutableStateOf(false) }

    // choix type
    var typeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metrics") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Metric name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- Category dropdown (simple)
            Box {
                val selectedName = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Uncategorized"
                OutlinedTextField(
                    value = selectedName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 8.dp)
                        .clickable { catExpanded = true }
                )
                DropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Uncategorized") },
                        onClick = { selectedCategoryId = null; catExpanded = false }
                    )
                    categories.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.name) },
                            onClick = { selectedCategoryId = c.id; catExpanded = false }
                        )
                    }
                }
            }

            // --- Type dropdown (simple)
            Box {
                OutlinedTextField(
                    value = type.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 8.dp)
                        .clickable { typeExpanded = true }
                )
                DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    MetricType.values().forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.name) },
                            onClick = { type = t; typeExpanded = false }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val n = name.trim()
                    if (n.isNotEmpty()) {
                        vm.addMetric(n, type, unit.trim(), selectedCategoryId)
                        name = ""
                        unit = ""
                        type = MetricType.NUMBER
                        selectedCategoryId = null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add metric")
            }
        }
    }
}


// ----------------------------
// HISTORY
// ----------------------------

@Composable
fun HistoryScreen(
    vm: TrackerViewModel,
    metricId: Long,
    onBack: () -> Unit
) {
    val history by vm.history(metricId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history, key = { it.id }) { v ->
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text(v.date, style = MaterialTheme.typography.titleSmall)
                        Text(
                            v.numberValue?.toString()
                                ?: v.textValue
                                ?: v.boolValue?.toString()
                                ?: ""
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------
// CATEGORIES
// ----------------------------

@Composable
fun CategoriesScreen(
    vm: TrackerViewModel,
    onBack: () -> Unit
) {
    val categories by vm.categories.collectAsState()
    var name by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("New category") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.addCategory(name)
                    name = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add") }

            Divider()

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.id }) { c ->
                    CategoryRow(
                        category = c,
                        onDelete = { vm.deleteCategory(c) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: CategoryEntity,
    onDelete: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category.name)
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}
