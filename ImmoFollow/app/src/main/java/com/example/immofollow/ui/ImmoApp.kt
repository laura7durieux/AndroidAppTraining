package com.example.immofollow.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.data.repository.AppRepository
import com.example.immofollow.data.storage.LocalStorage
import com.example.immofollow.ui.component.BottomNavBar
import com.example.immofollow.ui.navigation.NavDestination
import com.example.immofollow.ui.screen.CompareScreen
import com.example.immofollow.ui.screen.DashboardScreen
import com.example.immofollow.ui.screen.HouseDetailScreen
import com.example.immofollow.ui.screen.HouseListScreen
import com.example.immofollow.ui.screen.SettingsScreen

@Composable
fun ImmoApp() {
    val context = LocalContext.current
    val repository = remember {
        AppRepository(LocalStorage(context))
    }

    var currentTab by remember {
        mutableStateOf(NavDestination.DASHBOARD)
    }

    var selectedHouseId by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        repository.load()
    }

    val houses: List<House> = repository.houses
    val specs: List<SpecDefinition> = repository.specs

    Scaffold(
        bottomBar = {
            BottomNavBar(
                current = currentTab,
                onSelect = { destination ->
                    currentTab = destination
                    if (destination != NavDestination.HOUSES) {
                        selectedHouseId = null
                    }
                }
            )
        }
    ) { paddingValues ->
        val contentModifier = Modifier.padding(paddingValues)

        if (selectedHouseId != null) {
            val house = houses.firstOrNull { it.id == selectedHouseId }

            if (house != null) {
                HouseDetailScreen(
                    house = house,
                    specs = specs,
                    onBackToList = {
                        selectedHouseId = null
                        currentTab = NavDestination.HOUSES
                    },
                    modifier = contentModifier,
                    onDataChanged = {
                        repository.save()
                    }
                )
            }
        } else {
            when (currentTab) {
                NavDestination.DASHBOARD -> {
                    DashboardScreen(
                        houses = houses,
                        specs = specs,
                        onOpenHouse = { houseId ->
                            selectedHouseId = houseId
                        },
                        onGoToList = {
                            currentTab = NavDestination.HOUSES
                        },
                        onGoToSettings = {
                            currentTab = NavDestination.SETTINGS
                        },
                        onGoToCompare = {
                            currentTab = NavDestination.COMPARE
                        },
                        onCreateHouse = { name, city ->
                            val created = repository.addHouse(name, city)
                            selectedHouseId = created.id
                        },
                        modifier = contentModifier
                    )
                }

                NavDestination.HOUSES -> {
                    HouseListScreen(
                        houses = houses,
                        specs = specs,
                        onOpenHouse = { houseId ->
                            selectedHouseId = houseId
                        },
                        onCreateHouse = { name, city ->
                            val created = repository.addHouse(name, city)
                            selectedHouseId = created.id
                        },
                        modifier = contentModifier
                    )
                }

                NavDestination.SETTINGS -> {
                    SettingsScreen(
                        specs = specs,
                        onAddSpec = {
                            repository.addEmptySpec()
                        },
                        onUpdateSpec = { updatedSpec ->
                            repository.updateSpec(updatedSpec)
                        },
                        onDeleteSpec = { specId ->
                            repository.removeSpec(specId)
                        },
                        modifier = contentModifier
                    )
                }

                NavDestination.COMPARE -> {
                    CompareScreen(
                        houses = houses,
                        specs = specs,
                        onOpenHouse = { houseId ->
                            selectedHouseId = houseId
                        },
                        modifier = contentModifier
                    )
                }
            }
        }
    }
}