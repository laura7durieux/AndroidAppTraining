package com.example.immofollow.data.model

sealed class HouseSpecValue {
    data class TextValue(val value: String) : HouseSpecValue()
    data class NumberValue(val value: Double) : HouseSpecValue()
    data class BooleanValue(val value: Boolean) : HouseSpecValue()
    data class ChoiceValue(val value: String) : HouseSpecValue()
}