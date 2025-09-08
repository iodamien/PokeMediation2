package com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope

sealed class LayoutScope {

    data class ColumnLayoutScope(
        val columnScope: ColumnScope,
    ): LayoutScope()

    data class RowLayoutScope(
        val rowScope: RowScope,
    ): LayoutScope()

    data class BoxLayoutScope(
        val boxScope: BoxScope
    ): LayoutScope()
}

fun BoxScope.asLayoutScope() = LayoutScope.BoxLayoutScope(this)
fun ColumnScope.asLayoutScope() = LayoutScope.ColumnLayoutScope(this)
fun RowScope.asLayoutScope() = LayoutScope.RowLayoutScope(this)