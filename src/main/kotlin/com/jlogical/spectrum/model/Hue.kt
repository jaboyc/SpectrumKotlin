package com.jlogical.spectrum.model

/**
 * A Hue is the 'color' of a MunsellColor.
 *
 * @param value the value of the hue from 0-99. A value of -1 means it is grayscale.
 */
data class Hue(val value: Double) {
    /**
     * Creates a Hue from a given hue prefix and value.
     */
    constructor(prefix: String, hueValue: Double) : this(getHueValue(prefix, hueValue))

    /**
     * Creates a Hue from a given hue string (such as 2.5R)
     */
    constructor(hueString: String) : this(getHueValue(hueString))

    /**
     * Initialization checks.
     */
    init {
        // Ensure value is valid.
        if ((!isGrayscale && value < 0) || value > 100) throw IllegalArgumentException("Hue value cannot be less than 0 or greater than 100! value = $value")
    }

    /**
     * The prefix of the hue.
     */
    val prefix: String
        get() = if (isGrayscale) "N" else if (value == 100.0) "RP" else huePrefixes[value.toInt() / 10]

    /**
     * The hue value (from 0-10) of the hue.
     */
    val hueValue: Double
        get () = if (isGrayscale) 0.0 else if (value == 100.0) 10.0 else value % 10

    /**
     * Whether the hue is grayscale or not.
     */
    val isGrayscale: Boolean
        get() = value == -1.0

    private companion object {

        /**
         * Array of valid hue prefixes.
         */
        val huePrefixes = listOf("R", "YR", "Y", "GY", "G", "BG", "B", "PB", "P", "RP", "N")

        /**
         * Returns the value from 0-99 with the given prefix and hueValue.
         */
        fun getHueValue(prefix: String, hueValue: Double): Double {

            // Special case for the prefix being "N".
            if (prefix == "N") return -1.0

            val index = huePrefixes.indexOf(prefix)
            if (index == -1) throw IllegalArgumentException("prefix '$prefix' is not valid for Hue.")

            return index * 10 + hueValue
        }

        /**
         * Returns the value from 0-99 with the given hue string (such as 2.5R)
         */
        fun getHueValue(hueString: String): Double {
            val hueValue = hueString.takeWhile { it.isDigit() || it == '.' }
            val prefix = hueString.takeLastWhile { it.isLetter() }

            return getHueValue(prefix, hueValue.toDouble())
        }
    }

    /**
     * String version of the hue.
     */
    override fun toString(): String {
        if (hueValue % 1.0 == 0.0) return "${hueValue.toInt()}$prefix"
        return "%.2f".format(hueValue) + prefix
    }
}