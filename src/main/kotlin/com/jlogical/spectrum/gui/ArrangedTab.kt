package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.Hue
import com.jlogical.spectrum.model.MunsellColor
import com.jlogical.spectrum.util.getColorMatrix
import com.jlogical.spectrum.util.highestChromaInHue
import com.jlogical.spectrum.util.hues
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import tornadofx.*

/**
 * The tab that stores the arranged display of the Munsell Color Spectrum.
 */
class ArrangedTab : BorderPane() {

    init {
        bottom = hbox(3.0) {
            alignment = Pos.CENTER
            paddingAll = 4.0

            // Get the hue pickers at the bottom.
            for (hue in hues) {
                this += huePicker(hue)
            }
        }

        arrangeColors(hues[0])
    }

    /**
     * Arranges the colors in the center of the tab with the selected hue's colors.
     */
    private fun arrangeColors(selectedHue: Hue) {

        // Get the color slice first.
        val slice = getColorMatrix(selectedHue)

        // Go through and add each color to the center.
        center = vbox(3.0) {
            paddingAll = 4.0

            for (row in slice) {

                this += hbox(3.0) {
                    for (color in row) {
                        this += arrangedColor(color)
                    }
                }
            }
        }
    }

    /**
     * Returns a MunsellColorBlock that is stylized to be in the center.
     */
    private fun arrangedColor(color: MunsellColor): MunsellColorBlock {
        return MunsellColorBlock(color, 50.0, 50.0, 4.0, text = "").apply {
            // Listen to mouse events.
            setOnMousePressed {
                if (it.isPrimaryButtonDown && it.clickCount == 2)
                    DetailsTab.showColorDetails(color)
            }
        }
    }

    /**
     * Returns a hue picker for the bottom of the screen.
     */
    private fun huePicker(hue: Hue): MunsellColorBlock {
        return MunsellColorBlock(highestChromaInHue(hue), 24.0, 24.0, 4.0, "").apply {

            // Listen to mouse events.
            setOnMousePressed {
                if (it.isPrimaryButtonDown)
                    if (it.clickCount == 1)
                        arrangeColors(hue)
                if (it.clickCount == 2)
                    DetailsTab.showColorDetails(highestChromaInHue(hue))
            }
        }
    }
}
