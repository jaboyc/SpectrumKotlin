package com.jlogical.spectrum.model

import tornadofx.*
import java.lang.NullPointerException

/**
 * A singleton for the current colors used for mixing. This can only hold 8 colors maximum.
 */
object Mixer {

    /**
     * The colors currently in the palette.
     */
    val colors = mutableListOf<MunsellColor>().observable()

    /**
     * Adds the given munsell color to the palette.
     */
    fun addColor(color: MunsellColor) {
        colors.add(0, color)

        // Ensure that there is a max of 8 colors.
        if (Palette.colors.size > 8)
            Palette.colors.removeAt(8)
    }

    /**
     * Removes the given color from the palette.
     */
    fun removeColor(color: MunsellColor) {
        colors.remove(color)
    }

    /**
     * Returns whether the Palette contains the given munsell color.
     */
    fun contains(color: MunsellColor): Boolean {
        return colors.contains(color)
    }

    /**
     * Returns the mixing of all the colors in the mixer.
     */
    fun mix() : MunsellColor?{

        // If there are no colors in the mixer, return nothing.
        if(colors.isEmpty()) return null

        // Return the mixing of all the colors with the weight of 1.
        return MunsellColor.mix(colors, colors.map{1.0})
    }
}