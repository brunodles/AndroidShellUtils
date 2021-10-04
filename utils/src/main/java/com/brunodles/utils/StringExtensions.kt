package com.brunodles.utils


/**
 * Creates a string using the specified [char] to complete the [length]
 *
 * @param char will be used to fill the string
 * @param length the wanted size
 * @return a new String with given size
 */
fun createString(char: Char, length: Int): String {
    val result = StringBuilder()
    for (i in 0 until length) {
        result.append(char)
    }
    return result.toString()
}

/**
 * Pads the string to the specified [length] at the start and end with the specified character or space.
 *
 * @param length the desired string length.
 * @param padChar the character to pad string with, if it has length less than the [length] specified. Space is used by default.
 * @return Returns a string of length at least [length] consisting of `this` string appended with [padChar] as many times
 * as are necessary to reach that length.
 */
fun String.center(length: Int, padChar: Char): String {
    var result = this
    var pos = false
    while (result.length < length) {
        result = if (pos)
            result + padChar
        else
            padChar + result
        pos = !pos
    }
    return result
}
