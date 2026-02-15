package com.example.logtrack_1_1.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun AddCategoryDialog(
    initialName: String = "",
    initialColorArgb: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorArgb: Int) -> Unit
) {
    val isEdit = initialName.isNotBlank()

    val palette = listOf(
        Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF5C6BC0), Color(0xFF29B6F6),
        Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFFFFCA28), Color(0xFFFFA726),
        Color(0xFF8D6E63), Color(0xFF78909C)
    )

    // ✅ pré-remplissage
    var name by remember(initialName) { mutableStateOf(TextFieldValue(initialName)) }

    val startColor = remember(initialColorArgb) {
        initialColorArgb?.let { argb ->
            palette.minByOrNull { c -> kotlin.math.abs(c.toArgb() - argb) }
        } ?: palette.first()
    }
    var selectedColor by remember(initialColorArgb) { mutableStateOf(startColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit category" else "Add category") },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )

                Text("Color", style = MaterialTheme.typography.labelLarge)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    palette.forEach { c ->
                        TextButton(
                            onClick = { selectedColor = c },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (selectedColor == c) 28.dp else 24.dp)
                                    .clip(CircleShape)
                                    .background(c)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))
                Row {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Selected", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.text.trim().isNotEmpty(),
                onClick = {
                    onConfirm(
                        name.text.trim(),
                        selectedColor.toArgb() // ✅ Int, pas Long
                    )
                }
            ) { Text(if (isEdit) "Save" else "Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
