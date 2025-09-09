package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf

class FillableState {

    private val state: SnapshotStateMap<String, Any> = mutableStateMapOf()

    fun updateState(tag: String, value: Any) {
        state[tag] = value
    }

    fun getString(tag: String): String? {
        return state[tag] as? String
    }

    fun getInt(tag: String): Int? {
        return state[tag] as? Int
    }

    fun getLong(tag: String): Long? {
        return state[tag] as? Long
    }

    fun getFloat(tag: String): Float? {
        return state[tag] as? Float
    }

    fun getDouble(tag: String): Double? {
        return state[tag] as? Double
    }

    fun getBoolean(tag: String): Boolean? {
        return state[tag] as? Boolean
    }
}

val LocalFillableScope = staticCompositionLocalOf { FillableState() }