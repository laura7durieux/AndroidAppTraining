package com.example.logtrack_1_1.ui.screens.today

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.logtrack_1_1.data.entity.MetricEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventSheet(
    metric: MetricEntity,
    onDismiss: () -> Unit,
    onConfirm: (value: Double, note: String?) -> Unit
) {
    var valueText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val value = valueText.replace(',', '.').toDoubleOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        // windowInsets = WindowInsets(0) // ✅ pas de réaction au clavier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add event — ${metric.name}", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = valueText,
                onValueChange = { valueText = it },
                label = { Text("Value") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(
                    enabled = value != null,
                    onClick = { onConfirm(value!!, note) }
                ) { Text("Add") }
            }
        }
    }
}
