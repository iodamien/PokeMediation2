package com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty

interface ComposableProperty {

    val modifier: List<ModifierProperty>

    @Composable
    fun drawCompose(density: Density)
}
