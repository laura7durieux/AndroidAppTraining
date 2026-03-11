package com.example.immofollow.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.domain.ScoreEngine
import com.example.immofollow.ui.component.SpecValueEditor

@Composable
fun HouseDetailScreen(
    house: House,
    specs: List<SpecDefinition>,
    onBackToList: () -> Unit,
    modifier: Modifier = Modifier,
    onDataChanged: () -> Unit = {}
) {
    val excluded = ScoreEngine.hasExclusion(house, specs)
    val totalScore = ScoreEngine.computeTotalScore(house, specs).coerceIn(0.0, 100.0)
    val groupedSpecs = specs.groupBy { it.category }

    val strongSpecs = specs
        .map { it to ScoreEngine.computeSpecScore(house, it) }
        .filter { it.second >= 2 }
        .sortedByDescending { it.second }
        .take(3)

    val weakSpecs = specs
        .map { it to ScoreEngine.computeSpecScore(house, it) }
        .filter { it.second <= 0 }
        .sortedBy { it.second }
        .take(3)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Button(onClick = onBackToList) {
                Text("Retour à la liste")
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = house.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (house.city.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = house.city,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (excluded) "Maison exclue" else "Score global : ${"%.1f".format(totalScore)} / 100",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { (totalScore / 100.0).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Les valeurs que tu saisis ici sont les valeurs réelles du bien. L’application les transforme ensuite en score selon ta grille.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = "Points forts",
                    lines = if (strongSpecs.isEmpty()) {
                        listOf("Aucun point fort encore saisi")
                    } else {
                        strongSpecs.map { "${it.first.defaultName} • ${it.second}/3" }
                    }
                )

                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = "Points faibles",
                    lines = if (weakSpecs.isEmpty()) {
                        listOf("Aucun point faible détecté")
                    } else {
                        weakSpecs.map {
                            if (it.second < 0) "${it.first.defaultName} • exclusion"
                            else "${it.first.defaultName} • ${it.second}/3"
                        }
                    }
                )
            }
        }

        groupedSpecs.forEach { (category, specsInCategory) ->
            item {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(specsInCategory, key = { it.id }) { spec ->
                SpecValueEditor(
                    house = house,
                    spec = spec,
                    onValueChanged = onDataChanged
                )
            }
        }
    }
}

@Composable
private fun InsightCard(
    modifier: Modifier = Modifier,
    title: String,
    lines: List<String>
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            lines.forEach { line ->
                Text(
                    text = "• $line",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}