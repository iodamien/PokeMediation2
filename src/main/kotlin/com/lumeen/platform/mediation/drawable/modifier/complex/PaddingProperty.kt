package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.DpProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.DpPropertySerializer
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@SerialName("Padding")
@Serializable(with = PaddingSerializer::class)
data class PaddingProperty(
    val left: DpProperty = DpProperty.Default,
    val top: DpProperty = DpProperty.Default,
    val right: DpProperty = DpProperty.Default,
    val bottom: DpProperty = DpProperty.Default,
) {
    companion object {
        fun all(v: DpProperty) = PaddingProperty(v, v, v, v)
    }
}

object PaddingSerializer : KSerializer<PaddingProperty> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Padding", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): PaddingProperty {
        if (decoder is YamlInput) {
            return when (val node = decoder.node) {
                is YamlScalar -> {
                    // Parse scalar as DpProperty and apply to all sides
                    val dpValue = parseDpProperty(node.content)
                    PaddingProperty.all(dpValue)
                }
                is YamlMap -> {
                    fun getDpProperty(key: String): DpProperty {
                        val content = node.get<YamlScalar>(key)?.content
                        return if (content != null) {
                            parseDpProperty(content)
                        } else {
                            DpProperty.Default
                        }
                    }

                    // Check for "v" (value for all sides)
                    val allValue = node.get<YamlScalar>("v")?.content
                    if (allValue != null) {
                        PaddingProperty.all(parseDpProperty(allValue))
                    } else {
                        PaddingProperty(
                            left = getDpProperty("l"),
                            top = getDpProperty("t"),
                            right = getDpProperty("r"),
                            bottom = getDpProperty("b"),
                        )
                    }
                }
                else -> throw SerializationException("Invalid padding: $node")
            }
        }

        // Generic fallback for non-YAML decoders
        return try {
            // Try to decode as a single string first
            val singleValue = decoder.decodeString()
            PaddingProperty.all(parseDpProperty(singleValue))
        } catch (_: Exception) {
            // Try to decode as a structured object
            var left = DpProperty.Default
            var top = DpProperty.Default
            var right = DpProperty.Default
            var bottom = DpProperty.Default
            var value: DpProperty? = null

            val c = decoder.beginStructure(buildClassSerialDescriptor("PaddingMap") {
                element<String>("l", isOptional = true)
                element<String>("t", isOptional = true)
                element<String>("r", isOptional = true)
                element<String>("b", isOptional = true)
                element<String>("v", isOptional = true)
            })

            loop@ while (true) {
                when (val i = c.decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> left = parseDpProperty(c.decodeStringElement(descriptor, 0))
                    1 -> top = parseDpProperty(c.decodeStringElement(descriptor, 1))
                    2 -> right = parseDpProperty(c.decodeStringElement(descriptor, 2))
                    3 -> bottom = parseDpProperty(c.decodeStringElement(descriptor, 3))
                    4 -> value = parseDpProperty(c.decodeStringElement(descriptor, 4))
                }
            }
            c.endStructure(descriptor)

            value?.let { PaddingProperty.all(it) } ?: PaddingProperty(left, top, right, bottom)
        }
    }

    override fun serialize(encoder: Encoder, value: PaddingProperty) {
        val d = buildClassSerialDescriptor("PaddingMap") {
            element<String>("l", isOptional = true)
            element<String>("t", isOptional = true)
            element<String>("r", isOptional = true)
            element<String>("b", isOptional = true)
        }
        val c = encoder.beginStructure(d)
        c.encodeStringElement(d, 0, value.left.toString())
        c.encodeStringElement(d, 1, value.top.toString())
        c.encodeStringElement(d, 2, value.right.toString())
        c.encodeStringElement(d, 3, value.bottom.toString())
        c.endStructure(d)
    }

    private fun parseDpProperty(content: String): DpProperty {
        val dpSerializer = DpPropertySerializer()
        return dpSerializer.parseString(content)
    }
}