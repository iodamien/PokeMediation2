package com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type.ContentAlignmentProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Box")
data class BoxLayout(
    override val modifier: List<ModifierProperty> = emptyList(),
    override val child: List<ComposableProperty> = emptyList(),
    @SerialName("content-alignment") val contentAlignment: ContentAlignmentProperty = ContentAlignmentProperty.TopStart,
): LayoutProperty {

    @Composable
    override fun drawCompose(density: Density) {
        Box(
            modifier = modifier.applyModifiers(density),
            contentAlignment = contentAlignment.asCompose(),
        ) {
            child.forEach { it.drawCompose(density) }
        }
    }
}
