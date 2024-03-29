package com.jlogical.spectrum.model

import com.jlogical.spectrum.util.fromMunsell
import javafx.scene.paint.Color
import kotlin.math.abs
import kotlin.math.roundToInt
import com.jlogical.spectrum.util.fromRGB as rgb

/**
 * A MunsellColor stores the information for a color in the Munsell Color Spectrum. This class also contains helper methods.
 */
data class MunsellColor(val hue: Hue, val value: Double, val chroma: Double) {

    init {
        if (value < 0 || value > 10 || chroma > 40) throw IllegalArgumentException("Invalid arguments for MunsellColor")
    }

    /**
     * Returns the RGB color this Munsell Color corresponds to.
     */
    val color: Color by lazy {

        // If the color is grayscale, return a special color.
        if (hue.isGrayscale) {
            val shade = value / 10
            Color(shade, shade, shade, 1.0)
        } else
            fromMunsell(this)
    }

    companion object {

        /**
         * Returns a munsell color of type 'N'. [n] should be a value from 0-10.
         */
        fun N(n: Double): MunsellColor {
            return MunsellColor(Hue("N", 0.0), n, 0.0)
        }

        /**
         * Returns a munsell color from the given rgb color.
         */
        fun fromRGB(rgb: Color): MunsellColor {

            // If the color is grayscale, return the N version of MunsellColor.
            if (rgb.red == rgb.green && rgb.green == rgb.blue) return N(rgb.red * 10)

            return rgb(rgb)
        }

        /**
         * Returns the mixing of all the colors along with their associated weights.
         */
        fun mix(colors: List<MunsellColor>, weights: List<Double>): MunsellColor? {
            // Make sure the lists are valid.
            if (colors.isEmpty() || colors.size != weights.size) return null

            // Return the mixing of all the colors converted to their RGBs.
            return fromRGB(mixRGB(colors.map { it.color }, weights) ?: return null)
        }

        /**
         * Returns the mixing of all the RGB colors along with their associated weights.
         * Returns null if the weights were all 0 or the lists aren't initialized correctly.
         */
        fun mixRGB(colors: List<Color>, weights: List<Double>): Color? {

            // Make sure the lists are valid.
            if (colors.isEmpty() || colors.size != weights.size) return null

            var red = 0.0
            var green = 0.0
            var blue = 0.0
            var weight = 0.0

            colors.forEachIndexed { i, color ->
                red += color.red * weights[i]
                green += color.green * weights[i]
                blue += color.blue * weights[i]
                weight += weights[i]
            }

            // If all the weights were 0, return null.
            if (weight == 0.0) return null

            red /= weight
            green /= weight
            blue /= weight

            return Color(red, green, blue, 1.0)
        }
    }

    /**
     * Returns the distance from this munsell color to the one given.
     */
    fun distance(color: MunsellColor): Double = this.color.diff(color.color)

    /**
     * Returns the complementary color of this color, which is the color on the other side of the Munsell Spectrum.
     */
    fun complementaryColor(): MunsellColor {
        val newHue = Hue((hue.value + 50) % 100)
        return MunsellColor(newHue, value, chroma)
    }

    /**
     * Returns a list of analogous colors.
     */
    fun analogousColors(): List<MunsellColor> {
        return (1 until 10).map {
            MunsellColor(Hue(hue.prefix, (hue.hueValue + it) % 10), value, chroma)
        }
    }

    override fun toString(): String {
        if (hue.isGrayscale)
            return "N" + value.roundToInt()

        return hue.toString() + ", " + value.roundToInt() + ", " + chroma.roundToInt()
    }
}

/**
 * Extension method of JavaFX.Color which returns the difference between the colors based on their RGB values. Always a positive number.
 */
fun Color.diff(color: Color): Double = abs(red - color.red) + abs(green - color.green) + abs(blue - color.blue)