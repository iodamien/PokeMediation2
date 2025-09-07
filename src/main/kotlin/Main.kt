package com.lumeen.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.singleWindowApplication
import com.charleskorn.kaml.AnchorsAndAliases
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.ComposableProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.composable.TextComposable
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.BoxLayout
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.layout.LayoutProperty
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.mediation.Page
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
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
    }

    polymorphic(ComposableProperty::class) {
        subclass(TextComposable::class)
    }

    polymorphic(LayoutProperty::class) {
        subclass(BoxLayout::class)
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

//    val modifierProperties = listOf(
//        ModifierProperty.Rotate(45f),
//        ModifierProperty.Scale(ScaleProperty(1.5f, 1.5f)),
//        ModifierProperty.Alpha(0.8f),
//        ModifierProperty.Padding(PaddingProperty(left = 10, top = 20, right = 10, bottom = 20)),
//        ModifierProperty.Rotate(45f),
//    )
//
//    val struct = Struct(name = "ExampleStruct", modifiers = modifierProperties)
//
//    val yamlString = yaml.encodeToString(Struct.serializer(), struct)
//    println("YAML Output:\n$yamlString")
//    File("output.yaml").writeText(yamlString)
//
//    val decodeStruct = yaml.decodeFromString(Struct.serializer(), yamlString)
//    println("Decoded Struct:\n$decodeStruct")



    val file = File(File("page.yaml").absolutePath)
    val inputYaml = file.readText()
    val decodedPage = yaml.decodeFromString(Page.serializer(), inputYaml)
    println(decodedPage.root)
    singleWindowApplication {
        var page: Page by remember { mutableStateOf(decodedPage) }
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
        page.asCompose(density)
    }
}

fun watchFileAsFlow(file: java.nio.file.Path): Flow<java.nio.file.Path> = callbackFlow {
    val dir = file.parent
    val watcher = FileSystems.getDefault().newWatchService()
    val key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)

    val thread = Thread {
        try {
            println("HELLO 0")
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

