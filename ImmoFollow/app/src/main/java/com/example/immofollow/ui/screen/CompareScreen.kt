package com.example.immofollow.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.domain.ScoreEngine

@Composable
fun CompareScreen(
    houses: List<House>,
    specs: List<SpecDefinition>,
    onOpenHouse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIds = remember { mutableStateListOf<String>() }
    val selectedHouses = houses.filter { selectedIds.contains(it.id) }.take(5)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Comparaison",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sélectionne de 2 à 5 maisons",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(houses, key = { it.id }) { house ->
            val isSelected = selectedIds.contains(house.id)
            val score = ScoreEngine.computeTotalScore(house, specs).coerceIn(0.0, 100.0)
            val excluded = ScoreEngine.hasExclusion(house, specs)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelected) {
                            selectedIds.remove(house.id)
                        } else if (selectedIds.size < 5) {
                            selectedIds.add(house.id)
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = house.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (house.city.isNotBlank()) {
                        Text(
                            text = house.city,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = if (excluded) "Exclue" else "Score : ${"%.1f".format(score)} / 100",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isSelected) "Sélectionnée" else "Appuie pour comparer",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (selectedHouses.size >= 2) {
            item {
                Text(
                    text = "Comparaison côte à côte",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedHouses.forEach { house ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = house.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (house.city.isNotBlank()) {
                                    Text(
                                        text = house.city,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = if (ScoreEngine.hasExclusion(house, specs)) {
                                        "Exclue"
                                    } else {
                                        "${ScoreEngine.computeTotalScore(house, specs).toInt()}/100"
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { onOpenHouse(house.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ouvrir")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Comparaison par critère",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(specs, key = { it.id }) { spec ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = spec.defaultName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        selectedHouses.forEach { house ->
                            val score = ScoreEngine.computeSpecScore(house, spec)
                            Text(
                                text = "${house.name} : ${if (score < 0) "Exclusion" else "$score / 3"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}