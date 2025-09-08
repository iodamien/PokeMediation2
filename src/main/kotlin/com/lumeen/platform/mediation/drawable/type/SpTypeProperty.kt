package com.lumeen.platform.com.lumeen.platform.mediation.drawable.type

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@SerialName("Sp")
@Serializable(with = SpPropertySerializer::class)
data class SpTypeProperty(
    val value: Float,
    val unit: SpUnit = SpUnit.Sp,
) {

    fun toComposeSp(density: Density): TextUnit {
        return when (unit) {
            SpUnit.Sp -> value.sp
            SpUnit.Px -> (value / density.density).sp
            SpUnit.Unspecified -> TextUnit.Unspecified
        }
    }

    companion object {
        val Zero = SpTypeProperty(0f, SpUnit.Unspecified)
        val Unspecified = SpTypeProperty(0f, SpUnit.Unspecified)
    }
}

enum class SpUnit {
    Sp,
    Px,
    Unspecified,
}

internal class SpPropertySerializer : KSerializer<SpTypeProperty> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Sp", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: SpTypeProperty) {
        // Serialize as the string representation
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): SpTypeProperty {
        // Handle YAML-specific decoding
        if (decoder is YamlInput) {
            return when (val node = decoder.node) {
                is YamlScalar -> parseString(node.content)
                else -> throw SerializationException("Expected string for SpProperty, got: $node")
            }
        }

        // Fallback for other formats
        val stringValue = decoder.decodeString()
        return parseString(stringValue)
    }

    internal fun parseString(input: String): SpTypeProperty {
        val trimmed = input.trim()

        return when {
            trimmed.equals("Unspecified", ignoreCase = true) -> SpTypeProperty.Unspecified

            trimmed.endsWith(".sp", ignoreCase = true) -> {
                val valueStr = trimmed.dropLast(3) // Remove ".sp"
                val value = valueStr.toFloatOrNull()
                    ?: throw SerializationException("Invalid sp value: '$valueStr' in '$input'")
                SpTypeProperty(value, SpUnit.Sp)
            }

            trimmed.endsWith(".px", ignoreCase = true) -> {
                val valueStr = trimmed.dropLast(3) // Remove ".px"
                val value = valueStr.toFloatOrNull()
                    ?: throw SerializationException("Invalid px value: '$valueStr' in '$input'")
                SpTypeProperty(value, SpUnit.Px)
            }

            else -> {
                // Try to parse as a plain number (assume sp)
                val value = trimmed.toFloatOrNull()
                if (value != null) {
                    SpTypeProperty(value, SpUnit.Sp)
                } else {
                    throw SerializationException("Invalid SpProperty format: '$input'. Expected formats: '15.sp', '15.5.sp', '15.px', '15.5.px', or 'Unspecified'")
                }
            }
        }
    }
}