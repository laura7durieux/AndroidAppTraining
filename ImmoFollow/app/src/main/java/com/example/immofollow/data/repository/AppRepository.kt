package com.example.immofollow.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.immofollow.data.defaults.DefaultSpecs
import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.ScoreBand
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.data.model.SpecInputType
import com.example.immofollow.data.storage.AppState
import com.example.immofollow.data.storage.LocalStorage
import java.util.UUID

class AppRepository(
    private val localStorage: LocalStorage
) {
    val houses = mutableStateListOf<House>()
    val specs = mutableStateListOf<SpecDefinition>()

    fun load() {
        val loaded = localStorage.load()

        houses.clear()
        specs.clear()

        if (loaded == null) {
            specs.addAll(DefaultSpecs.all())
            save()
        } else {
            houses.addAll(loaded.houses)
            specs.addAll(loaded.specs.ifEmpty { DefaultSpecs.all() })
        }
    }

    fun save() {
        localStorage.save(
            AppState(
                houses = houses.toList(),
                specs = specs.toList()
            )
        )
    }

    fun addHouse(name: String, city: String): House {
        val house = House(
            id = UUID.randomUUID().toString(),
            name = name,
            city = city
        )
        houses.add(house)
        save()
        return house
    }

    fun updateSpec(updated: SpecDefinition) {
        val index = specs.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            specs[index] = updated
            save()
        }
    }

    fun addEmptySpec(): SpecDefinition {
        val spec = SpecDefinition(
            id = "spec_" + UUID.randomUUID().toString().replace("-", "").take(8),
            defaultName = "Nouvelle spec",
            category = "Divers",
            flexibility = "F1",
            inputType = SpecInputType.TEXT,
            weight = 1,
            bands = listOf(
                ScoreBand(
                    label = "Valeur par défaut",
                    score = 0
                )
            ),
            options = emptyList(),
            visitOnly = false
        )
        specs.add(spec)
        save()
        return spec
    }

    fun removeSpec(specId: String) {
        specs.removeAll { it.id == specId }
        houses.forEach { house ->
            house.values.remove(specId)
        }
        save()
    }
}