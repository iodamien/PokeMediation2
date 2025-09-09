package com.lumeen.platform.com.lumeen.platform.mediation.drawable.type

import androidx.compose.ui.text.style.TextAlign
import kotlinx.serialization.Serializable

@Serializable
enum class TextAlignProperty {
    Start,
    Center,
    Justify,
    End;

    fun asCompose(): TextAlign = when (this) {
        Start -> TextAlign.Start
        End -> TextAlign.End
        Center -> TextAlign.Center
        Justify -> TextAlign.Justify
    }
}
