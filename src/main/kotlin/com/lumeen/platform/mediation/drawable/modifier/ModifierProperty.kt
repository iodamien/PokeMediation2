package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Density
import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.yamlMap
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex.PaddingProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex.ScaleProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex.SizeProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ModifierPropertySerializer::class)
@SerialName("Modifier")
sealed class ModifierProperty {

    @Serializable
    @SerialName("rotate")
    data class Rotate(val degrees: Float) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier.rotate(degrees)
    }

    @Serializable
    @SerialName("scale")
    data class Scale(val scale: ScaleProperty) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier.scale(scale.x, scale.y)
    }

    @Serializable
    @SerialName("alpha")
    data class Alpha(val alpha: Float) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier.alpha(alpha)
    }

    @Serializable
    @SerialName("padding")
    data class Padding(val padding: PaddingProperty) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier.padding(
            start = padding.left.toComposeDp(density),
            top = padding.top.toComposeDp(density),
            end = padding.right.toComposeDp(density),
            bottom = padding.bottom.toComposeDp(density),
        )
    }

    @Serializable
    @SerialName("size")
    data class Size(val size: SizeProperty) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier {
            return modifier.size(
                width = size.width.toComposeDp(density),
                height = size.height.toComposeDp(density),
            )
        }
    }

    abstract fun applyModifier(modifier: Modifier, density: Density): Modifier
}

object ModifierPropertySerializer : KSerializer<ModifierProperty> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ModifierProperty")

    override fun serialize(encoder: Encoder, value: ModifierProperty) {
        when (value) {
            is ModifierProperty.Rotate ->
                encoder.encodeSerializableValue(ModifierProperty.Rotate.serializer(), value)
            is ModifierProperty.Scale  ->
                encoder.encodeSerializableValue(ModifierProperty.Scale.serializer(), value)
            is ModifierProperty.Alpha  ->
                encoder.encodeSerializableValue(ModifierProperty.Alpha.serializer(), value)
            is ModifierProperty.Padding ->
                encoder.encodeSerializableValue(ModifierProperty.Padding.serializer(), value)
            is ModifierProperty.Size ->
                encoder.encodeSerializableValue(ModifierProperty.Size.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): ModifierProperty {
        // Peek at YAML keys to decide which subtype to use.
        if (decoder is YamlInput) {
            val node = decoder.node
            val map = node.yamlMap // throws a nice error if it's not a map
            val keys = map.entries.map { it.key.content }.toSet()

            return when {
                "degrees" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Rotate.serializer())
                "scale" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Scale.serializer())
                "alpha" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Alpha.serializer())
                "padding" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Padding.serializer())
                "size" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Size.serializer())
                else -> throw SerializationException(
                    "Unknown modifier at ${decoder.node}: keys=$keys"
                )
            }
        } else {
            // Non-YAML fallback (shouldn’t normally happen with Kaml)
            throw SerializationException(
                "ModifierProperty must be decoded from YAML (got ${decoder::class.simpleName})"
            )
        }
    }
}