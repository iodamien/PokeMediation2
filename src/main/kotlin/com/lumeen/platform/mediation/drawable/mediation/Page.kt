package com.lumeen.platform.com.lumeen.platform.mediation.drawable.mediation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.asLayoutScope
import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val root: LayoutProperty,
) {
    @Composable
    fun asCompose(density: Density) {
        Box {
            root.drawCompose(density, asLayoutScope())
        }
    }
}
