package com.brunodles.tablebuilder

/**
 * Define a format of how the table data will be presented
 */
interface Format {
    /**
     * How the headers should be presented.
     * @param content is a list with all the headers pre-formatted on @function[headerCell]
     * @return a String for the given headers
     */
    fun headerLine(content: List<String>): String

    /**
     * Format a "single cell" of the header
     * @param content is a string containing the column name
     * @return a formatted string for the column
     */
    fun headerCell(content: String): String

    /**
     * How the dividers should be presented
     * @param sizes is a list with all the dividers of the headers, pre-formatted on @function[headerDivider]
     * @return a String for all dividers
     */
    fun dividers(sizes: List<String>): String

    /**
     * How a "single cell" divider should be presented
     * @param size the size of the divider
     * @param direction where the text should go
     * @return the formatted divider
     */
    fun headerDivider(size: Int, direction: ColumnDirection): String

    /**
     * Creates a line, representing how a combination of cells should be presented
     * @param content is a list cells for a given line, pre-formatted on @function[cell]
     * @return a String for the given line
     */
    fun cellLine(content: List<String>): String

    /**
     * Format individual cell
     * @param content is the content of the cell
     * @return a formatted string for a cell
     */
    fun cell(content: String): String

    /**
     * How a "single cell" divider should be presented
     * @param size the size of the divider
     * @return the formatted divider
     */
    fun divider(size: Int): String

    /**
     * Is header divider enabled in this format?
     */
    val isHeaderDividerRowEnabled: Boolean
}