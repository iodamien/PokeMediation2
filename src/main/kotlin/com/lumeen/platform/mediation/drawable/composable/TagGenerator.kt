package com.lumeen.platform.mediation.drawable.composable

import kotlin.reflect.KClass

object TagGenerator {
    private val tagCount: MutableMap<KClass<out
    FillableProperty>, Long> = mutableMapOf()

    fun generateTag(kClass: KClass<out FillableProperty>): String {
        val currentCount = tagCount[kClass] ?: 0L
        tagCount[kClass] = currentCount + 1
        val className = kClass.simpleName
        return "${className}_$currentCount"
    }

    fun reset() {
        tagCount.clear()
    }
}