package com.lumeen.platform.mediation.drawable.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Density
import com.irobax.uikit.components.textfield.IRTextField
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutScope
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.ColorTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.SpTypeProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.type.SpUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Text")
data class TextComposable(
    override val tag: String,
    val placeholder: String = "",
    val color: ColorTypeProperty = ColorTypeProperty.Unspecified,
    @SerialName("font-size") val fontSize: SpTypeProperty = SpTypeProperty(14f, SpUnit.Sp),
    override val modifier: List<ModifierProperty> = emptyList(),
): ComposableProperty, FillableProperty {

    @Composable
    override fun drawCompose(density: Density, layoutScope: LayoutScope) {
        val localFillableScope = LocalFillableScope.current
        val textValue = localFillableScope.getString(tag)
        Text(
            modifier = modifier.applyModifiers(density, layoutScope),
            text = textValue ?: placeholder,
            color = color.asComposeColor(),
            fontSize = fontSize.toCompose(density),
        )
    }

    @Composable
    override fun editableComposable() {
        val localFillableScope = LocalFillableScope.current
        var textFieldValue by remember { mutableStateOf(TextFieldValue(localFillableScope.getString(tag).orEmpty())) }
        IRTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = { newTextFieldValue ->
                textFieldValue = newTextFieldValue
                localFillableScope.updateState(tag, textFieldValue.text)
            },
        )
    }
}
