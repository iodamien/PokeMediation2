package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@SerialName("Padding")
@Serializable(with = ScaleSerializer::class)
data class ScaleProperty(
    val x: Float = 0f,
    val y: Float = 0f,
) {
    companion object {
        fun all(v: Float) = ScaleProperty(v, v)
    }
}

object ScaleSerializer : KSerializer<ScaleProperty> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Scale", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): ScaleProperty {
        if (decoder is YamlInput) {
            return when (val node = decoder.node) {
                is YamlScalar -> ScaleProperty.all(node.content.toFloatOrNull() ?: 1f)
                is YamlMap -> {
                    fun g(k: String) = (node.get<YamlScalar>(k)?.content)?.toFloatOrNull() ?: 1f
                    val value = node.get<YamlScalar>("v")?.content?.toFloatOrNull()
                    value?.let { ScaleProperty.all(it) } ?: ScaleProperty(
                        x = g("x"),
                        y = g("y"),
                    )
                }
                else -> throw SerializationException("Invalid scale: $node")
            }
        }

        // very generic fallback: try int, then object
        return try {
            ScaleProperty.all(decoder.decodeFloat())
        } catch (_: Exception) {
            var x = 0f; var y = 0f; var value: Float? = null;
            val c = decoder.beginStructure(buildClassSerialDescriptor("ScaleMap") {
                element<Int>("x", isOptional = true)
                element<Int>("y", isOptional = true)
                element<Int>("v", isOptional = true)
            })
            loop@ while (true) {
                when (val i = c.decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> x  = c.decodeFloatElement(descriptor, 0)
                    1 -> y  = c.decodeFloatElement(descriptor, 1)
                    2 -> value = c.decodeFloatElement(descriptor, 2)
                }
            }
            c.endStructure(descriptor)
            value?.let { ScaleProperty.all(it) } ?: ScaleProperty(x, y)
        }
    }

    override fun serialize(encoder: Encoder, value: ScaleProperty) {
        val d = buildClassSerialDescriptor("PaddingMap") {
            element<Int>("x", isOptional = true)
            element<Int>("y", isOptional = true)
        }
        val c = encoder.beginStructure(d)
        c.encodeFloatElement(d, 0, value.x)
        c.encodeFloatElement(d, 1, value.y)
        c.endStructure(d)
    }
}