package com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Column")
data class ColumnLayout(
    override val modifier: List<ModifierProperty> = emptyList(),
    override val child: List<ComposableProperty> = emptyList(),
): LayoutProperty {

    @Composable
    override fun drawCompose(density: Density) {
        Column(
            modifier = modifier.applyModifiers(density)
        ) {
            child.forEach { it.drawCompose(density) }
        }
    }
}