package com.jlogical.spectrum.app

import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Holds style information for the components.
 */
class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
    }
}