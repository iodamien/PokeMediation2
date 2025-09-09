package com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutScope
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.ColorTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.SpTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.SpUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Text")
data class TextComposable(
    override val tag: String = TagGenerator.generateTag(TextComposable::class),
    @SerialName("text") override val value: String = "",
    val color: ColorTypeProperty = ColorTypeProperty.Unspecified,
    @SerialName("font-size") val fontSize: SpTypeProperty = SpTypeProperty(14f, SpUnit.Sp),
    override val modifier: List<ModifierProperty> = emptyList(),
): ComposableProperty, FillableProperty<String> {

    @Composable
    override fun drawCompose(density: Density, layoutScope: LayoutScope) {
        Text(
            modifier = modifier.applyModifiers(density, layoutScope),
            text = value,
            color = color.asComposeColor(),
            fontSize = fontSize.toCompose(density),
        )
    }
}
