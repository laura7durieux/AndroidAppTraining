package com.example.logtrack_1_1.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.logtrack_1_1.ui.screens.InsightsScreen
import com.example.logtrack_1_1.ui.screens.settings.SettingsScreen
import com.example.logtrack_1_1.ui.screens.today.TodayScreen

@Composable
fun AppNav(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.Today.path
    ) {
        composable(Route.Today.path) { TodayScreen() }
        composable(Route.Insights.path) { InsightsScreen() }
        composable(Route.Settings.path) { SettingsScreen() }
    }
}