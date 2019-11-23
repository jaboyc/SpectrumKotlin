package com.jlogical.spectrum.util

import com.jlogical.spectrum.model.Hue
import com.jlogical.spectrum.model.MunsellColor
import javafx.scene.paint.Color
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Stores the mapping of a Munsell Color to an RGB value.
 * Map<Hue, Map<Value, Map<Chroma, Color>>>
 */
private val munsellToRGB: LinkedHashMap<Hue, LinkedHashMap<Int, LinkedHashMap<Int, Color>>> = LinkedHashMap()

/**
 * Stores the mapping of an RGB value to a Munsell Color.
 * Map<Red, Map<Green, Map<Blue, MunsellColor>>>
 */
private val rgbToMunsell: LinkedHashMap<Int, LinkedHashMap<Int, LinkedHashMap<Int, MunsellColor>>> = LinkedHashMap()

/**
 * A list of hues parsed in the csv.
 */
val hues: List<Hue> by lazy {
    munsellToRGB.keys.toList()
}

/**
 * Finds the closest rgb color from the given munsell color.
 */
fun fromMunsell(munsellColor: MunsellColor): Color {

    // Get the first layer.
    val layerOne = munsellToRGB.getOrElse(munsellColor.hue) {
        munsellToRGB[munsellToRGB.keys.filter{munsellColor.hue.prefix == it.prefix}.minBy { abs(munsellColor.hue.value - it.value) }]
                ?: throw IllegalArgumentException("Could not find layer one for $munsellColor")
    }


    // Get the second layer
    val layerTwo = layerOne.getOrElse(munsellColor.value.roundToInt()) {
        layerOne[layerOne.keys.minBy { abs(munsellColor.value - it) }]
                ?: throw IllegalArgumentException("Could not find layer two for $munsellColor")
    }

    // Get the third layer
    return layerTwo.getOrElse(munsellColor.chroma.roundToInt()) {
        layerTwo[layerTwo.keys.minBy { abs(munsellColor.chroma - it) }]
                ?: throw IllegalArgumentException("Could not find layer three for $munsellColor")
    }
}

/**
 * Finds the closest munsell color from the given rgb color.
 */
fun fromRGB(rgb: Color): MunsellColor {
    // Get the first layer.
    val layerOne = rgbToMunsell.getOrElse((rgb.red * 255).toInt()) {
        rgbToMunsell[rgbToMunsell.keys.minBy { abs(rgb.red * 255 - it) }]
                ?: throw IllegalArgumentException("Could not find layer one for $rgb")
    }

    // Get the second layer
    val layerTwo = layerOne.getOrElse((rgb.green * 255).toInt()) {
        layerOne[layerOne.keys.minBy { abs(rgb.green * 255 - it) }]
                ?: throw IllegalArgumentException("Could not find layer two for $rgb")
    }

    // Get the third layer
    return layerTwo.getOrElse((rgb.blue * 255).toInt()) {
        layerTwo[layerTwo.keys.minBy { abs(rgb.blue * 255 - it) }]
                ?: throw IllegalArgumentException("Could not find layer three for $rgb")
    }
}

/**
 * Returns the MunsellColor with the highest chroma in the given hue.
 */
fun highestChromaInHue(hue: Hue): MunsellColor {

    // Get the first layer.
    val layerOne = munsellToRGB[hue] ?: throw IllegalArgumentException("Invalid hue")

    // The highest munsell color so far.
    var highest: MunsellColor? = null

    // Go through each color in the hue and find the one with the highest chroma.
    for (value in layerOne.keys) {
        for (chroma in layerOne[value]!!.keys) {
            if (highest == null || chroma >= highest.chroma) {
                highest = MunsellColor(hue, value.toDouble(), chroma.toDouble())
            }
        }
    }

    return highest ?: throw IllegalArgumentException("Invalid hue")
}

/**
 * Returns the 2D slice of the given hue
 */
fun getColorMatrix(hue: Hue) : List<List<MunsellColor>>{

    // Get the first layer.
    val layerOne = munsellToRGB[hue] ?: throw IllegalArgumentException("Invalid hue")

    // The count for grayscale.
    var count = 1.0

    // Return each color in the hue.
    return layerOne.keys.map {value ->
        listOf(MunsellColor.N(count++), *layerOne[value]!!.keys.map { chroma -> MunsellColor(hue, value.toDouble(), chroma.toDouble()) }.toTypedArray())
    }
}

/**
 * Builds both the munsellToRGB map and rgbToMunsell map.
 */
fun buildCSVMaps() {
    buildMunsellToRGBMap()
    buildRGBToMunsellMap()
}

/**
 * Builds the munsellToRGB map.
 */
private fun buildMunsellToRGBMap() {

    // For each of the lines in the Munsell2RGB.csv, parse the row and add it to the correct location in the map.
    File("src/main/res/Munsell2RGB.csv").forEachLine {

        // If the line starts with key, we know this is the header line and we need to skip it.
        if (it.startsWith("Key")) return@forEachLine

        val split: List<String> = it.split(",")

        // Parse the row.
        val hue = Hue(split[2], split[3].toDouble())
        val value = split[4].toInt()
        val chroma = split[5].toInt()

        val red = split[6].toDouble()
        val green = split[7].toDouble()
        val blue = split[8].toDouble()
        val rgb = Color(red / 255, green / 255, blue / 255, 1.0)

        // If value or chroma are 0, continue.
        if (value == 0 || chroma == 0) return@forEachLine

        addMunsellColorToMap(hue, value, chroma, rgb)
    }
}

/**
 * Maps a hue, value, chroma to a color.
 */
private fun addMunsellColorToMap(hue: Hue, value: Int, chroma: Int, rgb: Color) {
    // Get the innermost layer to add the chroma -> rgb mapping to.
    val layerOne = munsellToRGB.getOrPut(hue) { LinkedHashMap() }
    val layerTwo = layerOne.getOrPut(value) { LinkedHashMap() }

    // Map chroma to the rgb
    layerTwo[chroma] = rgb
}

/**
 * Builds the rgbToMunsell map.
 */
private fun buildRGBToMunsellMap() {
    // For each of the lines in the Munsell2RGB.csv, parse the row and add it to the correct location in the map.
    File("src/main/res/RGB2Munsell.csv").forEachLine {

        // If the line starts with red, we know this is the header line and we need to skip it.
        if (it.startsWith("Red")) return@forEachLine

        val split: List<String> = it.split(",")

        val red = split[0].toDouble()
        val green = split[1].toDouble()
        val blue = split[2].toDouble()

        // Parse the row.
        val hue = Hue(split[3])
        val value = split[4].toDouble()
        val chroma = split[5].toDouble()
        val munsellColor = MunsellColor(hue, value, chroma)

        addRGBToMap(red, green, blue, munsellColor)
    }
}

/**
 * Maps a red, green, blue to a munsell color.
 */
private fun addRGBToMap(red: Double, green: Double, blue: Double, munsellColor: MunsellColor) {
    // Get the innermost layer to add the blue -> munsell color mapping to.
    val layerOne = rgbToMunsell.getOrPut(red.roundToInt()) { LinkedHashMap() }
    val layerTwo = layerOne.getOrPut(green.roundToInt()) { LinkedHashMap() }

    // Map blue to munsell color
    layerTwo[blue.toInt()] = munsellColor
}