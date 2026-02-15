package com.example.logtrack_1_1.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.logtrack_1_1.data.entity.CategoryEntity
import com.example.logtrack_1_1.ui.components.SectionCard

@Composable
fun ManageCategoriesSection(
    categories: List<CategoryEntity>,
    onAdd: () -> Unit,
    onEdit: (CategoryEntity) -> Unit,
    onDelete: (CategoryEntity) -> Unit
) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Categories", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onAdd) { Text("Add") } // ✅ FIX ICI
        }

        Spacer(Modifier.height(8.dp))

        if (categories.isEmpty()) {
            Text("No categories yet.")
        } else {
            categories.forEachIndexed { idx, c ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(c.name)

                    Row {
                        IconButton(onClick = { onEdit(c) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit category")
                        }
                        IconButton(onClick = { onDelete(c) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete category")
                        }
                    }
                }

                if (idx != categories.lastIndex) {
                    Spacer(Modifier.height(6.dp))
                    Divider()
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}
