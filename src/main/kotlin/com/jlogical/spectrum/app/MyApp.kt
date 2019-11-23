package com.jlogical.spectrum.app

import com.jlogical.spectrum.util.buildCSVMaps
import tornadofx.*

/**
 * Starts the application.
 */
fun main(args: Array<String>) {
    buildCSVMaps()
    launch<MyApp>(args)
}

/**
 * The container for the app.
 */
class MyApp: App(MainView::class, Styles::class)