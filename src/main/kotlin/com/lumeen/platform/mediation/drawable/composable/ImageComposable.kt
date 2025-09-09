package com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.irobax.uikit.components.image.IRImagePicker
import com.irobax.uikit.components.image.ImageSource
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutScope
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.applyModifiers
import com.lumeen.platform.mediation.drawable.composable.FillableProperty
import com.lumeen.platform.mediation.drawable.composable.LocalFillableScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import javax.imageio.ImageIO

@Serializable
@SerialName("Image")
data class ImageComposable(
    override val tag: String,
    override val modifier: List<ModifierProperty> = emptyList(),
): ComposableProperty, FillableProperty {

    @Composable
    override fun drawCompose(density: Density, layoutScope: LayoutScope) {
        val localFillableScope = LocalFillableScope.current
        val imagePath = localFillableScope.getString(tag)
        var currentPainter: Painter? by remember { mutableStateOf(null) }

        LaunchedEffect(imagePath) {
            currentPainter = if (imagePath != null) {
                runCatching {
                    ImageIO.read(File(imagePath))
                }.getOrNull()?.toPainter()
            } else {
                null
            }
        }

        currentPainter?.also { painter ->
            Image(
                modifier = modifier.applyModifiers(density, layoutScope),
                contentDescription = null,
                painter = painter,
            )
        } ?: Box(modifier = modifier.applyModifiers(density, layoutScope))
    }

    @Composable
    override fun editableComposable() {
        val localFillableScope = LocalFillableScope.current
        val path = localFillableScope.getString(tag)
        var imageSource: ImageSource? by remember { mutableStateOf(ImageSource.File(
            File(path.orEmpty())
        ).takeIf { File(path.orEmpty()).exists() }) }

        IRImagePicker(
            modifier = Modifier
                .size(256.dp),
            imageSource = imageSource,
            onImagePicked = { files ->
                println("Picked files: $files")
                val firstFile = files.getOrNull(0)
                if (firstFile != null) {
                    localFillableScope.updateState(tag, firstFile.file.absolutePath)
                    imageSource = ImageSource.File(firstFile.file)
                }
            },
            onDeleteClick = {
                println("Delete image")
                imageSource = null
                localFillableScope.updateState(tag, null)
            }
        )
    }
}