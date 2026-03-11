package com.example.immofollow.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.ui.component.SpecDefinitionEditor

@Composable
fun SettingsScreen(
    specs: List<SpecDefinition>,
    onAddSpec: () -> Unit,
    onUpdateSpec: (SpecDefinition) -> Unit,
    onDeleteSpec: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Paramètres de scoring",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ajout, suppression et modification des specs",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Button(onClick = onAddSpec) {
                Text("Ajouter une spec")
            }
        }

        items(
            items = specs,
            key = { spec -> spec.id }
        ) { spec ->
            SpecDefinitionEditor(
                spec = spec,
                onUpdate = onUpdateSpec,
                onDelete = { onDeleteSpec(spec.id) }
            )
        }
    }
}