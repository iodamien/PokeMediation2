package com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout

import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty

interface LayoutProperty: ComposableProperty {
    val child: List<ComposableProperty>
}
