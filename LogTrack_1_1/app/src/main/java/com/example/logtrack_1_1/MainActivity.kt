package com.example.logtrack_1_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.logtrack_1_1.ui.nav.AppNav
import com.example.logtrack_1_1.ui.nav.Route
import com.example.logtrack_1_1.ui.theme.LogTrack_1_1Theme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.CompositionLocalProvider
import com.example.logtrack_1_1.di.DefaultAppContainer
import com.example.logtrack_1_1.di.LocalAppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogTrack_1_1Theme {
                AppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot() {
    val navController = rememberNavController()

    val appContainer = remember {
        DefaultAppContainer(navController.context.applicationContext)
    }

    CompositionLocalProvider(LocalAppContainer provides appContainer) {

        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        val tabs = remember {
            listOf(Route.Today, Route.Insights, Route.Settings)
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            when (currentRoute) {
                                Route.Today.path -> "Today"
                                Route.Insights.path -> "Insights"
                                Route.Settings.path -> "Settings"
                                else -> "LogTrack"
                            }
                        )
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.path

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.path) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = {
                                Text(
                                    when (tab) {
                                        Route.Today -> "Today"
                                        Route.Insights -> "Insights"
                                        Route.Settings -> "Settings"
                                    }
                                )
                            },
                            icon = {
                                when (tab) {
                                    Route.Today -> Icon(Icons.Default.Today, contentDescription = null)
                                    Route.Insights -> Icon(Icons.Default.BarChart, contentDescription = null)
                                    Route.Settings -> Icon(Icons.Default.Settings, contentDescription = null)
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AppNav(navController)
            }
        }
    }
}
