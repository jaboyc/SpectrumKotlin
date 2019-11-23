package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.MunsellColor
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import tornadofx.*

/**
 * Manipulates an image with MunsellColors.
 */
class ImageTab : BorderPane() {

    /**
     * Holds the currently loaded image.
     */
    private var image: Image? = null

    /**
     * Holds the buttons that shift between visible and invisible depending on the image.
     */
    private lateinit var buttonBar: HBox

    init {
        top = hbox(4.0) {
            paddingAll = 4.0
            alignment = Pos.CENTER

            button("Upload Image...") {
                isDefaultButton = true
                action {
                    val file = chooseFile("Open Image", arrayOf(FileChooser.ExtensionFilter("Images (*.png, *.jpg)", "*.png", "*.jpg")), FileChooserMode.Single, null)
                    if (file.isNotEmpty()) {
                        image = Image(file[0].toURI().toString(), 450.0, 450.0, true, true)
                        this@ImageTab.center = VBox(5.0).apply {
                            alignment = Pos.CENTER

                            this += PaletteBar()
                            imageview(image!!).apply {
                                setOnMousePressed {
                                    this@ImageTab.bottom = HBox().apply {
                                        alignment = Pos.CENTER
                                        paddingAll = 4.0

                                        this += MunsellColorBlock(MunsellColor.fromRGB(image.pixelReader.getColor(it.x.toInt(), it.y.toInt()))).apply {
                                            setOnMousePressed { e ->
                                                if (e.isPrimaryButtonDown && e.clickCount == 2)
                                                    DetailsTab.showColorDetails(this.color)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        buttonBar.isVisible = true
                    }
                }
            }

            buttonBar = hbox(4.0) {
                isVisible = false

                button("Posterize") {
                    action {
                        println("Do something!")
                    }
                }
                button("Clear Image") {
                    action {
                        image = null
                        this@ImageTab.center = null
                        this@ImageTab.bottom = null
                        buttonBar.isVisible = false
                    }
                }
            }
        }
    }
}