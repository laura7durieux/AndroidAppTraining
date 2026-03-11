package com.example.immofollow.ui.component

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.immofollow.ui.navigation.NavDestination

@Composable
fun BottomNavBar(
    current: NavDestination,
    onSelect: (NavDestination) -> Unit
) {
    NavigationBar {
        NavDestination.entries.forEach { destination: NavDestination ->
            NavigationBarItem(
                selected = current == destination,
                onClick = { onSelect(destination) },
                icon = { },
                label = {
                    Text(destination.label)
                }
            )
        }
    }
}