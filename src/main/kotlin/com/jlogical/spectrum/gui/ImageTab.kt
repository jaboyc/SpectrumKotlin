package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.MunsellColor
import com.jlogical.spectrum.model.Palette
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
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
     * Stores the image used for posterizing and analyzing.
     */
    private val imageProperty = SimpleObjectProperty<Image>()
    private var image: Image? by imageProperty

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
                    openImage()
                }
            }

            buttonBar = hbox(4.0) {
                isVisible = false

                button("Posterize") {
                    action {
                        posterizeImage()
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

    /**
     * Posterizes the current image with the colors in the Palette.
     */
    private fun posterizeImage() {

        // Make sure there are colors in the Palette.
        if (Palette.colors.isEmpty()) {
            Alert(Alert.AlertType.WARNING, "No color's specified in Palette. Right click on a color to add it to the palette.").showAndWait()
            return
        }

        val safeImage = image ?: throw NullPointerException("No image has been loaded")
        val writableImage = WritableImage(safeImage.pixelReader, safeImage.width.toInt(), safeImage.height.toInt())
        for (x in 0 until safeImage.width.toInt()) {
            for (y in 0 until safeImage.height.toInt()) {
                writableImage.pixelWriter.setColor(x, y, Palette.closestColor(MunsellColor.fromRGB(safeImage.pixelReader.getColor(x, y)))?.color)
            }
        }

        image = writableImage
    }

    /**
     * Opens an image and displays it.
     */
    private fun openImage() {
        val file = chooseFile("Open Image", arrayOf(FileChooser.ExtensionFilter("Images (*.png, *.jpg)", "*.png", "*.jpg")), FileChooserMode.Single, null)
        if (file.isNotEmpty()) {
            image = Image(file[0].toURI().toString(), 450.0, 400.0, true, true)
            center = VBox(5.0).apply {
                alignment = Pos.CENTER

                this += PaletteBar()
                imageview(imageProperty).apply {
                    setOnMousePressed {
                        showMunsellBlock(it)
                    }
                }
            }
            buttonBar.isVisible = true
        }
    }

    /**
     * Displays a MunsellBlock on the bottom of the page with the clicked pixel.
     */
    private fun showMunsellBlock(me: MouseEvent) {
        bottom = HBox().apply {
            alignment = Pos.CENTER
            paddingAll = 4.0

            this += MunsellColorBlock(MunsellColor.fromRGB(image!!.pixelReader.getColor(me.x.toInt(), me.y.toInt()))).apply {
                setOnMousePressed { e ->
                    if (e.isPrimaryButtonDown && e.clickCount == 2)
                        DetailsTab.showColorDetails(this.color)
                }
            }
        }
    }
}