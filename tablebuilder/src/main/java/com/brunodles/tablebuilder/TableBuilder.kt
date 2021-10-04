package com.brunodles.tablebuilder

import kotlin.math.max

class TableBuilder(private val tableFormat: Format = FormatDefault.simple) {
    private val columns = mutableListOf<ColumnData>()
    private val rows = mutableListOf<List<String>>()
    private val footer = mutableListOf<List<String>>()

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
        footer.add(rowBlockImpl.cells)
        return this
    }

    fun build(): String {
        val sizes = buildSizes()
        val result = StringBuilder()
        // headers
        result.append(tableFormat.headerLine(
            columns.mapIndexed { index, columnData ->
                tableFormat.headerCell(columnData.name).withSize(sizes[index], columnData.columnDirection)
            }
        )).append("\n")
        // dividers
        if (tableFormat.isHeaderDividerRowEnabled)
            result.append(
                tableFormat.dividers(
                    columns.mapIndexed { index, columnData ->
                        tableFormat.headerDivider(sizes[index] ?: 0, columnData.columnDirection)
                    }
                )).append("\n")
        // rows
        rows.forEach { row ->
            result.append(
                tableFormat.cellLine(
                    row.mapIndexedNotNull { index, cell ->
                        try {
                            tableFormat.cell(cell).withSize(sizes[index], columns[index].columnDirection)
                        } catch (e: Exception) {
                            null
                        }
                    }
                )
            ).append("\n")
        }
        // footers
        if (footer.isNotEmpty())
            result.append(
                tableFormat.dividers(
                    columns.mapIndexed { index, _ ->
                        tableFormat.divider(sizes[index] ?: 0)
                    }
                )).append("\n")
        footer.forEach { row ->
            result.append(
                tableFormat.headerLine(
                    row.mapIndexedNotNull { index, cell ->
                        try {
                            tableFormat.headerCell(cell).withSize(sizes[index], columns[index].columnDirection)
                        } catch (e: Exception) {
                            null
                        }
                    }
                )
            ).append("\n")
        }
        return result.toString()
    }

    private fun buildSizes(): MutableMap<Int, Int> {
        val sizes = mutableMapOf<Int, Int>()
        columns.forEachIndexed { index, columnData ->
            sizes[index] = columnData.name.length
        }
        rows.forEach { cells ->
            cells.forEachIndexed { index, cell ->
                sizes[index] = max(sizes[index] ?: 0, cell.length)
            }
        }
        footer.forEach { cells ->
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
        fun add(name: String, direction: ColumnDirection = ColumnDirection.left)
    }

    interface RowBlock {
        fun add(value: String)
        fun add(value: Any?) = add(value.toString())
        fun skip()
    }

    private class ColumnBlockImpl(private val builder: TableBuilder) : ColumnBlock {
        override fun add(name: String, direction: ColumnDirection) {
            builder.columns.add(
                ColumnData(
                    name = name,
                    columnDirection = direction
                )
            )
        }
    }

    private class RowBlockImpl : RowBlock {
        val cells = mutableListOf<String>()
        override fun add(text: String) {
            cells.add(text)
        }

        override fun skip() {
            cells.add("")
        }
    }

    companion object {
    }
}