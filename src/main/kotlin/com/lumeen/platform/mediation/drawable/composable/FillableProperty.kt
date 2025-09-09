package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

interface FillableProperty {
    val tag: String

    @Composable
    fun editableComposable()
}
