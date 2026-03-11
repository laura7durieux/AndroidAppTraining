package com.example.immofollow.data.storage

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.example.immofollow.data.model.House
import com.example.immofollow.data.model.HouseSpecValue
import com.example.immofollow.data.model.ScoreBand
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.data.model.SpecInputType
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class LocalStorage(
    private val context: Context
) {
    private val fileName = "immofollow_state.json"

    fun save(state: AppState) {
        val root = JSONObject()
        root.put("houses", housesToJson(state.houses))
        root.put("specs", specsToJson(state.specs))

        val file = File(context.filesDir, fileName)
        file.writeText(root.toString())
    }

    fun load(): AppState? {
        return try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return null

            val content = file.readText()
            if (content.isBlank()) return null

            val root = JSONObject(content)

            val housesJson = root.optJSONArray("houses") ?: JSONArray()
            val specsJson = root.optJSONArray("specs") ?: JSONArray()

            AppState(
                houses = jsonToHouses(housesJson),
                specs = jsonToSpecs(specsJson)
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun housesToJson(houses: List<House>): JSONArray {
        val array = JSONArray()

        houses.forEach { house ->
            val obj = JSONObject()
            obj.put("id", house.id)
            obj.put("name", house.name)
            obj.put("city", house.city)
            obj.put("values", houseValuesToJson(house))
            array.put(obj)
        }

        return array
    }

    private fun houseValuesToJson(house: House): JSONObject {
        val obj = JSONObject()

        house.values.forEach { (key, value) ->
            val valueObj = JSONObject()

            when (value) {
                is HouseSpecValue.TextValue -> {
                    valueObj.put("type", "text")
                    valueObj.put("value", value.value)
                }
                is HouseSpecValue.NumberValue -> {
                    valueObj.put("type", "number")
                    valueObj.put("value", value.value)
                }
                is HouseSpecValue.BooleanValue -> {
                    valueObj.put("type", "boolean")
                    valueObj.put("value", value.value)
                }
                is HouseSpecValue.ChoiceValue -> {
                    valueObj.put("type", "choice")
                    valueObj.put("value", value.value)
                }
            }

            obj.put(key, valueObj)
        }

        return obj
    }

    private fun jsonToHouses(array: JSONArray): List<House> {
        val result = mutableListOf<House>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            val house = House(
                id = obj.getString("id"),
                name = obj.getString("name"),
                city = obj.optString("city", "")
            )

            val valuesObj = obj.optJSONObject("values") ?: JSONObject()
            val keys = valuesObj.keys()

            while (keys.hasNext()) {
                val key = keys.next()
                val valueObj = valuesObj.getJSONObject(key)
                val type = valueObj.getString("type")

                val parsedValue = when (type) {
                    "text" -> HouseSpecValue.TextValue(valueObj.optString("value", ""))
                    "number" -> HouseSpecValue.NumberValue(valueObj.optDouble("value", 0.0))
                    "boolean" -> HouseSpecValue.BooleanValue(valueObj.optBoolean("value", false))
                    "choice" -> HouseSpecValue.ChoiceValue(valueObj.optString("value", ""))
                    else -> null
                }

                if (parsedValue != null) {
                    house.values[key] = parsedValue
                }
            }

            result.add(house)
        }

        return result
    }

    private fun specsToJson(specs: List<SpecDefinition>): JSONArray {
        val array = JSONArray()

        specs.forEach { spec ->
            val obj = JSONObject()
            obj.put("id", spec.id)
            obj.put("defaultName", spec.defaultName)
            obj.put("category", spec.category)
            obj.put("flexibility", spec.flexibility)
            obj.put("inputType", spec.inputType.name)
            obj.put("weight", spec.weight)
            obj.put("visitOnly", spec.visitOnly)

            val optionsArray = JSONArray()
            spec.options.forEach { optionsArray.put(it) }
            obj.put("options", optionsArray)

            val bandsArray = JSONArray()
            spec.bands.forEach { band ->
                val bandObj = JSONObject()
                bandObj.put("label", band.label)
                bandObj.put("score", band.score)
                if (band.minInclusive != null) bandObj.put("minInclusive", band.minInclusive)
                if (band.maxInclusive != null) bandObj.put("maxInclusive", band.maxInclusive)

                val acceptedTextsArray = JSONArray()
                band.acceptedTexts.forEach { acceptedTextsArray.put(it) }
                bandObj.put("acceptedTexts", acceptedTextsArray)

                if (band.booleanValue != null) bandObj.put("booleanValue", band.booleanValue)

                bandsArray.put(bandObj)
            }
            obj.put("bands", bandsArray)

            array.put(obj)
        }

        return array
    }

    private fun jsonToSpecs(array: JSONArray): List<SpecDefinition> {
        val result = mutableListOf<SpecDefinition>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            val options = mutableListOf<String>()
            val optionsArray = obj.optJSONArray("options") ?: JSONArray()
            for (j in 0 until optionsArray.length()) {
                options.add(optionsArray.getString(j))
            }

            val bands = mutableListOf<ScoreBand>()
            val bandsArray = obj.optJSONArray("bands") ?: JSONArray()
            for (j in 0 until bandsArray.length()) {
                val bandObj = bandsArray.getJSONObject(j)

                val acceptedTexts = mutableListOf<String>()
                val acceptedTextsArray = bandObj.optJSONArray("acceptedTexts") ?: JSONArray()
                for (k in 0 until acceptedTextsArray.length()) {
                    acceptedTexts.add(acceptedTextsArray.getString(k))
                }

                bands.add(
                    ScoreBand(
                        label = bandObj.getString("label"),
                        score = bandObj.getInt("score"),
                        minInclusive = if (bandObj.has("minInclusive")) bandObj.getDouble("minInclusive") else null,
                        maxInclusive = if (bandObj.has("maxInclusive")) bandObj.getDouble("maxInclusive") else null,
                        acceptedTexts = acceptedTexts,
                        booleanValue = if (bandObj.has("booleanValue")) bandObj.getBoolean("booleanValue") else null
                    )
                )
            }

            result.add(
                SpecDefinition(
                    id = obj.getString("id"),
                    defaultName = obj.getString("defaultName"),
                    category = obj.getString("category"),
                    flexibility = obj.getString("flexibility"),
                    inputType = SpecInputType.valueOf(obj.getString("inputType")),
                    weight = obj.getInt("weight"),
                    bands = bands,
                    options = options,
                    visitOnly = obj.optBoolean("visitOnly", false)
                )
            )
        }

        return result
    }
}