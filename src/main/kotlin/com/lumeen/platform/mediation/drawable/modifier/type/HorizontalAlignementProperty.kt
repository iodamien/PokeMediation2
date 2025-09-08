package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type

import androidx.compose.ui.Alignment

enum class HorizontalAlignmentProperty {
    Start,
    Center,
    End;

    fun asCompose(): Alignment.Horizontal = when (this) {
        Start -> Alignment.Start
        Center -> Alignment.CenterHorizontally
        End -> Alignment.End
    }
}
