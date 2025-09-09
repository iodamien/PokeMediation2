package com.lumeen.platform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.charleskorn.kaml.AnchorsAndAliases
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.irobax.uikit.components.buttons.IRButton
import com.irobax.uikit.components.icon.IRFlag
import com.irobax.uikit.components.window.IRSingWindowApplication
import com.lumeen.platform.com.lumeen.platform.mediation.MediationLang
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ImageComposable
import com.lumeen.platform.mediation.drawable.composable.RichTextComposable
import com.lumeen.platform.mediation.drawable.composable.TextComposable
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.BoxLayout
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.ColumnLayout
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.RowLayout
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.mediation.Page
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.mediation.getAllFillableComposable
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import com.lumeen.platform.mediation.drawable.composable.FillableState
import com.lumeen.platform.mediation.drawable.composable.LocalFillableScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import java.nio.file.WatchEvent
import kotlin.time.Duration.Companion.seconds

val module = SerializersModule {
    polymorphic(ModifierProperty::class) {
        subclass(ModifierProperty.Rotate::class)
        subclass(ModifierProperty.Scale::class)
        subclass(ModifierProperty.Alpha::class)
        subclass(ModifierProperty.Padding::class)
        subclass(ModifierProperty.Size::class)
        subclass(ModifierProperty.Background::class)
        subclass(ModifierProperty.Clip::class)
        subclass(ModifierProperty.Border::class)
        subclass(ModifierProperty.DropShadow::class)
        subclass(ModifierProperty.InnerShadow::class)
    }

    polymorphic(ComposableProperty::class) {
        subclass(TextComposable::class)
        subclass(RichTextComposable::class)
        subclass(ImageComposable::class)

        subclass(BoxLayout::class)
        subclass(ColumnLayout::class)
        subclass(RowLayout::class)
    }

    polymorphic(LayoutProperty::class) {
        subclass(BoxLayout::class)
        subclass(ColumnLayout::class)
        subclass(RowLayout::class)
    }
}


val yaml = Yaml(
    configuration = YamlConfiguration(
        polymorphismStyle = PolymorphismStyle.Tag,
        anchorsAndAliases = AnchorsAndAliases.Permitted(),
        sequenceStyle = SequenceStyle.Block,  // This makes arrays/lists use flow style [item1, item2]
        encodeDefaults = false,
        strictMode = false,
    ),
    serializersModule = module
)

// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@OptIn(FlowPreview::class)
fun main() {
    val file = File(File("page.yaml").absolutePath)
    val inputYaml = file.readText()
    val decodedPage = yaml.decodeFromString(Page.serializer(), inputYaml)
    println(decodedPage.root)
    val jsonText = File("output.json").readText()

    val localState = FillableState("fr")
    localState.loadJson(jsonText)
    IRSingWindowApplication {
        var page: Page by remember { mutableStateOf(decodedPage) }
        var selectedLang: MediationLang by remember { mutableStateOf(MediationLang.FRENCH) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                watchFileAsFlow(file.toPath())
                    .debounce(0.5.seconds)
                    .collect {
                        println("File changed: $it")
                        try {
                            val newInput = it.toFile().readText()
                            val newPage = yaml.decodeFromString(Page.serializer(), newInput)
                            page = newPage
                            println("Reloaded page: $newPage")
                        } catch (e: Exception) {
                            println("Error reloading file: $e")
                        }
                    }
            }
        }

        val density = LocalDensity.current
        var export: String by remember { mutableStateOf(jsonText) }

        Row(
            modifier = Modifier.padding(16.dp),
        ) {
            CompositionLocalProvider(LocalFillableScope provides localState) {
                Box(
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight()
                ) {
                    page.asCompose(density)
                }
                Column(
                    modifier = Modifier
                        .width(256.dp)
                        .fillMaxHeight()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        MediationLang.entries.forEach { lang ->
                            val isSelected = selectedLang == lang
                            IRFlag(
                                modifier = Modifier
                                    .size(32.dp)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .alpha(if (isSelected) 1.0f else 0.5f)
                                    .clickable {
                                        selectedLang = lang
                                        localState.switchLang(lang.code)
                                   },
                                iconFlag = lang.icon
                            )
                        }
                    }

                    page.getAllFillableComposable().forEach {
                        it.editableComposable()
                    }

                    IRButton(
                        text = "Export"
                    ) {
                        export = localState.exportAsJson()
                        File("output.json").writeText(export)
                    }

                    Text(text = export)
                }
            }
        }
    }
}

fun watchFileAsFlow(file: java.nio.file.Path): Flow<java.nio.file.Path> = callbackFlow {
    val dir = file.parent
    val watcher = FileSystems.getDefault().newWatchService()
    val key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)

    val thread = Thread {
        try {
            while (!Thread.currentThread().isInterrupted) {
                val wk = watcher.take()
                for (event in wk.pollEvents()) {
                    if (event.kind() == OVERFLOW) continue
                    @Suppress("UNCHECKED_CAST")
                    val ev = event as WatchEvent<java.nio.file.Path>
                    val changed = dir.resolve(ev.context())
                    if (changed == file) trySend(changed)
                }
                if (!wk.reset()) break
            }
        } finally {
            watcher.close()
        }
    }.apply { isDaemon = true; start() }

    awaitClose {
        key.cancel()
        thread.interrupt()
    }
}

