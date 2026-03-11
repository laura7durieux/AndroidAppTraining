package com.example.immofollow.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.domain.ScoreEngine

@Composable
fun DashboardScreen(
    houses: List<House>,
    specs: List<SpecDefinition>,
    onOpenHouse: (String) -> Unit,
    onGoToList: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToCompare: () -> Unit,
    onCreateHouse: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val name = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }

    val validHouses = houses.filterNot { ScoreEngine.hasExclusion(it, specs) }
    val excludedCount = houses.count { ScoreEngine.hasExclusion(it, specs) }
    val bestScore = validHouses.maxOfOrNull { ScoreEngine.computeTotalScore(it, specs) } ?: 0.0

    val ranked = houses.sortedByDescending { house ->
        if (ScoreEngine.hasExclusion(house, specs)) -1.0
        else ScoreEngine.computeTotalScore(house, specs)
    }

    val top3 = ranked.take(3)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "ImmoFollow",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dashboard immobilier",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Biens",
                    value = houses.size.toString(),
                    subtitle = "maisons suivies"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Top score",
                    value = bestScore.toInt().toString(),
                    subtitle = "/100"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Exclusions",
                    value = excludedCount.toString(),
                    subtitle = "bloquantes"
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Créer une nouvelle maison",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nom du bien") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = city.value,
                        onValueChange = { city.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Ville / localisation") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val safeName = name.value.trim()
                                if (safeName.isNotEmpty()) {
                                    onCreateHouse(safeName, city.value.trim())
                                    name.value = ""
                                    city.value = ""
                                }
                            }
                        ) {
                            Text("Créer")
                        }

                        TextButton(onClick = onGoToList) {
                            Text("Voir tous les biens")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Top 3 des maisons",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (top3.isEmpty()) {
            item {
                EmptyCard("Aucune maison pour l’instant")
            }
        } else {
            items(top3, key = { it.id }) { house ->
                DashboardHouseCard(
                    house = house,
                    specs = specs,
                    onClick = { onOpenHouse(house.id) }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun DashboardHouseCard(
    house: House,
    specs: List<SpecDefinition>,
    onClick: () -> Unit
) {
    val excluded = ScoreEngine.hasExclusion(house, specs)
    val score = ScoreEngine.computeTotalScore(house, specs).coerceIn(0.0, 100.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Photo / annonce",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = house.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    ScoreBadge(
                        text = if (excluded) "Exclue" else "${score.toInt()}/100",
                        excluded = excluded
                    )
                }

                if (house.city.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = house.city,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { (score / 100.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp))
                )
            }
        }
    }
}

@Composable
private fun ScoreBadge(
    text: String,
    excluded: Boolean
) {
    val bg = if (excluded) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val fg = if (excluded) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyCard(text: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}