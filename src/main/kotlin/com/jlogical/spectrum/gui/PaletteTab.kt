package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.Mixer
import com.jlogical.spectrum.model.MunsellColor
import com.jlogical.spectrum.model.Palette
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.TabPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

/**
 * Tab that displays the palette and functions within the Palette.
 */
class PaletteTab : BorderPane() {

    init {
        top = PaletteBar().apply {
            paddingAll = 4.0
        }

        center = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            tab("Mixing") {
                this += mixingTab()
            }
            tab("Desired Color") {
                this += desiredColorTab()
            }
        }
    }

    /**
     * Returns the mixing sub-tab.
     */
    private fun mixingTab(): Pane {
        return BorderPane().apply {
            top = vbox(5.0) {
                alignment = Pos.CENTER

                this += mixingBar()
                hbox(5.0) {
                    minWidth = 400.0
                    alignment = Pos.CENTER

                    // This fixes a strange glitch with button sizes.
                    style {
                        borderWidth += box(0.px)
                    }

                    button("Mix") {
                        isDefaultButton = true
                        action {
                            val color = Mixer.mix()
                            if (color == null) {
                                Alert(Alert.AlertType.ERROR, "Mixer is empty. Add colors to it by right clicking a color.").showAndWait()
                                return@action
                            }

                            this@apply.center = MunsellColorBlock(color)
                        }
                    }
                    button("Clear") {
                        action {
                            Mixer.colors.clear()
                            this@apply.center = null
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the desired color sub-tab.
     */
    private fun desiredColorTab(): Pane {
        return BorderPane().apply {
            top = hbox(5.0) {
                paddingAll = 4.0
                alignment = Pos.CENTER

                label("       Desired Color: ")
                borderpane {
                    center = placeHolder()

                    Mixer.desiredColor.onChange {
                        center = if (it == null) placeHolder() else MunsellColorBlock(it)

                    }
                }
                button("Find Mixing Weights") {
                    paddingAll = 4.0
                    isDefaultButton = true

                    action {
                        val desired = Mixer.desiredColor.value

                        //Ensure variables are set for finding mixing weights.
                        if (desired == null) {
                            Alert(Alert.AlertType.ERROR, "There is no desired color. Set a desired color by right clicking a color.").showAndWait()
                            return@action
                        }
                        if (Palette.colors.isEmpty()) {
                            Alert(Alert.AlertType.ERROR, "You must have at least one color in the palette before you can begin finding a desired color.").showAndWait()
                            return@action
                        }

                        val mixingWeights = Mixer.findMixingWeights()

                        this@apply.center = VBox(4.0).apply{
                            alignment = Pos.CENTER

                            label("Weights")
                            hbox(4.0) {
                                alignment = Pos.CENTER

                                Palette.colors.forEachIndexed { i, color ->
                                    this += MunsellColorBlock(color, 60.0, 60.0, 5.0, String.format("%.2fx", mixingWeights[i]))
                                }
                            }

                            label("Actual Result")
                            this += MunsellColorBlock(MunsellColor.mix(Palette.colors, mixingWeights)!!, rounded = 5.0)

                        }
                    }
                }

            }
        }
    }

    /**
     * Returns a place holder pane for when no desired color is selected.
     */
    private fun placeHolder(): Pane {
        return Pane().apply {
            maxWidth = 110.0
            minWidth = 110.0
            maxHeight = 60.0
            minHeight = 60.0
            style {
                borderColor += box(Color.BLACK)
                borderStyle += BorderStrokeStyle.DASHED
                borderWidth += box(1.px)
            }
        }
    }

    /**
     * Returns the bar that displays all the mixing colors.
     */
    private fun mixingBar(): VBox {
        return VBox().apply {
            alignment = Pos.CENTER

            label("Mixing Bar")
            hbox(5.0) {
                alignment = Pos.CENTER

                minWidth = 365.0
                maxWidth = 365.0
                minHeight = 48.0
                maxHeight = 48.0

                bindChildren(Mixer.colors) {
                    MunsellColorBlock(it, 40.0, 40.0, 4.0, "").apply {
                        // Listen to mouse events.
                        setOnMousePressed { e ->
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
}