package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex

import androidx.compose.ui.graphics.Color
import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.DpProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.DpPropertySerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.text.toFloatOrNull
import kotlin.text.toIntOrNull

@Serializable(with = SizeSerializer::class)
@SerialName("Size")
data class SizeProperty(
    val width: DpProperty = DpProperty.Unspecified,
    val height: DpProperty = DpProperty.Unspecified,
) {

    companion object {
        fun from(size: DpProperty) = SizeProperty(
            width = size,
            height = size,
        )
    }
}
object SizeSerializer: KSerializer<SizeProperty> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("Size", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: SizeProperty) {
        val d = buildClassSerialDescriptor("SizeMap") {
            element<String>("w", isOptional = true)
            element<String>("h", isOptional = true)
        }
        val c = encoder.beginStructure(d)
        c.encodeStringElement(d, 0, value.width.toString())
        c.encodeStringElement(d, 1, value.height.toString())
        c.endStructure(d)
    }

    override fun deserialize(decoder: Decoder): SizeProperty {
        if (decoder is YamlInput) {
            return when (val node = decoder.node) {
                is YamlScalar -> {
                    // Parse scalar as DpProperty and apply to both width and height
                    val dpValue = parseDpProperty(node.content)
                    SizeProperty.from(dpValue)
                }
                is YamlMap -> {
                    fun getDpProperty(key: String): DpProperty {
                        val content = node.get<YamlScalar>(key)?.content
                        return if (content != null) {
                            parseDpProperty(content)
                        } else {
                            DpProperty.Unspecified
                        }
                    }

                    // Check for "v" (value for both dimensions)
                    val allValue = node.get<YamlScalar>("v")?.content
                    if (allValue != null) {
                        SizeProperty.from(parseDpProperty(allValue))
                    } else {
                        SizeProperty(
                            width = getDpProperty("w"),
                            height = getDpProperty("h"),
                        )
                    }
                }
                else -> throw SerializationException("Invalid size: $node")
            }
        }

        // Generic fallback for non-YAML decoders
        return try {
            // Try to decode as a single string first
            val singleValue = decoder.decodeString()
            SizeProperty.from(parseDpProperty(singleValue))
        } catch (_: Exception) {
            // Try to decode as a structured object
            var width = DpProperty.Default
            var height = DpProperty.Default
            var value: DpProperty? = null

            val c = decoder.beginStructure(buildClassSerialDescriptor("SizeMap") {
                element<String>("w", isOptional = true)
                element<String>("h", isOptional = true)
                element<String>("v", isOptional = true)
            })

            loop@ while (true) {
                when (val i = c.decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> width = parseDpProperty(c.decodeStringElement(descriptor, 0))
                    1 -> height = parseDpProperty(c.decodeStringElement(descriptor, 1))
                    2 -> value = parseDpProperty(c.decodeStringElement(descriptor, 2))
                }
            }
            c.endStructure(descriptor)

            value?.let { SizeProperty.from(it) } ?: SizeProperty(width, height)
        }
    }

    private fun parseDpProperty(content: String): DpProperty {
        val dpSerializer = DpPropertySerializer()
        return dpSerializer.parseString(content)
    }
}