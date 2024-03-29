package com.jlogical.spectrum.gui

import com.jlogical.spectrum.app.MainView
import com.jlogical.spectrum.model.Hue
import com.jlogical.spectrum.model.MunsellColor
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The tab that stores the details of a color
 */
class DetailsTab : BorderPane() {

    init {
        top = colorDetailsPane()
        center = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            tab("Complementary Color") {
                this += complementaryTab()
            }
            tab("Analogous Color") {
                this += analogousTab()
            }
        }
    }

    companion object {
        val huePrefix = SimpleStringProperty("")
        val hueValue = SimpleStringProperty("")
        val valueValue = SimpleStringProperty("")
        val chromaValue = SimpleStringProperty("")

        val colorValue = SimpleObjectProperty<MunsellColor?>()
        var detailsPane: BorderPane? = null

        /**
         * Shows the details of the given MunsellColor.
         */
        fun showColorDetails(color: MunsellColor) {
            huePrefix.value = color.hue.prefix
            hueValue.value = color.hue.hueValue.toString()
            valueValue.value = color.value.toString()
            chromaValue.value = color.chroma.toString()
            showColor()
            MainView.switchTab(1)
        }

        /**
         * Shows the MunsellColorBlock based on the given values.
         */
        private fun showColor() {

            detailsPane?.center = try {
                val hue = Hue(huePrefix.value, hueValue.value.toDouble())
                val value = valueValue.value.toDouble()
                val chroma = chromaValue.value.toDouble()
                colorValue.value = MunsellColor(hue, value, chroma)
                MunsellColorBlock(MunsellColor(hue, value, chroma))
            } catch (e: Exception) {
                Label("Make sure hueValue, value, and chroma are all valid numbers.").apply {
                    style {
                        fontSize = 20.0.px
                    }
                }
            }
        }
    }

    /**
     * Returns the complementary tab.
     */
    private fun complementaryTab(): Pane {
        return BorderPane().apply {
            colorValue.onChange {
                center = if (it == null) HBox() else MunsellColorBlock(it.complementaryColor()).apply {
                    setOnMouseClicked { e ->
                        if (e.clickCount == 2)
                            DetailsTab.showColorDetails(color)
                    }
                }
            }
        }
    }

    /**
     * Returns the analogous tab.
     */
    private fun analogousTab(): Pane {
        return Pane().apply {
            val pane = this
            colorValue.onChange {

                children.clear()

                val color: MunsellColor = colorValue.value ?: return@onChange

                // Properties of the analogous colors circle.
                val radius = 190.0
                var rot = PI / 2

                // Add this color to the bottom of the circle.
                this += MunsellColorBlock(color, 70.0, 70.0).apply {
                    translateXProperty().bind(pane.widthProperty().divide(2).add(radius * cos(rot)))
                    translateYProperty().bind(pane.heightProperty().divide(2).minus(25).add(radius * sin(rot)))

                }

                // Add all the analogous colors in a circle.
                for (analogousColor in color.analogousColors()) {
                    rot += 2 * PI / 10

                    this += MunsellColorBlock(analogousColor, 70.0, 70.0).apply {
                        translateXProperty().bind(pane.widthProperty().divide(2).add(radius * cos(rot)))
                        translateYProperty().bind(pane.heightProperty().divide(2).minus(25).add( radius * sin(rot)))

                        setOnMouseClicked { e ->
                            if (e.clickCount == 2)
                                DetailsTab.showColorDetails(analogousColor)
                        }
                    }
                }

            }
        }
    }

    /**
     * Returns a placeholder box for when no color is selected.
     */
    private fun placeHolder(): Pane {
        return vbox {
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
     * The sub-tab that displays the color details.
     */
    private fun colorDetailsPane(): BorderPane {
        detailsPane = borderpane {
            minHeight = 100.0
            paddingAll = 4.0

            center = placeHolder()
            top = hbox(8.0) {
                alignment = Pos.CENTER

                label("Hue Prefix")
                textfield(huePrefix) {
                    maxWidth = 70.0
                }

                label("   Hue Value")
                textfield(hueValue) {
                    maxWidth = 70.0
                }

                label("   Value")
                textfield(valueValue) {
                    maxWidth = 70.0
                }

                label("   Chroma")
                textfield(chromaValue) {
                    maxWidth = 70.0
                }

                button("Get Color") {
                    isDefaultButton = true
                    action {
                        showColor()
                    }
                }

                button("Clear") {
                    action {
                        huePrefix.value = ""
                        hueValue.value = ""
                        valueValue.value = ""
                        chromaValue.value = ""
                        colorValue.value = null
                        center = placeHolder()
                    }
                }
            }
        }
        return detailsPane!!
    }
}
