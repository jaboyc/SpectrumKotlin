package com.jlogical.spectrum.model

import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

/**
 * A singleton for the current colors used for mixing. This can only hold 8 colors maximum.
 */
object Mixer {

    /**
     * The colors currently in the palette.
     */
    val colors = mutableListOf<MunsellColor>().observable()

    /**
     * The desired color to achieve while mixing.
     */
    val desiredColor = SimpleObjectProperty<MunsellColor?>()

    /**
     * Adds the given munsell color to the palette.
     */
    fun addColor(color: MunsellColor) {
        colors.add(0, color)

        // Ensure that there is a max of 8 colors.
        if (Palette.colors.size > 8)
            Palette.colors.removeAt(8)
    }

    /**
     * Removes the given color from the palette.
     */
    fun removeColor(color: MunsellColor) {
        colors.remove(color)
    }

    /**
     * Returns whether the Palette contains the given munsell color.
     */
    fun contains(color: MunsellColor): Boolean {
        return colors.contains(color)
    }

    /**
     * Returns the mixing of all the colors in the mixer.
     */
    fun mix(): MunsellColor? {

        // If there are no colors in the mixer, return nothing.
        if (colors.isEmpty()) return null

        // Return the mixing of all the colors with the weight of 1.
        return MunsellColor.mix(colors, colors.map { 1.0 })
    }

    /**
     * Finds the mixing weights for the desired color from the palette.
     */
    fun findMixingWeights(): List<Double> {

        // Convert the desired color and current palette colors to their rgb corresponding colors.
        val rgbDesired = desiredColor.value?.color ?: throw NullPointerException()
        val rgbColors = Palette.colors.map { it.color }

        // Lists used in finding the weights to get the desired color.
        var currWeights = MutableList(Palette.colors.size) { 0.0 }
        var bestWeights: MutableList<Double>? = null

        // The best distance found so far.
        var bestDist = Double.MAX_VALUE

        // List of the index each color is at in the coarse weights.
        var colorIndices = MutableList(Palette.colors.size) { 0 }

        // List of coarse weights to try for each color at first.
        val coarseWeights = listOf(0.0, 2.0, 5.0, 8.0, 14.0)

        // Loop through and find the best coarse weights.
        loop@ while (true) {

            // Adjust the index to go to the next test.
            for (i in (colorIndices.size - 1) downTo 0) {

                // If this index is at its highest, set it back to zero and continue the loop.
                if (colorIndices[i] == coarseWeights.size - 1) {

                    // If this is the last index (meaning all previous indices have been the max
                    // value), break the loop.
                    if (i == 0)
                        break@loop

                    colorIndices[i] = 0
                    currWeights[i] = coarseWeights[0]
                } else {
                    colorIndices[i] += 1
                    currWeights[i] = coarseWeights[colorIndices[i]]
                    break
                }
            }

            // Mix the colors with the given rgb colors.
            val mix = MunsellColor.mixRGB(rgbColors, currWeights) ?: continue

            // Get the current distance.
            val dist = rgbDesired.diff(mix)

            // If a better combination of weights was found, replace bestWeights with it.
            if (dist < bestDist) {
                bestDist = dist
                bestWeights = mutableListOf(*currWeights.toTypedArray())
            }
        }

        // Fine-tune the results by looking at 4 similar weights for each weight.
        val weightMatrix = MutableList(Palette.colors.size) { List(5) { -1.0 } }

        // Find the values for the weight matrix by looking at the best weights.
        bestWeights?.forEachIndexed { i, weight ->
            if (weight == 0.0) {
                weightMatrix[i] = listOf(0.0, 0.2, 0.5, 1.0, 2.0)
            } else {
                weightMatrix[i] = listOf(-2, -1, 0, 1, 2).map {
                    weight + it
                }
            }
        }

        // Repeat the loop with the fine-tuned weights.
        loop@ while (true) {

            // Adjust the index to go to the next test.
            for (i in (colorIndices.size - 1) downTo 0) {

                // If this index is at its highest, set it back to zero and continue the loop.
                if (colorIndices[i] == weightMatrix[i].size - 1) {

                    // If this is the last index (meaning all previous indices have been the max
                    // value), break the loop.
                    if (i == 0)
                        break@loop

                    colorIndices[i] = 0
                    currWeights[i] = weightMatrix[i][0]
                } else {
                    colorIndices[i] += 1
                    currWeights[i] = weightMatrix[i][colorIndices[i]]
                    break
                }
            }

            // Mix the colors with the given rgb colors.
            val mix = MunsellColor.mixRGB(rgbColors, currWeights) ?: continue

            // Get the current distance.
            val dist = rgbDesired.diff(mix)

            // If a better combination of weights was found, replace bestWeights with it.
            if (dist < bestDist) {
                bestDist = dist
                bestWeights = mutableListOf(*currWeights.toTypedArray())
            }
        }

        return bestWeights ?: throw IllegalArgumentException("Invalid colors passed in.")
    }
}