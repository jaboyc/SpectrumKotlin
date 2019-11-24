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
        hbox (5.0){
            alignment = Pos.CENTER

            minWidth = 355.0
            maxWidth = 355.0
            minHeight = 48.0
            maxHeight = 48.0

            bindChildren(Palette.colors) {
                MunsellColorBlock(it, 40.0, 40.0, 4.0, "").apply{
                    // Listen to mouse events.
                    setOnMousePressed {e->
                        if (e.isPrimaryButtonDown && e.clickCount == 2)
                            DetailsTab.showColorDetails(color)
                    }
                }
            }

            style {
                borderColor += box(Color.BLACK)
                borderStyle += BorderStrokeStyle.SOLID
                borderWidth += box(1.px)
            }
        }
    }
}