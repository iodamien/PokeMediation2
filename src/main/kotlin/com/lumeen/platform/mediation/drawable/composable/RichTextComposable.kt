package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.irobax.uikit.components.icon.IRClickableIconSquare
import com.irobax.uikit.components.icon.IconResources
import com.irobax.uikit.components.textfield.IRRichTextEditor
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutScope
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.SpTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.TextAlignProperty
import com.mohamedrejeb.richeditor.model.RichSpanStyle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RichText")
data class RichTextComposable(
    override val tag: String,
    override val modifier: List<ModifierProperty> = emptyList(),
    @SerialName("max-lines") val maxLines: Int = Int.MAX_VALUE,
    @SerialName("text-align") val textAlign: TextAlignProperty = TextAlignProperty.Start,
    @SerialName("line-height") val lineHeight: SpTypeProperty = SpTypeProperty.Unspecified,
    @SerialName("soft-wrap") val softWrap: Boolean = true,
): ComposableProperty, FillableProperty {

    @Composable
    override fun drawCompose(
        density: Density,
        layoutScope: LayoutScope,
    ) {
        val richTextState = rememberRichTextState()
        val fillableState = LocalFillableScope.current
        val text by fillableState.getStringState(tag)

        LaunchedEffect(text) {
            println("Text: $text")
            text?.also { txt ->
                richTextState.setHtml(html = txt)
            }
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

    @Composable
    override fun editableComposable() {
        val localFillableState = LocalFillableScope.current
        val richTextState = rememberRichTextState()
        LaunchedEffect(richTextState.annotatedString) {
            withContext(Dispatchers.IO) {
                localFillableState.updateState(tag, richTextState.toHtml())
            }
        }
//
//        Column {
//            RichTextControls(richTextState)
//            RichTextEditor(
//                state = richTextState
//            )
//        }

        IRRichTextEditor(
            modifier = Modifier.fillMaxWidth()
                .heightIn(min = 200.dp),
            richTextState = richTextState,
        )
    }

    @Composable
    private fun RichTextControls(richTextState: RichTextState) {

        Row {
            IRClickableIconSquare(
                modifier = Modifier.size(32.dp),
                icon = IconResources.TEXT,
            ) {
                richTextState.addParagraphStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Left,
                    )
                )
            }

            IRClickableIconSquare(
                modifier = Modifier.size(32.dp),
                icon = IconResources.TEXT,
            ) {
                richTextState.addParagraphStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Center,
                    )
                )
            }

            IRClickableIconSquare(
                modifier = Modifier.size(32.dp),
                icon = IconResources.TEXT,
            ) {
                richTextState.addParagraphStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Right,
                    )
                )
            }

            IRClickableIconSquare(
                modifier = Modifier.size(32.dp),
                icon = IconResources.TEXT,
            ) {
                richTextState.toggleSpanStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}