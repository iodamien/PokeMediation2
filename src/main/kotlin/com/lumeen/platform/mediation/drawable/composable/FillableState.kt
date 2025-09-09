package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

class FillableState {

    private val state: SnapshotStateMap<String, Any?> = mutableStateMapOf()

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
}

val LocalFillableScope = staticCompositionLocalOf { FillableState() }