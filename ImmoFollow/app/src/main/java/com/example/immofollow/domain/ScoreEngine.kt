package com.example.immofollow.domain

import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.HouseSpecValue
import com.example.immofollow.data.model.ScoreBand
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.data.model.SpecInputType

object ScoreEngine {

    fun computeSpecScore(house: House, spec: SpecDefinition): Int {
        val value = house.values[spec.id] ?: return 0

        return when (spec.inputType) {
            SpecInputType.NUMBER -> {
                val numericValue = (value as? HouseSpecValue.NumberValue)?.value ?: return 0
                findNumericScore(numericValue, spec.bands)
            }

            SpecInputType.BOOLEAN -> {
                val boolValue = (value as? HouseSpecValue.BooleanValue)?.value ?: return 0
                spec.bands.firstOrNull { it.booleanValue == boolValue }?.score ?: 0
            }

            SpecInputType.CHOICE -> {
                val textValue = (value as? HouseSpecValue.ChoiceValue)?.value ?: return 0
                findChoiceScore(textValue, spec.bands)
            }

            SpecInputType.TEXT -> {
                val textValue = (value as? HouseSpecValue.TextValue)?.value ?: return 0
                findChoiceScore(textValue, spec.bands)
            }
        }
    }

    fun computeTotalScore(house: House, specs: List<SpecDefinition>): Double {
        val maxScore = specs.sumOf { it.weight * 3.0 }
        if (maxScore == 0.0) return 0.0

        val weighted = specs.sumOf { spec ->
            val score = computeSpecScore(house, spec).coerceAtLeast(0)
            score * spec.weight.toDouble()
        }

        return (weighted / maxScore) * 100.0
    }

    fun hasExclusion(house: House, specs: List<SpecDefinition>): Boolean {
        return specs.any { computeSpecScore(house, it) < 0 }
    }

    private fun findNumericScore(value: Double, bands: List<ScoreBand>): Int {
        for (band in bands) {
            val minOk = band.minInclusive?.let { value >= it } ?: true
            val maxOk = band.maxInclusive?.let { value <= it } ?: true
            if (minOk && maxOk) return band.score
        }
        return 0
    }

    private fun findChoiceScore(value: String, bands: List<ScoreBand>): Int {
        return bands.firstOrNull { band ->
            band.acceptedTexts.any { it.equals(value, ignoreCase = true) }
        }?.score ?: 0
    }
}