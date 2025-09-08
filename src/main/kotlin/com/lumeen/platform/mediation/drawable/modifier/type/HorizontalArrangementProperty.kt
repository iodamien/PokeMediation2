package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.DpTypeProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("horizontal-arrangement")
data class HorizontalArrangementProperty(
    val space: DpTypeProperty = DpTypeProperty.Zero,
    val align: HorizontalAlignmentProperty = HorizontalAlignmentProperty.Start,
) {
    fun asCompose(density: Density) = Arrangement.spacedBy(
        space = space.toComposeDp(density),
        alignment = align.asCompose()
    )
}