package com.lumeen.platform.com.lumeen.platform.mediation.drawable.mediation

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutProperty
import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val root: LayoutProperty,
) {
    @Composable
    fun asCompose(density: Density) {
        root.drawCompose(density)
    }
}
