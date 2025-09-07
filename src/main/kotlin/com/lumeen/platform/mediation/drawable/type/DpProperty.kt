package com.lumeen.platform.com.lumeen.platform.mediation.drawable.type

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@SerialName("Dp")
@Serializable(with = DpPropertySerializer::class)
data class DpProperty(
    val value: Float,
    val unit: DpUnit = DpUnit.Dp,
) {

    fun toComposeDp(density: Density): Dp {
        return when (unit) {
            DpUnit.Dp -> Dp(value)
            DpUnit.Px -> Dp(value / density.density)
            DpUnit.Unspecified -> Dp.Unspecified
        }
    }

    companion object {
        val Default = DpProperty(0f, DpUnit.Dp)
        val Unspecified = DpProperty(0f, DpUnit.Unspecified)
    }
}

enum class DpUnit {
    Dp,
    Px,
    Unspecified,
}

class DpPropertySerializer : KSerializer<DpProperty> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Dp", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: DpProperty) {
        // Serialize as the string representation
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DpProperty {
        // Handle YAML-specific decoding
        if (decoder is YamlInput) {
            return when (val node = decoder.node) {
                is YamlScalar -> parseString(node.content)
                else -> throw SerializationException("Expected string for DpProperty, got: $node")
            }
        }

        // Fallback for other formats
        val stringValue = decoder.decodeString()
        return parseString(stringValue)
    }

    internal fun parseString(input: String): DpProperty {
        val trimmed = input.trim()

        return when {
            trimmed.equals("Unspecified", ignoreCase = true) -> DpProperty.Unspecified

            trimmed.endsWith(".dp", ignoreCase = true) -> {
                val valueStr = trimmed.dropLast(3) // Remove ".dp"
                val value = valueStr.toFloatOrNull()
                    ?: throw SerializationException("Invalid dp value: '$valueStr' in '$input'")
                DpProperty(value, DpUnit.Dp)
            }

            trimmed.endsWith(".px", ignoreCase = true) -> {
                val valueStr = trimmed.dropLast(3) // Remove ".px"
                val value = valueStr.toFloatOrNull()
                    ?: throw SerializationException("Invalid px value: '$valueStr' in '$input'")
                DpProperty(value, DpUnit.Px)
            }

            else -> {
                // Try to parse as a plain number (assume dp)
                val value = trimmed.toFloatOrNull()
                if (value != null) {
                    DpProperty(value, DpUnit.Dp)
                } else {
                    throw SerializationException("Invalid DpProperty format: '$input'. Expected formats: '15.dp', '15.5.dp', '15.px', '15.5.px', or 'Unspecified'")
                }
            }
        }
    }
}