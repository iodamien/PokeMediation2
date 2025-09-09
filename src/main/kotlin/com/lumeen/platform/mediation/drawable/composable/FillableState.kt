package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*

@kotlinx.serialization.Serializable
data class ImageExport(
    val path: String,
    val base64: String? = null,
    val mimeType: String? = null
) {
    companion object {
        fun fromFile(file: File): ImageExport {
            val base64 = encodeFileToBase64(file)
            val mimeType = getMimeType(file)
            return ImageExport(
                path = file.absolutePath,
                base64 = base64,
                mimeType = mimeType
            )
        }

        private fun encodeFileToBase64(file: File): String? {
            return try {
                val fileBytes = Files.readAllBytes(file.toPath())
                Base64.getEncoder().encodeToString(fileBytes)
            } catch (e: Exception) {
                println("Error encoding file to base64: ${e.message}")
                null
            }
        }

        private fun getMimeType(file: File): String? {
            return when (file.extension.lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "webp" -> "image/webp"
                "bmp" -> "image/bmp"
                else -> "application/octet-stream"
            }
        }

        fun decodeBase64ToFile(base64: String, outputPath: String, mimeType: String? = null): File? {
            return try {
                val fileBytes = Base64.getDecoder().decode(base64)
                val file = File(outputPath)

                // Create parent directories if they don't exist
                file.parentFile?.mkdirs()

                FileOutputStream(file).use { fos ->
                    fos.write(fileBytes)
                }
                file
            } catch (e: Exception) {
                println("Error decoding base64 to file: ${e.message}")
                null
            }
        }
    }

    fun saveToFile(outputPath: String): File? {
        return if (base64 != null) {
            decodeBase64ToFile(base64, outputPath, mimeType)
        } else if (File(path).exists()) {
            // Copy existing file
            try {
                val sourceFile = File(path)
                val targetFile = File(outputPath)
                targetFile.parentFile?.mkdirs()
                sourceFile.copyTo(targetFile, overwrite = true)
                targetFile
            } catch (e: Exception) {
                println("Error copying file: ${e.message}")
                null
            }
        } else {
            null
        }
    }
}

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
    fun getImageExportState(tag: String): State<ImageExport?> {
        val currentLang by lang
        return remember(tag, currentLang) {
            derivedStateOf {
                val value = state[currentLang]?.get(tag) as? ImageExport
                println("getImageExportState($tag) for lang '$currentLang' returning: $value")
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
    fun getImageExport(tag: String): ImageExport? {
        val currentLang = lang.value
        return state[currentLang]?.get(tag) as? ImageExport
    }

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

    // Convenience method to update image from file path
    fun updateImageFromPath(tag: String, filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            val imageExport = ImageExport.fromFile(file)
            updateState(tag, imageExport)
        }
    }

    fun updateImageFromPath(tag: String, filePath: String, langCode: String) {
        val file = File(filePath)
        if (file.exists()) {
            val imageExport = ImageExport.fromFile(file)
            updateState(tag, imageExport, langCode)
        }
    }

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
                        is ImageExport -> {
                            // Serialize ImageExport as JsonObject
                            val imageJson = buildJsonObject {
                                put("path", value.path)
                                value.base64?.let { put("base64", it) }
                                value.mimeType?.let { put("mimeType", it) }
                                put("type", "ImageExport")
                            }
                            imageJson
                        }
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
                is ImageExport -> {
                    // Serialize ImageExport as JsonObject
                    val imageJson = buildJsonObject {
                        put("path", value.path)
                        value.base64?.let { put("base64", it) }
                        value.mimeType?.let { put("mimeType", it) }
                        put("type", "ImageExport")
                    }
                    imageJson
                }
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
            is JsonObject -> {
                // Check if this is an ImageExport object
                if (jsonElement["type"]?.jsonPrimitive?.content == "ImageExport") {
                    val path = jsonElement["path"]?.jsonPrimitive?.content ?: ""
                    val base64 = jsonElement["base64"]?.jsonPrimitive?.content
                    val mimeType = jsonElement["mimeType"]?.jsonPrimitive?.content
                    ImageExport(path = path, base64 = base64, mimeType = mimeType)
                } else {
                    jsonElement.toString()
                }
            }
            else -> jsonElement.toString()
        }
    }

    // Utility method to restore images from base64 to files
    fun restoreImagesFromJson(outputDirectory: String = "restored_images"): Map<String, String> {
        val restoredPaths = mutableMapOf<String, String>()

        state.values.forEach { langState ->
            langState.forEach { (tag, value) ->
                if (value is ImageExport && value.base64 != null) {
                    val originalFile = File(value.path)
                    val fileName = originalFile.name.ifEmpty { "$tag.${getExtensionFromMimeType(value.mimeType)}" }
                    val outputPath = File(outputDirectory, fileName).absolutePath

                    val restoredFile = value.saveToFile(outputPath)
                    if (restoredFile != null) {
                        restoredPaths[tag] = restoredFile.absolutePath
                        println("Restored image for tag '$tag' to: ${restoredFile.absolutePath}")
                    }
                }
            }
        }

        return restoredPaths
    }

    private fun getExtensionFromMimeType(mimeType: String?): String {
        return when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            else -> "jpg"
        }
    }
}

val LocalFillableScope = staticCompositionLocalOf { FillableState("fr") }