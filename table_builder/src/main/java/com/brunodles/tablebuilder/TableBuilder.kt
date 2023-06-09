package com.brunodles.tablebuilder

import kotlin.math.max

class TableBuilder
@JvmOverloads
constructor(
        private val tableFormat: Format = FormatDefault.simple
) {
    private val headers = mutableListOf<ColumnData>()
    private val rows = mutableListOf<List<String>>()
    private val footers = mutableListOf<List<String>>()

    /** Set up the Headers for the table. Same method as [columns] */
    fun headers(columnsBlock: ColumnBlock.() -> Unit): TableBuilder = columns(columnsBlock)

    /** Set up the Headers for the table. Same method as [headers] */
    fun columns(columnsBlock: ColumnBlock.() -> Unit): TableBuilder {
        columnsBlock(ColumnBlockImpl(this))
        return this
    }

    fun newRow(rowBlock: RowBlock.() -> Unit): TableBuilder {
        val rowBlockImpl = RowBlockImpl()
        rowBlock(rowBlockImpl)
        rows.add(rowBlockImpl.cells)
        return this
    }

    fun newFooter(rowBlock: RowBlock.() -> Unit): TableBuilder {
        val rowBlockImpl = RowBlockImpl()
        rowBlock(rowBlockImpl)
        footers.add(rowBlockImpl.cells)
        return this
    }

    fun build(): String {
        val sizes = buildSizes()
        val result = StringBuilder()
        // headers
        result.appendln(tableFormat.headerFormat.line(
            headers.mapIndexed { index, columnData ->
                tableFormat.headerFormat.cell(columnData.name, sizes[index]?:0, columnData.columnDirection)
            }
        ))
        // dividers
        tableFormat.headerDividerFormat?.let { format->
            format.line(
                headers.mapIndexed { index, columnData ->
                    format.cell("", sizes[index] ?: 0, columnData.columnDirection)
                }
            )
        }?.let { result.appendln(it) }
        // rows
        rows.forEach { row ->
            result.appendln(
                tableFormat.bodyFormat.line(
                    row.mapIndexedNotNull { index, cell ->
                        try {
                            tableFormat.bodyFormat.cell(cell, sizes[index]?:0, headers[index].columnDirection )
                        } catch (e: Exception) {
                            null
                        }
                    }
                )
            )
        }
        // footers
        if (footers.isNotEmpty()) {
            tableFormat.footerDivider?.let { format ->
                format.line(
                    headers.mapIndexed { index, _ ->
                        format.cell("", sizes[index] ?: 0, headers[index].columnDirection)
                    }
                )
            }?.let { result.appendln(it) }
            val footerFormat = tableFormat.footerFormat ?: tableFormat.bodyFormat
            footers.forEach { row ->
                result.appendln(
                    footerFormat.line(
                        row.mapIndexedNotNull { index, cell ->
                            try {
                                footerFormat.cell(cell, sizes[index] ?: 0, headers[index].columnDirection)
                            } catch (e: Exception) {
                                null
                            }
                        }
                    )
                )
            }
        }
        return result.toString()
    }

    private fun buildSizes(): MutableMap<Int, Int> {
        val sizes = mutableMapOf<Int, Int>()
        headers.forEachIndexed { index, columnData ->
            sizes[index] = columnData.name.length
        }
        headers.mapIndexed { index, columnData ->
            tableFormat.headerDividerFormat?.let { format ->
                val headerDivider = format.cell("",sizes[index] ?: 0, columnData.columnDirection)
                sizes[index] = max(sizes[index] ?: 0, headerDivider.length)
            }
        }
        rows.forEach { cells ->
            cells.forEachIndexed { index, cell ->
                sizes[index] = max(sizes[index] ?: 0, cell.length)
            }
        }
        footers.forEach { cells ->
            cells.forEachIndexed { index, cell ->
                sizes[index] = max(sizes[index] ?: 0, cell.length)
            }
        }
        return sizes
    }

    private class ColumnData(
        val name: String,
        val columnDirection: ColumnDirection
    )

    interface ColumnBlock {
        fun add(name: String) = add(name, ColumnDirection.left)
        fun add(name: String, direction: ColumnDirection = ColumnDirection.left)
    }

    interface RowBlock {
        fun add(value: String)
        fun add(value: Any?) = add(value.toString())
        fun skip()
    }

    private class ColumnBlockImpl(private val builder: TableBuilder) : ColumnBlock {

        override fun add(name: String, direction: ColumnDirection) {
            builder.headers.add(
                ColumnData(
                        name = name,
                        columnDirection = direction
                )
            )
        }
    }

    private class RowBlockImpl : RowBlock {
        val cells = mutableListOf<String>()
        override fun add(value: String) {
            cells.add(value)
        }

        override fun skip() {
            cells.add("")
        }
    }

    companion object {
        private fun StringBuilder.appendln(string: String): StringBuilder =
            this.append(string).append("\n")
    }
}