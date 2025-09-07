package com.lumeen.platform.com.lumeen.platform.mediation.drawable.type

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlScalar
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

@SerialName("Shape")
@Serializable(with = ShapeSerializer::class)
sealed class ShapeTypeProperty {

    @Serializable
    @SerialName("Rectangle")
    object Rectangle : ShapeTypeProperty() {
        override fun asComposeShape(localDensity: Density): Shape = RectangleShape
    }

    @Serializable
    @SerialName("Circle")
    object Circle : ShapeTypeProperty() {
        override fun asComposeShape(localDensity: Density): Shape = androidx.compose.foundation.shape.CircleShape
    }

    @Serializable
    data class RoundedCornerShape(
        val topLeft: DpTypeProperty = DpTypeProperty.Zero,
        val topRight: DpTypeProperty = DpTypeProperty.Zero,
        val bottomRight: DpTypeProperty = DpTypeProperty.Zero,
        val bottomLeft: DpTypeProperty = DpTypeProperty.Zero,
    ) : ShapeTypeProperty() {
        override fun asComposeShape(localDensity: Density): Shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = topLeft.toComposeDp( localDensity),
            topEnd = topRight.toComposeDp(localDensity),
            bottomEnd = bottomRight.toComposeDp(localDensity),
            bottomStart = bottomLeft.toComposeDp(localDensity),
        )

        companion object {
            fun all(radius: DpTypeProperty) = RoundedCornerShape(
                topLeft = radius,
                topRight = radius,
                bottomRight = radius,
                bottomLeft = radius,
            )
        }
    }

    abstract fun asComposeShape(localDensity: Density): Shape
}

object ShapeSerializer : KSerializer<ShapeTypeProperty> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Shape", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: ShapeTypeProperty) {
        when (value) {
            is ShapeTypeProperty.Rectangle -> encoder.encodeString("Rectangle")
            is ShapeTypeProperty.Circle -> encoder.encodeString("Circle")
            is ShapeTypeProperty.RoundedCornerShape -> {
                val d = buildClassSerialDescriptor("RoundedCornerShape") {
                    element<String>("topLeft", isOptional = true)
                    element<String>("topRight", isOptional = true)
                    element<String>("botRight", isOptional = true)
                    element<String>("botLeft", isOptional = true)
                }
                val c = encoder.beginStructure(d)
                c.encodeStringElement(d, 0, value.topLeft.toString())
                c.encodeStringElement(d, 1, value.topRight.toString())
                c.encodeStringElement(d, 2, value.bottomRight.toString())
                c.encodeStringElement(d, 3, value.bottomLeft.toString())
                c.endStructure(d)
            }
        }
    }

    override fun deserialize(decoder: Decoder): ShapeTypeProperty {
        if (decoder is YamlInput) {
            return parseShapeFromYamlNode(decoder.node)
        }

        throw IllegalStateException("Unreachable")
    }

    internal fun parseShapeFromYamlNode(node: YamlNode): ShapeTypeProperty {
        return when (node) {
            is YamlScalar -> {
                when (val content = node.content.trim().lowercase()) {
                    "rectangle" -> ShapeTypeProperty.Rectangle
                    "circle" -> ShapeTypeProperty.Circle
                    else -> {
                        // Try to parse as a single radius for rounded corners
                        try {
                            val radius = ShapeSerializer.parseDpProperty(node.content)
                            ShapeTypeProperty.RoundedCornerShape.all(radius)
                        } catch (e: Exception) {
                            throw SerializationException("Unknown shape: '$content'")
                        }
                    }
                }
            }

            is YamlMap -> {
                // Check if it's a tagged shape
                val type = node.get<YamlScalar>("type")?.content?.lowercase()

                when (type) {
                    "rectangle" -> ShapeTypeProperty.Rectangle
                    "circle" -> ShapeTypeProperty.Circle
                    "roundedcornershape", "rounded" -> ShapeSerializer.parseRoundedCornerShape(node)
                    else -> {
                        // Assume it's RoundedCornerShape if no type specified
                        ShapeSerializer.parseRoundedCornerShape(node)
                    }
                }
            }

            else -> throw SerializationException("Invalid shape: $node")
        }
    }

    private fun parseRoundedCornerShape(node: YamlMap): ShapeTypeProperty.RoundedCornerShape {
        fun getDpProperty(key: String): DpTypeProperty {
            val content = node.get<YamlScalar>(key)?.content
            return if (content != null) {
                parseDpProperty(content)
            } else {
                DpTypeProperty.Zero
            }
        }

        // Check for uniform radius first
        val radius = node.get<YamlScalar>("radius")?.content
            ?: node.get<YamlScalar>("r")?.content

        if (radius != null) {
            return ShapeTypeProperty.RoundedCornerShape.all(parseDpProperty(radius))
        }

        return ShapeTypeProperty.RoundedCornerShape(
            topLeft = getDpProperty("topLeft"),
            topRight = getDpProperty("topRight"),
            bottomRight = getDpProperty("botRight"),
            bottomLeft = getDpProperty("botLeft"),
        )
    }

    private fun parseDpProperty(content: String): DpTypeProperty {
        val dpSerializer = DpPropertySerializer()
        return dpSerializer.parseString(content)
    }
}