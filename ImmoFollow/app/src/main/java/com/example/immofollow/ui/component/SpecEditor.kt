package com.example.immofollow.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.HouseSpecValue
import com.example.immofollow.data.model.ScoreBand
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.data.model.SpecInputType
import com.example.immofollow.domain.ScoreEngine

@Composable
fun SpecValueEditor(
    house: House,
    spec: SpecDefinition,
    onValueChanged: () -> Unit = {}
) {
    val score = ScoreEngine.computeSpecScore(house, spec)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spec.defaultName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${spec.category} • ${spec.flexibility} • coef ${spec.weight}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ScorePill(score = score)
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (spec.inputType) {
                SpecInputType.NUMBER -> NumberEditor(house, spec, onValueChanged)
                SpecInputType.BOOLEAN -> BooleanEditor(house, spec, onValueChanged)
                SpecInputType.CHOICE -> ChoiceEditor(house, spec, onValueChanged)
                SpecInputType.TEXT -> TextEditor(house, spec, onValueChanged)
            }
        }
    }
}

@Composable
fun SpecDefinitionEditor(
    spec: SpecDefinition,
    onUpdate: (SpecDefinition) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember(spec.defaultName) { mutableStateOf(spec.defaultName) }
    var category by remember(spec.category) { mutableStateOf(spec.category) }
    var flexibility by remember(spec.flexibility) { mutableStateOf(spec.flexibility) }
    var weightText by remember(spec.weight) { mutableStateOf(spec.weight.toString()) }
    var optionsText by remember(spec.options) { mutableStateOf(spec.options.joinToString(",")) }
    var selectedType by remember(spec.inputType) { mutableStateOf(spec.inputType) }
    var visitOnly by remember(spec.visitOnly) { mutableStateOf(spec.visitOnly) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Édition de spec",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    onUpdate(
                        spec.copy(defaultName = it)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nom") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                    onUpdate(
                        spec.copy(category = it)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Catégorie") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = flexibility,
                onValueChange = {
                    flexibility = it
                    onUpdate(
                        spec.copy(flexibility = it)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Flexibilité") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = weightText,
                onValueChange = { raw ->
                    weightText = raw
                    val parsed = raw.toIntOrNull()
                    if (parsed != null) {
                        onUpdate(
                            spec.copy(weight = parsed.coerceAtLeast(0))
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Coefficient") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Type",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpecInputType.entries.forEach { inputType ->
                    Button(
                        onClick = {
                            selectedType = inputType
                            onUpdate(
                                spec.copy(inputType = inputType)
                            )
                        }
                    ) {
                        Text(inputType.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedType == SpecInputType.CHOICE) {
                OutlinedTextField(
                    value = optionsText,
                    onValueChange = { raw ->
                        optionsText = raw
                        val options = raw
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }

                        onUpdate(
                            spec.copy(options = options)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Options (séparées par des virgules)") }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = visitOnly,
                    onCheckedChange = { checked ->
                        visitOnly = checked
                        onUpdate(
                            spec.copy(visitOnly = checked)
                        )
                    }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("À remplir surtout pendant la visite")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bandes actuelles : ${spec.bands.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            spec.bands.forEach { band ->
                Text(
                    text = "• ${band.label} → ${band.score}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Les critères détaillés des bandes seront édités dans l’étape suivante.",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Supprimer la spec")
            }
        }
    }
}

@Composable
private fun NumberEditor(
    house: House,
    spec: SpecDefinition,
    onValueChanged: () -> Unit
) {
    val currentValue =
        (house.values[spec.id] as? HouseSpecValue.NumberValue)?.value?.toString() ?: ""

    OutlinedTextField(
        value = currentValue,
        onValueChange = { raw ->
            val normalized = raw.replace(",", ".")
            if (normalized.isBlank()) {
                house.values.remove(spec.id)
                onValueChanged()
            } else {
                val parsed = normalized.toDoubleOrNull()
                if (parsed != null) {
                    house.values[spec.id] = HouseSpecValue.NumberValue(parsed)
                    onValueChanged()
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Valeur numérique") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
private fun BooleanEditor(
    house: House,
    spec: SpecDefinition,
    onValueChanged: () -> Unit
) {
    val currentValue =
        (house.values[spec.id] as? HouseSpecValue.BooleanValue)?.value ?: false

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = currentValue,
            onCheckedChange = { checked ->
                house.values[spec.id] = HouseSpecValue.BooleanValue(checked)
                onValueChanged()
            }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Oui / présent",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ChoiceEditor(
    house: House,
    spec: SpecDefinition,
    onValueChanged: () -> Unit
) {
    val currentValue =
        (house.values[spec.id] as? HouseSpecValue.ChoiceValue)?.value ?: ""

    val expanded = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = currentValue,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Valeur sélectionnée") }
    )

    Spacer(modifier = Modifier.height(8.dp))

    TextButton(onClick = { expanded.value = !expanded.value }) {
        Text(if (expanded.value) "Masquer les choix" else "Choisir une valeur")
    }

    if (expanded.value) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            spec.options.forEach { option ->
                Button(
                    onClick = {
                        house.values[spec.id] = HouseSpecValue.ChoiceValue(option)
                        expanded.value = false
                        onValueChanged()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (option == currentValue) "✓ $option" else option
                    )
                }
            }
        }
    }
}

@Composable
private fun TextEditor(
    house: House,
    spec: SpecDefinition,
    onValueChanged: () -> Unit
) {
    val currentValue =
        (house.values[spec.id] as? HouseSpecValue.TextValue)?.value ?: ""

    OutlinedTextField(
        value = currentValue,
        onValueChange = { value ->
            house.values[spec.id] = HouseSpecValue.TextValue(value)
            onValueChanged()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Texte libre") }
    )
}

@Composable
private fun ScorePill(score: Int) {
    val background = when {
        score < 0 -> MaterialTheme.colorScheme.errorContainer
        score >= 3 -> MaterialTheme.colorScheme.primaryContainer
        score == 2 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val text = if (score < 0) "Exclusion" else "$score / 3"

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}