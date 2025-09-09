package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

class FillableState(
    private var initLang: String,
) {

    private val lang: MutableState<String> = mutableStateOf(initLang)
    private val state: SnapshotStateMap<String, SnapshotStateMap<String, Any?>> = mutableStateMapOf()
    val readLang: State<String> = lang

    init {
        prepareLang(initLang)
    }

    fun prepareLang(langCode: String) {
        if (!state.contains(langCode)) {
            state[langCode] = mutableStateMapOf()
        }
    }

    fun switchLang(langCode: String) {
        lang.value = langCode
        prepareLang(langCode)
    }

    fun setCurrentLang(langCode: String) {
        prepareLang(langCode)
        lang.value = langCode
    }

    fun getCurrentLang(): String = lang.value

    // Composable functions that return observable State
    @Composable
    fun getStringState(tag: String): State<String?> {
        val currentLang by lang
        return remember(tag, currentLang) {
            derivedStateOf {
                val value = state[currentLang]?.get(tag) as? String
                println("getStringState($tag) for lang '$currentLang' returning: $value")
                value
            }
        }
    }

    @Composable
    fun getIntState(tag: String): State<Int?> {
        val currentLang by lang
        return remember(tag, currentLang) {
            derivedStateOf { state[currentLang]?.get(tag) as? Int }
        }
    }

    @Composable
    fun getLongState(tag: String): State<Long?> {
        val currentLang by lang
        return remember(tag, currentLang) {
            derivedStateOf { state[currentLang]?.get(tag) as? Long }
        }
    }

    @Composable
    fun getFloatState(tag: String): State<Float?> {
        val currentLang by lang
        return remember(tag, currentLang) {
            derivedStateOf { state[currentLang]?.get(tag) as? Float }
        }
    }

    @Composable
    fun getDoubleState(tag: String): State<Double?> {
        val currentLang by lang
        return remember(tag, currentLang) {
            derivedStateOf { state[currentLang]?.get(tag) as? Double }
        }
    }

    @Composable
    fun getBooleanState(tag: String): State<Boolean?> {
        val currentLang by lang
        return remember(tag, currentLang) {
            derivedStateOf { state[currentLang]?.get(tag) as? Boolean }
        }
    }

    // Non-composable getters for immediate access
//    fun getStringState(tag: String): String? {
//        val currentLang = lang.value
//        return state[currentLang]?.get(tag) as? String
//    }

    fun getInt(tag: String): Int? {
        val currentLang = lang.value
        return state[currentLang]?.get(tag) as? Int
    }

    fun getLong(tag: String): Long? {
        val currentLang = lang.value
        return state[currentLang]?.get(tag) as? Long
    }

    fun getFloat(tag: String): Float? {
        val currentLang = lang.value
        return state[currentLang]?.get(tag) as? Float
    }

    fun getDouble(tag: String): Double? {
        val currentLang = lang.value
        return state[currentLang]?.get(tag) as? Double
    }

    fun getBoolean(tag: String): Boolean? {
        val currentLang = lang.value
        return state[currentLang]?.get(tag) as? Boolean
    }

    // Legacy getString method for backward compatibility
//    fun getString(tag: String): String? = getStringState(tag)

    fun updateState(tag: String, value: Any?) {
        val currentLang = lang.value
        prepareLang(currentLang)
        state[currentLang]?.set(tag, value)
        println("Updated state for lang '$currentLang', tag '$tag': $value")
        println("Current state: ${state[currentLang]}")
    }

    fun updateState(tag: String, value: Any?, langCode: String) {
        prepareLang(langCode)
        state[langCode]?.set(tag, value)
        println("Updated state for lang '$langCode', tag '$tag': $value")
    }

    fun removeState(tag: String) {
        val currentLang = lang.value
        state[currentLang]?.remove(tag)
    }

    fun removeState(tag: String, langCode: String) {
        state[langCode]?.remove(tag)
    }

    fun exportAsJson(): String {
        val jsonMap = state.mapValues { (_, langState) ->
            kotlinx.serialization.json.JsonObject(
                langState.mapValues { (_, value) ->
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
            )
        }

        return Json.encodeToString(
            kotlinx.serialization.json.JsonObject.serializer(),
            kotlinx.serialization.json.JsonObject(jsonMap)
        )
    }


    fun exportAsJson(langCode: String): String {
        val langState = state[langCode] ?: return "{}"
        val jsonMap = langState.mapValues { (_, value) ->
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

            // Check if this is multi-language JSON or single language
            val isMultiLang = jsonObject.values.any {
                it is kotlinx.serialization.json.JsonObject
            }

            if (isMultiLang) {
                // Multi-language format: {"en": {"key": "value"}, "fr": {"key": "valeur"}}
                state.clear()
                jsonObject.forEach { (langCode, langJson) ->
                    if (langJson is kotlinx.serialization.json.JsonObject) {
                        prepareLang(langCode)
                        langJson.forEach { (key, jsonElement) ->
                            val value = parseJsonElement(jsonElement)
                            state[langCode]?.set(key, value)
                        }
                    }
                }
            } else {
                // Single language format: {"key": "value"}
                val currentLang = lang.value
                prepareLang(currentLang)
                state[currentLang]?.clear()

                jsonObject.forEach { (key, jsonElement) ->
                    val value = parseJsonElement(jsonElement)
                    state[currentLang]?.set(key, value)
                }
            }

            println("State loaded from JSON: $state")
        } catch (e: Exception) {
            println("Error loading JSON: ${e.message}")
        }
    }

    fun loadJson(jsonString: String, langCode: String) {
        try {
            val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
            prepareLang(langCode)
            state[langCode]?.clear()

            jsonObject.forEach { (key, jsonElement) ->
                val value = parseJsonElement(jsonElement)
                state[langCode]?.set(key, value)
            }

            println("State loaded from JSON for lang '$langCode': ${state[langCode]}")
        } catch (e: Exception) {
            println("Error loading JSON for lang '$langCode': ${e.message}")
        }
    }

    private fun parseJsonElement(jsonElement: kotlinx.serialization.json.JsonElement): Any? {
        return when (jsonElement) {
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
    }
}

val LocalFillableScope = staticCompositionLocalOf { FillableState("fr") }