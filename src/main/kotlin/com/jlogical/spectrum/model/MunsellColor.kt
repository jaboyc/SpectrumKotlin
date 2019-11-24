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
    }

    /**
     * Extension method of JavaFX.Color which returns the difference between the colors based on their RGB values. Always a positive number.
     */
    private fun Color.diff(color: Color) : Double = abs(red - color.red) + abs(green - color.green) + abs(blue - color.blue)

    /**
     * Returns the distance from this munsell color to the one given.
     */
    fun distance(color: MunsellColor) : Double = this.color.diff(color.color)

    /**
     * Returns the complementary color of this color, which is the color on the other side of the Munsell Spectrum.
     */
    fun complementaryColor() : MunsellColor{
        val newHue = Hue((hue.value + 50) % 100)
        return MunsellColor(newHue, value, chroma)
    }

    /**
     * Returns a list of analogous colors.
     */
    fun analogousColors() : List<MunsellColor>{
        return (1 until 10).map{
            MunsellColor(Hue(hue.prefix, (hue.hueValue + it) % 10), value, chroma)
        }
    }

    override fun toString(): String {
        if (hue.isGrayscale)
            return "N" + value.roundToInt()

        return hue.toString() + ", " + value.roundToInt() + ", " + chroma.roundToInt()
    }
}