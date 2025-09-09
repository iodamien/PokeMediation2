package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Density
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutScope
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.SpTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.TextAlignProperty
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RichText")
data class RichTextComposable(
    val html: String,
    override val modifier: List<ModifierProperty> = emptyList(),
    @SerialName("max-lines") val maxLines: Int = Int.MAX_VALUE,
    @SerialName("text-align") val textAlign: TextAlignProperty = TextAlignProperty.Start,
    @SerialName("line-height") val lineHeight: SpTypeProperty = SpTypeProperty.Unspecified,
    @SerialName("soft-wrap") val softWrap: Boolean = true,
): ComposableProperty {

    @Composable
    override fun drawCompose(
        density: Density,
        layoutScope: LayoutScope,
    ) {
        val richTextState = rememberRichTextState()
        LaunchedEffect(html) {
            richTextState.setHtml(html)
        }

        RichText(
            modifier = modifier.applyModifiers(density, layoutScope),
            state = richTextState,
            maxLines = maxLines,
            textAlign = textAlign.asCompose(),
            lineHeight = lineHeight.toCompose(density),
            softWrap = softWrap,
        )
    }
}