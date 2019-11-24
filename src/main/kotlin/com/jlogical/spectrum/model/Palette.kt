package com.jlogical.spectrum.model

import tornadofx.*

/**
 * A singleton for the current Palette. The palette can only hold 8 colors maximum.
 */
object Palette {

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
        if (colors.size > 8)
            colors.removeAt(8)
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
     * Returns the closest color in the Palette with [color].
     */
    fun closestColor(color: MunsellColor): MunsellColor? = colors.minBy { color.distance(it) }
}