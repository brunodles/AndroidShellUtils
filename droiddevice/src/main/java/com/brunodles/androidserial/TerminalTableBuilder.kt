package com.brunodles.androidserial

import kotlin.math.max

class TerminalTableBuilder {
    private val columns = mutableListOf<ColumnData>()
    private val rows = mutableListOf<List<String>>()

    fun columns(columnsBlock: ColumnBlock.() -> Unit): TerminalTableBuilder {
        columnsBlock(ColumnBlockImpl(this))
        return this
    }

    fun newRow(rowBlock: RowBlock.() -> Unit): TerminalTableBuilder {
        val rowBlockImpl = RowBlockImpl()
        rowBlock(rowBlockImpl)
        rows.add(rowBlockImpl.cells)
        return this
    }

    fun build(): String {
        val sizes = buildSizes()
        val result = StringBuilder()
        columns.forEachIndexed { index, columnData ->
            result
                .append(' ')
                .append(columnData.name.withSize(sizes[index]))
                .append(" |")
        }
        result.append("\n")
        columns.forEachIndexed { index, _ ->
            result
                .append(' ')
                .append(createString('-', sizes[index] ?: 0))
                .append(" |")
        }
        result.append("\n")
        rows.forEach { cells ->
            cells.forEachIndexed { index, cell ->
                result
                    .append(' ')
                    .append(cell.withSize(sizes[index]))
                    .append(" |")
            }
            result.append("\n")
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
        return sizes
    }

    private class ColumnData(
        val name: String
    )

    interface ColumnBlock {
        fun add(name: String)
    }

    interface RowBlock {
        fun add(value: String)
        fun add(value: Any) = add(value.toString())
        fun skip()
    }

    private class ColumnBlockImpl(private val builder: TerminalTableBuilder) : ColumnBlock {
        override fun add(name: String) {
            builder.columns.add(
                ColumnData(
                    name = name
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
        private fun String.withSize(size: Int?): String = if (size == null || size == 0)
            ""
        else if (this.length > size)
            this.substring(0, size)
        else
            this.padEnd(size, ' ')

        private fun createString(c: Char, size: Int): String {
            val result = StringBuilder()
            for (i in 0 until size) {
                result.append(c)
            }
            return result.toString()
        }
    }
}