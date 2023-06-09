package com.brunodles.tablebuilder

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

}