package com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout

import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.mediation.drawable.composable.FillableProperty

interface LayoutProperty: ComposableProperty {
    val child: List<ComposableProperty>
}

fun LayoutProperty.getFillableComposable(): List<FillableProperty> {
    val tags: MutableList<FillableProperty> = mutableListOf()
    for (property in child) {
        when (property) {
            is LayoutProperty -> tags.addAll(property.getFillableComposable())
            is FillableProperty -> tags.add(property)
        }
    }
    return tags
}
