package com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type.HorizontalAlignmentProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type.VerticalArrangementProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Column")
data class ColumnLayout(
    override val modifier: List<ModifierProperty> = emptyList(),
    override val child: List<ComposableProperty> = emptyList(),
    @SerialName("horizontal-alignment") val horizontalAlignment: HorizontalAlignmentProperty = HorizontalAlignmentProperty.Start,
    @SerialName("vertical-arrangement") val verticalArrangementProperty: VerticalArrangementProperty = VerticalArrangementProperty(),
): LayoutProperty {

    @Composable
    override fun drawCompose(density: Density, layoutScope: LayoutScope) {
        Column(
            modifier = modifier.applyModifiers(density, layoutScope),
            horizontalAlignment = horizontalAlignment.asCompose(),
            verticalArrangement = verticalArrangementProperty.asCompose(density),
        ) {
            child.forEach { it.drawCompose(density, asLayoutScope()) }
        }
    }
}