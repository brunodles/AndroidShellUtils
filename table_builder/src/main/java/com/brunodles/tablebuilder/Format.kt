package com.brunodles.tablebuilder

import com.brunodles.utils.createString

/**
 * Define a format of how the table data will be presented
 */
interface Format {

    val headerFormat : ContentFormat
    val headerDividerFormat : ContentFormat?
    val bodyFormat : ContentFormat
    val footerDivider : ContentFormat?
    val footerFormat : ContentFormat?

    interface ContentFormat {
        /**
         * How the whole line would be formatted
         * @param content is a list with all the cells pre-formatted on @function[cell]
         * @return a String for the given headers
         */
        fun line(content: List<String>): String

        /**
         * Format a "single cell"
         * @param content is a string containing content
         * @return a formatted string of the cell
         */
        fun cell(content: String, size: Int, direction: ColumnDirection): String
    }

    class SimpleContentFormat(
        private val separator: String
    ) : ContentFormat {
        override fun line(content: List<String>): String =
            content.joinToString(separator)
        override fun cell(content: String, size: Int, direction: ColumnDirection): String =
            content.withSize(size, direction)
    }
    class SimpleDividerFormat(
        private val separator: String,
        private val dividerChar : Char,
    ) : ContentFormat {
        override fun line(content: List<String>): String =
            content.joinToString(separator)
        override fun cell(content: String, size: Int, direction: ColumnDirection): String =
            createString(dividerChar, size)
    }
}