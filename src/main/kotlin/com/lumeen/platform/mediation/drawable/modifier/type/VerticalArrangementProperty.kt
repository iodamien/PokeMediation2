package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type

import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.DpTypeProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("vertical-arrangement")
data class VerticalArrangementProperty(
    val space: DpTypeProperty = DpTypeProperty.Zero,
    val align: VerticalAlignmentProperty = VerticalAlignmentProperty.Top,
) {
    fun asCompose(density: Density) = androidx.compose.foundation.layout.Arrangement.spacedBy(
        space = space.toComposeDp(density),
        alignment = align.asCompose()
    )
}
