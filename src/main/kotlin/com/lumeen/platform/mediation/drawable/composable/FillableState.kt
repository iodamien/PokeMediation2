package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

class FillableState {

    private val state: SnapshotStateMap<String, Any?> = mutableStateMapOf()

    // Return State objects that Compose can observe
    @Composable
    fun getStringState(tag: String): androidx.compose.runtime.State<String?> {
        return remember(tag) {
            derivedStateOf { state[tag] as? String }
        }
    }

    @Composable
    fun getIntState(tag: String): androidx.compose.runtime.State<Int?> {
        return remember(tag) {
            derivedStateOf { state[tag] as? Int }
        }
    }

    @Composable
    fun getLongState(tag: String): androidx.compose.runtime.State<Long?> {
        return remember(tag) {
            derivedStateOf { state[tag] as? Long }
        }
    }

    @Composable
    fun getFloatState(tag: String): androidx.compose.runtime.State<Float?> {
        return remember(tag) {
            derivedStateOf { state[tag] as? Float }
        }
    }

    @Composable
    fun getDoubleState(tag: String): androidx.compose.runtime.State<Double?> {
        return remember(tag) {
            derivedStateOf { state[tag] as? Double }
        }
    }

    @Composable
    fun getBooleanState(tag: String): androidx.compose.runtime.State<Boolean?> {
        return remember(tag) {
            derivedStateOf { state[tag] as? Boolean }
        }
    }

    fun updateState(tag: String, value: Any?) {
        state[tag] = value
        println(state)
    }

    fun getString(tag: String): String? {
        return state[tag] as? String
    }

    fun getInt(tag: String): Int? {
        return state[tag] as? Int
    }

    fun getLong(tag: String): Long? {
        return state[tag] as? Long
    }

    fun getFloat(tag: String): Float? {
        return state[tag] as? Float
    }

    fun getDouble(tag: String): Double? {
        return state[tag] as? Double
    }

    fun getBoolean(tag: String): Boolean? {
        return state[tag] as? Boolean
    }

    fun exportAsJson(): String {
        val jsonMap = state.mapValues { (_, value) ->
            when (value) {
                null -> JsonNull
                is String -> JsonPrimitive(value)
                is Int -> JsonPrimitive(value)
                is Long -> JsonPrimitive(value)
                is Float -> JsonPrimitive(value)
                is Double -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                else -> JsonPrimitive(value.toString())
            }
        }

        return Json.encodeToString(
            kotlinx.serialization.json.JsonObject.serializer(),
            kotlinx.serialization.json.JsonObject(jsonMap)
        )
    }

    fun loadJson(jsonString: String) {
        try {
            val jsonObject = Json.parseToJsonElement(jsonString).jsonObject

            // Clear existing state
            state.clear()

            // Load values from JSON
            jsonObject.forEach { (key, jsonElement) ->
                val value = when (jsonElement) {
                    is JsonNull -> null
                    is JsonPrimitive -> {
                        when {
                            jsonElement.isString -> jsonElement.content
                            jsonElement.content == "true" || jsonElement.content == "false" ->
                                jsonElement.content.toBoolean()

                            jsonElement.content.contains('.') -> {
                                // Try to parse as Double first, then Float
                                jsonElement.content.toDoubleOrNull() ?: jsonElement.content.toFloatOrNull()
                            }

                            else -> {
                                // Try to parse as Long first, then Int
                                jsonElement.content.toLongOrNull() ?: jsonElement.content.toIntOrNull()
                                ?: jsonElement.content
                            }
                        }
                    }

                    else -> jsonElement.toString()
                }
                state[key] = value
            }
            println("State loaded from JSON: $state")
        } catch (e: Exception) {
            println("Error loading JSON: ${e.message}")
        }
    }
}

val LocalFillableScope = staticCompositionLocalOf { FillableState() }