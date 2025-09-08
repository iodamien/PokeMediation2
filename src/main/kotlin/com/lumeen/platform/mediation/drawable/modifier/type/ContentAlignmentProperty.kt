package com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.type

import androidx.compose.ui.Alignment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("content-alignment")
enum class ContentAlignmentProperty {
    TopStart,
    TopCenter,
    CenterStart,
    Center,
    CenterEnd,
    BottomStart,
    BottomCenter,
    BottomEnd;

    fun asCompose(): Alignment = when (this) {
        TopStart -> Alignment.TopStart
        TopCenter -> Alignment.TopCenter
        CenterStart -> Alignment.CenterStart
        Center -> Alignment.Center
        CenterEnd -> Alignment.CenterEnd
        BottomStart -> Alignment.BottomStart
        BottomCenter -> Alignment.BottomCenter
        BottomEnd -> Alignment.BottomEnd
    }
}