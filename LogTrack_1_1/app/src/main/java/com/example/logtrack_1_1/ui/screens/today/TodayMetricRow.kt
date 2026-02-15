package com.example.logtrack_1_1.ui.screens.today

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.logtrack_1_1.data.entity.MetricEntity

@Composable
fun TodayMetricRow(
    metric: MetricEntity,
    displayValue: String,
    onAddClick: () -> Unit
) {
    // On garde la signature identique (pour ne rien casser),
    // mais on n'affiche plus de bouton "+".
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = metric.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ✅ bouton supprimé volontairement
        // (onAddClick est conservé pour compatibilité)
        Spacer(Modifier.width(0.dp))
    }
}
