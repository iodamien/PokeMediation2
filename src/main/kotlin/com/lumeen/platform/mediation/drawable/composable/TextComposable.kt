package com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Text")
data class TextComposable(
    val text: String,
    override val modifier: List<ModifierProperty> = emptyList(),
): ComposableProperty {

    @Composable
    override fun drawCompose(density: Density) {
        Text(
            modifier = modifier.applyModifiers(density),
            text = text,
        )
    }
}
