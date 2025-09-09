package com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import com.lumeen.platform.mediation.drawable.composable.ImageExport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import javax.imageio.ImageIO

@Serializable
@SerialName("Image")
data class ImageComposable(
    override val tag: String,
    override val modifier: List<ModifierProperty> = emptyList(),
    val useBase64: Boolean = true // Option to enable/disable base64 encoding
) : ComposableProperty, FillableProperty {

    @Composable
    override fun drawCompose(density: Density, layoutScope: LayoutScope) {
        val localFillableScope = LocalFillableScope.current

        // Try to get ImageExport first, fallback to string path for backward compatibility
        val imageExport by localFillableScope.getImageExportState(tag)
        val tmpExport = imageExport
        val imagePath by localFillableScope.getStringState(tag)

        val currentPainter by remember(tmpExport, imagePath) {
            mutableStateOf(
                when {
                    // Handle ImageExport objects (new way)
                    imageExport != null -> {
                        try {
                            val file = if (tmpExport?.base64 != null) {
                                // Create temporary file from base64
                                val tempFile = File.createTempFile("temp_image_", getFileExtension(tmpExport.mimeType))
                                tmpExport.saveToFile(tempFile.absolutePath)
                                tempFile
                            } else {
                                File(tmpExport?.path)
                            }

                            if (file.exists()) {
                                ImageIO.read(file)?.toPainter()
                            } else {
                                println("Image file doesn't exist: ${file.absolutePath}")
                                null
                            }
                        } catch (e: Exception) {
                            println("Error loading ImageExport: ${e.message}")
                            null
                        }
                    }
                    // Handle string paths (backward compatibility)
                    !imagePath.isNullOrEmpty() && File(imagePath).exists() -> {
                        try {
                            ImageIO.read(File(imagePath))?.toPainter()
                        } catch (e: Exception) {
                            println("Error loading image from path: ${e.message}")
                            null
                        }
                    }
                    else -> {
                        println("No valid image found for tag: $tag")
                        null
                    }
                }
            )
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
        val currentLang by localFillableScope.readLang

        // Try to get ImageExport first, fallback to string path
        val imageExport by localFillableScope.getImageExportState(tag)
        val tmpExport = imageExport
        val stringPath by localFillableScope.getStringState(tag)

        var imageSource: ImageSource? by remember(currentLang, imageExport, stringPath) {
            mutableStateOf(
                when {
                    // Handle ImageExport objects
                    imageExport != null -> {
                        val file = if (tmpExport?.base64 != null) {
                            // Create temporary file from base64 for display
                            val tempFile = File.createTempFile("temp_display_", getFileExtension(tmpExport.mimeType))
                            tmpExport.saveToFile(tempFile.absolutePath)
                            tempFile
                        } else {
                            File(tmpExport?.path)
                        }

                        if (file.exists()) {
                            ImageSource.File(file)
                        } else {
                            null
                        }
                    }
                    // Handle string paths (backward compatibility)
                    !stringPath.isNullOrEmpty() && File(stringPath).exists() -> {
                        ImageSource.File(File(stringPath))
                    }
                    else -> null
                }
            )
        }

        IRImagePicker(
            modifier = Modifier.size(256.dp),
            imageSource = imageSource,
            onImagePicked = { files ->
                val firstFile = files.getOrNull(0)
                if (firstFile != null) {
                    if (useBase64) {
                        // Store as ImageExport with base64 encoding
                        localFillableScope.updateImageFromPath(tag, firstFile.file.absolutePath)
                    } else {
                        // Store as simple string path (backward compatibility)
                        localFillableScope.updateState(tag, firstFile.file.absolutePath)
                    }
                    imageSource = ImageSource.File(firstFile.file)
                }
            },
            onDeleteClick = {
                println("Delete image")
                imageSource = null
                localFillableScope.removeState(tag)
            }
        )
    }

    private fun getFileExtension(mimeType: String?): String {
        return when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "image/gif" -> ".gif"
            "image/webp" -> ".webp"
            "image/bmp" -> ".bmp"
            else -> ".jpg"
        }
    }
}