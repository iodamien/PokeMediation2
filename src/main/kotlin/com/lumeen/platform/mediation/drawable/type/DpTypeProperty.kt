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
data class DpTypeProperty(
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
        val Zero = DpTypeProperty(0f, DpUnit.Dp)
        val Unspecified = DpTypeProperty(0f, DpUnit.Unspecified)
    }
}

enum class DpUnit {
    Dp,
    Px,
    Unspecified,
}

internal class DpPropertySerializer : KSerializer<DpTypeProperty> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Dp", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: DpTypeProperty) {
        // Serialize as the string representation
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DpTypeProperty {
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

    internal fun parseString(input: String): DpTypeProperty {
        val trimmed = input.trim()

        return when {
            trimmed.equals("Unspecified", ignoreCase = true) -> DpTypeProperty.Unspecified

            trimmed.endsWith(".dp", ignoreCase = true) -> {
                val valueStr = trimmed.dropLast(3) // Remove ".dp"
                val value = valueStr.toFloatOrNull()
                    ?: throw SerializationException("Invalid dp value: '$valueStr' in '$input'")
                DpTypeProperty(value, DpUnit.Dp)
            }

            trimmed.endsWith(".px", ignoreCase = true) -> {
                val valueStr = trimmed.dropLast(3) // Remove ".px"
                val value = valueStr.toFloatOrNull()
                    ?: throw SerializationException("Invalid px value: '$valueStr' in '$input'")
                DpTypeProperty(value, DpUnit.Px)
            }

            else -> {
                // Try to parse as a plain number (assume dp)
                val value = trimmed.toFloatOrNull()
                if (value != null) {
                    DpTypeProperty(value, DpUnit.Dp)
                } else {
                    throw SerializationException("Invalid DpProperty format: '$input'. Expected formats: '15.dp', '15.5.dp', '15.px', '15.5.px', or 'Unspecified'")
                }
            }
        }
    }
}