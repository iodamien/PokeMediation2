package com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.Density
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlScalar
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutScope
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.yaml
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.util.Base64
import javax.imageio.ImageIO
import kotlin.collections.emptyList

@Serializable(with = ImageSerializer::class)
@SerialName("Image")
data class ImageComposable(
    val painter: Painter,
    override val modifier: List<ModifierProperty> = emptyList(),
): ComposableProperty {

    @Composable
    override fun drawCompose(density: Density, layoutScope: LayoutScope) {
        Image(
            modifier = modifier.applyModifiers(density, layoutScope),
            contentDescription = null,
            painter = painter,
        )
    }
}

class ImageSerializer: KSerializer<ImageComposable> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("Image", SerialKind.CONTEXTUAL)

    override fun serialize(
        encoder: Encoder,
        value: ImageComposable
    ) {

    }

    override fun deserialize(decoder: Decoder): ImageComposable {
        if (decoder is com.charleskorn.kaml.YamlInput) {
            val node = decoder.node
            if (node is YamlMap) {
                fun getString(key: String, default: String) =
                    node.get<YamlScalar>(key)?.content ?: default

                val path = getString("path", "")
                val base64 = getString("base64", "")
                val modifier = node.get<YamlNode>("modifier")?.let { modifierNode ->
                    when (modifierNode) {
                        is YamlList -> {
                            // Already a list
                            yaml.decodeFromYamlNode(
                                ListSerializer(ModifierProperty.serializer()),
                                modifierNode
                            )
                        }
                        is YamlMap -> {
                            // Single modifier - wrap in list
                            listOf(
                                yaml.decodeFromYamlNode(
                                    ModifierProperty.serializer(),
                                    modifierNode
                                )
                            )
                        }
                        else -> emptyList()
                    }
                } ?: emptyList()

                val painter = when {
                    base64.isNotEmpty() -> {
                        val img = base64ToBufferedImage(base64)
                            ?: throw SerializationException("Failed to decode Base64 image")
                        img.toPainter()
                    }
                    path.isNotEmpty() -> {
                        val imgFile = File(path)
                        if (imgFile.exists()) {
                            ImageIO.read(imgFile).toPainter()
                        } else {
                            throw SerializationException("Image file not found at path: $path")
                        }
                    }
                    else -> throw SerializationException("Image must have either 'path' or 'resource' defined")
                }

                return ImageComposable(
                    modifier = modifier,
                    painter = painter,
                )
            }
        }

        throw SerializationException("ImageComposable cannot be deserialized directly")
    }
}

fun base64ToBufferedImage(base64String: String): BufferedImage? {
    return try {
        // Remove data URL prefix if present (e.g., "data:image/png;base64,")
        val base64Data = if (base64String.contains(",")) {
            base64String.substringAfter(",")
        } else {
            base64String
        }

        // Decode Base64 string to byte array
        val imageBytes = Base64.getDecoder().decode(base64Data)

        // Create ByteArrayInputStream from byte array
        val inputStream = ByteArrayInputStream(imageBytes)

        // Read the image from the input stream
        ImageIO.read(inputStream)
    } catch (e: Exception) {
        println("Error converting Base64 to BufferedImage: ${e.message}")
        null
    }
}