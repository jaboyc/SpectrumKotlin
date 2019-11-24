package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.Mixer
import com.jlogical.spectrum.model.MunsellColor
import com.jlogical.spectrum.model.Palette
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
    private fun mixingTab() : Pane {
        return BorderPane().apply{
            top = vbox(5.0){
                alignment = Pos.CENTER

                this += mixingBar()
                hbox(5.0){
                    alignment = Pos.CENTER

                    button("Mix"){
                        isDefaultButton = true

                        action {
                            val color = Mixer.mix()
                            if(color == null){
                                Alert(Alert.AlertType.ERROR, "Mixer is empty. Add colors to it by right clicking a color.").showAndWait()
                                return@action
                            }

                            this@apply.center = MunsellColorBlock(color)

                        }
                    }
                    button("Clear"){
                        action{
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
    private fun desiredColorTab() : Pane{
        return BorderPane().apply{

        }
    }

    /**
     * Returns the bar that displays all the mixing colors.
     */
    private fun mixingBar() : VBox {
        return VBox().apply{
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