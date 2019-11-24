package com.jlogical.spectrum.app

import com.jlogical.spectrum.gui.ArrangedTab
import com.jlogical.spectrum.gui.DetailsTab
import com.jlogical.spectrum.gui.ImageTab
import com.jlogical.spectrum.gui.PaletteTab
import javafx.scene.control.TabPane
import tornadofx.*

class MainView : View("SpectrumFX") {

    override val root = tabpane{
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        prefWidth = 1100.0
        prefHeight = 640.0

        tab("Arranged"){
            this += ArrangedTab()
        }
        tab("Details"){
            this += DetailsTab()
        }
        tab("Image"){
            this += ImageTab()
        }
        tab("Palette"){
            this += PaletteTab()
        }
        tab("3D"){

        }
    }

    init{
        tabPane = root
    }

    companion object {

        private var tabPane: TabPane? = null

        /**
         * Switches the tab index to the one specified.
         */
        fun switchTab(index: Int){
            tabPane?.selectionModel?.select(index)
        }
    }
}