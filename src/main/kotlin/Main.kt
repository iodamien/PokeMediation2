package com.lumeen.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.singleWindowApplication
import com.charleskorn.kaml.AnchorsAndAliases
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.lumeen.platform.com.lumeen.platform.mediation.drawable.modifier.ModifierProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.File

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

@Serializable
@SerialName("Struct")
data class Struct(
    val name: String,
    val modifiers: List<ModifierProperty> = emptyList(),
) {
    fun applyModifiers(modifier: Modifier, density: Density): Modifier {
        var result = modifier
        for (mod in modifiers) {
            result = mod.applyModifier(result, density)
        }
        return result
    }
}
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
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

    val inputYaml = File("output.yaml").readText()
    val decodedStruct = yaml.decodeFromString(Struct.serializer(), inputYaml)
    singleWindowApplication {
        val density = LocalDensity.current
        Box(
            modifier = Modifier
                .then(decodedStruct.applyModifiers(Modifier, density)),
        ) {
            Text("Hello, ${decodedStruct.name}!")
        }
    }
}