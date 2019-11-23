package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.Palette
import javafx.geometry.Pos
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

/**
 * Displays the current palette in a line.
 */
class PaletteBar : VBox(5.0) {

    init {

        alignment = Pos.CENTER

        label("Palette")
        hbox {
            minWidth = 320.0
            maxWidth = 320.0
            minHeight = 40.0
            maxHeight = 40.0

            bindChildren(Palette.colors) {
                MunsellColorBlock(it, 40.0, 40.0, 4.0, "")
            }

            style {
                borderColor += box(Color.BLACK)
                borderStyle += BorderStrokeStyle.SOLID
                borderWidth += box(1.px)
            }
        }
    }
}