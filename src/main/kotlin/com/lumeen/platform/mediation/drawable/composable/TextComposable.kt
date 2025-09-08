package com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
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
    val text: String,
    val color: ColorTypeProperty = ColorTypeProperty.Unspecified,
    @SerialName("font-size") val fontSize: SpTypeProperty = SpTypeProperty(14f, SpUnit.Sp),
    override val modifier: List<ModifierProperty> = emptyList(),
): ComposableProperty {

    @Composable
    override fun drawCompose(density: Density) {
        Text(
            modifier = modifier.applyModifiers(density),
            text = text,
            color = color.asComposeColor(),
            fontSize = fontSize.toComposeSp(density),
        )
    }
}
