package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type

import androidx.compose.ui.Alignment

enum class VerticalAlignmentProperty {
    Top,
    Center,
    Bottom;

    fun asCompose(): Alignment.Vertical = when (this) {
        Top -> Alignment.Top
        Center -> Alignment.CenterVertically
        Bottom -> Alignment.Bottom
    }
}
