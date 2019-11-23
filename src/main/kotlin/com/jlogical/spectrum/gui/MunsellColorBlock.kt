package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.MunsellColor
import com.jlogical.spectrum.util.highestChromaInHue
import com.sun.org.apache.xpath.internal.operations.Bool
import javafx.beans.binding.Bindings
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import tornadofx.*

/**
 * A block that displays a munsell color.
 */
class MunsellColorBlock(color: MunsellColor, width: Double = 110.0, height: Double = 60.0, rounded: Double = 0.0, var text: String? = null) : BorderPane() {
    init {

        // If text is null, set it to the color's string.
        if(text == null) text = color.toString()

        minWidth = width
        minHeight = height
        maxWidth = width
        maxHeight = height

        style {
            backgroundColor += color.color
            backgroundRadius += box(rounded.px)
        }

        bottom = label(text ?: "") {
            textFill = textContrastColor(color.color)
        }

        contextmenu {
            item("View Details"){
                setOnAction {
                    DetailsTab.showColorDetails(color)
                }
            }
            item("Add to Palette")
            item("Remove from Palette")
            item("Add to Mixer")
            item("Set as Desired Color")
        }
    }

    /**
     * Returns white or black, depending on what the contrast for the given background color should be.
     * I adapted this from https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
     */
    private fun textContrastColor(color: Color): Color {
        val luminance = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue)

        return if (luminance > 0.5) Color.BLACK else Color.WHITE
    }
}
