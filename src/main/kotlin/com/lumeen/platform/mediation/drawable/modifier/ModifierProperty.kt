package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Density
import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.yamlMap
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex.PaddingProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex.ScaleProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.complex.SizeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.ColorTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.DpTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.ShapeSerializer
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.ShapeTypeProperty
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
    @SerialName("fill-max-width")
    data class FillMaxWidth(
        val fraction: Float = 1f,
    ): ModifierProperty() {
        override fun applyModifier(
            modifier: Modifier,
            density: Density
        ): Modifier = modifier.fillMaxWidth(fraction)
    }

    @Serializable
    @SerialName("fill-max-height")
    data class FillMaxHeight(
        val fraction: Float = 1f,
    ): ModifierProperty() {
        override fun applyModifier(
            modifier: Modifier,
            density: Density
        ): Modifier = modifier.fillMaxHeight(fraction)
    }


    @Serializable
    @SerialName("fill-max-size")
    data class FillMaxSize(
        val fraction: Float = 1f,
    ): ModifierProperty() {
        override fun applyModifier(
            modifier: Modifier,
            density: Density
        ): Modifier = modifier.fillMaxSize(fraction)
    }

    @Serializable
    @SerialName("rotate")
    data class Rotate(val rotate: Float) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier.rotate(rotate)
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

    @Serializable
    @SerialName("background")
    data class Background(
        val color: ColorTypeProperty = ColorTypeProperty.Unspecified,
        val shape: ShapeTypeProperty = ShapeTypeProperty.Rectangle,
    ) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier.background(
            color = color.asComposeColor(),
            shape = shape.asComposeShape(density),
        )
    }

    @Serializable
    @SerialName("clip")
    data class Clip(
        val shape: ShapeTypeProperty,
    ) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier.clip(shape.asComposeShape(density))
    }

    @Serializable
    @SerialName("border")
    data class Border(
        val color: ColorTypeProperty = ColorTypeProperty.Unspecified,
        val width: DpTypeProperty = DpTypeProperty(1f),
        val shape: ShapeTypeProperty = ShapeTypeProperty.Rectangle,
    ) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier {
            return modifier
                .border(
                    width = width.toComposeDp(density),
                    color = color.asComposeColor(),
                    shape = shape.asComposeShape(density),
                )
        }
    }

    @Serializable
    @SerialName("drop-shadow")
    class DropShadow(
        val radius: DpTypeProperty = DpTypeProperty(4f),
        val spread: DpTypeProperty = DpTypeProperty(4f),
        val opacity: Float = 0.5f,
        val color: ColorTypeProperty = ColorTypeProperty.Black,
    ) : ModifierProperty() {
        override fun applyModifier(modifier: Modifier, density: Density): Modifier = modifier
            .dropShadow(
                shape = RectangleShape,
                shadow = androidx.compose.ui.graphics.shadow.Shadow(
                    radius = radius.toComposeDp(density),
                    color = color.asComposeColor(),
                    spread = spread.toComposeDp(density),
                    alpha = opacity,
                )
            )
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
            is ModifierProperty.Background ->
                encoder.encodeSerializableValue(ModifierProperty.Background.serializer(), value)
            is ModifierProperty.Clip ->
                encoder.encodeSerializableValue(ModifierProperty.Clip.serializer(), value)
            is ModifierProperty.Border ->
                encoder.encodeSerializableValue(ModifierProperty.Border.serializer(), value)
            is ModifierProperty.DropShadow ->
                encoder.encodeSerializableValue(ModifierProperty.DropShadow.serializer(), value)
            is ModifierProperty.FillMaxHeight ->
                encoder.encodeSerializableValue(ModifierProperty.FillMaxHeight.serializer(), value)
            is ModifierProperty.FillMaxSize ->
                encoder.encodeSerializableValue(ModifierProperty.FillMaxSize.serializer(), value)
            is ModifierProperty.FillMaxWidth ->
                encoder.encodeSerializableValue(ModifierProperty.FillMaxWidth.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): ModifierProperty {
        // Peek at YAML keys to decide which subtype to use.
        if (decoder is YamlInput) {
            val node = decoder.node
            val map = node.yamlMap // throws a nice error if it's not a map
            val keys = map.entries.map { it.key.content }.toSet()

            return when {
                "fill-max-width" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.FillMaxWidth.serializer())
                "fill-max-height" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.FillMaxHeight.serializer())
                "fill-max-size" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.FillMaxSize.serializer())
                "drop-shadow" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.DropShadow.serializer())
                "rotate" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Rotate.serializer())
                "scale" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Scale.serializer())
                "alpha" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Alpha.serializer())
                "padding" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Padding.serializer())
                "size" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Size.serializer())
                "background" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Background.serializer())
                "clip" in keys && node is YamlMap ->
                    ModifierProperty.Clip(
                        shape = ShapeSerializer.parseShapeFromYamlNode(node.get<YamlMap>("clip")!!)
                    )
                "border" in keys ->
                    decoder.decodeSerializableValue(ModifierProperty.Border.serializer())
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

fun List<ModifierProperty>.applyModifiers(density: Density, modifier: Modifier = Modifier): Modifier {
    var m = modifier
    forEach { m = it.applyModifier(m, density) }
    return m
}
