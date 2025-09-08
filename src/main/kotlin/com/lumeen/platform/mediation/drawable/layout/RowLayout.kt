package com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type.VerticalAlignmentProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type.HorizontalArrangementProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Row")
data class RowLayout(
    override val modifier: List<ModifierProperty> = emptyList(),
    override val child: List<ComposableProperty> = emptyList(),
    @SerialName("vertical-alignment") val verticalAlignment: VerticalAlignmentProperty = VerticalAlignmentProperty.Top,
    @SerialName("horizontal-arrangement") val horizontalArrangementProperty: HorizontalArrangementProperty = HorizontalArrangementProperty(),
): LayoutProperty {

    @Composable
    override fun drawCompose(density: Density, layoutScope: LayoutScope) {
        Row(
            modifier = modifier.applyModifiers(density, layoutScope),
            verticalAlignment = verticalAlignment.asCompose(),
            horizontalArrangement = horizontalArrangementProperty.asCompose(density),
        ) {
            child.forEach { it.drawCompose(density, asLayoutScope()) }
        }
    }
}