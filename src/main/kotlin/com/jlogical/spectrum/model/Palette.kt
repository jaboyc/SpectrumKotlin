package com.jlogical.spectrum.model

import tornadofx.*

/**
 * A singleton for the current Palette. The palette can only hold 8 colors maximum.
 */
object Palette{

    /**
     * The colors currently in the palette.
     */
    val colors = mutableListOf<MunsellColor>().observable()

    /**
     * Adds the given MunsellColor to the palette.
     */
    fun addColor(color: MunsellColor){
        colors.add(0, color)

        // Ensure that there is a max of 8 colors.
        if(colors.size > 8)
            colors.removeAt(8)
    }
}