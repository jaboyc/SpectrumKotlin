package com.jlogical.spectrum.gui

import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import tornadofx.*

/**
 * Tab that displays the palette and functions within the Palette.
 */
class PaletteTab : BorderPane() {

    init {
        top = PaletteBar()

        center = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            tab("Mixing") {

            }
            tab("Desired Color") {

            }
        }
    }
}