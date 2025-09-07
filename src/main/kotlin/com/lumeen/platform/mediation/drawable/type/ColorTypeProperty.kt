package com.lumeen.platform.com.lumeen.platform.mediation.drawable.type

import androidx.compose.ui.graphics.Color
import com.charleskorn.kaml.YamlInput
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
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = ColorDeserializer::class)
@SerialName("Color")
class ColorTypeProperty(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255,
    val unspecified: Boolean = false,
) {
    fun asComposeColor(): Color {
        return if (unspecified) {
            Color.Unspecified
        } else {
            Color(
                red = red / 255f,
                green = green / 255f,
                blue = blue / 255f,
                alpha = alpha / 255f,
            )
        }
    }

    companion object {
        val Red = ColorTypeProperty(255, 0, 0, 255)
        val Black = ColorTypeProperty(0, 0, 0, 255)
        val Unspecified = ColorTypeProperty(0, 0, 0, 0, true)
    }
}

private object ColorDeserializer : KSerializer<ColorTypeProperty> {

    // IMPORTANT: contextual => don't force object shape
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Color", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: ColorTypeProperty) {
        val d = buildClassSerialDescriptor("ColorMapping") {
            element<Int>("r", isOptional = true)
            element<Int>("g", isOptional = true)
            element<Int>("b", isOptional = true)
            element<Int>("a", isOptional = true)
        }

        val c = encoder.beginStructure(d)
        c.encodeIntElement(d, 0, value.red)
        c.encodeIntElement(d, 1, value.green)
        c.encodeIntElement(d, 2, value.blue)
        c.encodeIntElement(d, 3, value.alpha)
        c.endStructure(d)
    }

    override fun deserialize(decoder: Decoder): ColorTypeProperty {
        // Fast path for YAML nodes
        if (decoder is YamlInput) {
            return when (val node = decoder.node) {
                is com.charleskorn.kaml.YamlScalar -> parseScalar(node.content)
                is com.charleskorn.kaml.YamlMap -> {
                    fun getInt(key: String, default: Int) =
                        node.get<com.charleskorn.kaml.YamlScalar>(key)?.content?.toIntOrNull() ?: default
                    ColorTypeProperty(
                        red = getInt("r", 0),
                        green = getInt("g", 0),
                        blue = getInt("b", 0),
                        alpha = getInt("a", 255),
                    )
                }
                else -> throw SerializationException("Invalid color: $node")
            }
        }

        // Generic fallback for other decoders
        var r = 0; var g = 0; var b = 0; var a = 255
        val c = decoder.beginStructure(descriptor)
        loop@ while (true) {
            when (val i = c.decodeElementIndex(descriptor)) {
                0 -> r = c.decodeIntElement(descriptor, 0)
                1 -> g = c.decodeIntElement(descriptor, 1)
                2 -> b = c.decodeIntElement(descriptor, 2)
                3 -> a = c.decodeIntElement(descriptor, 3)
                kotlinx.serialization.encoding.CompositeDecoder.DECODE_DONE -> break@loop
                else -> error("Unexpected index $i")
            }
        }
        c.endStructure(descriptor)
        return ColorTypeProperty(r, g, b, a)
    }

    private fun parseScalar(raw: String): ColorTypeProperty {
        val name = raw.trim()
        // Named colors (add more as needed)
        when (name.lowercase()) {
            "white" -> return ColorTypeProperty(255, 255, 255, 255)
            "black" -> return ColorTypeProperty(0, 0, 0, 255)
            "red"   -> return ColorTypeProperty(255, 0, 0, 255)
            "green" -> return ColorTypeProperty(0, 128, 0, 255)
            "blue"  -> return ColorTypeProperty(0, 0, 255, 255)
        }
        // Hex: #RGB, #RRGGBB, #RRGGBBAA
        if (name.startsWith("#")) return parseHex(name)
        throw SerializationException("Unknown color '$raw'")
    }

    private fun parseHex(hex: String): ColorTypeProperty {
        val s = hex.removePrefix("#")
        val (r, g, b, a) = when (s.length) {
            3 -> {
                val r = s[0].digitToInt(16) * 17
                val g = s[1].digitToInt(16) * 17
                val b = s[2].digitToInt(16) * 17
                Quad(r, g, b, 255)
            }
            6 -> Quad(
                s.substring(0, 2).toInt(16),
                s.substring(2, 4).toInt(16),
                s.substring(4, 6).toInt(16),
                255
            )
            8 -> Quad(
                s.substring(0, 2).toInt(16),
                s.substring(2, 4).toInt(16),
                s.substring(4, 6).toInt(16),
                s.substring(6, 8).toInt(16)
            )
            else -> throw SerializationException("Hex color must be #RGB, #RRGGBB or #RRGGBBAA")
        }
        return ColorTypeProperty(r, g, b, a)
    }

    // tiny helper to keep tuple-like return readable
    private data class Quad(val r: Int, val g: Int, val b: Int, val a: Int)
}