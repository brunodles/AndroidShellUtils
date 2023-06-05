package com.brunodles.tablebuilder

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class TableBuilderTest {

    @Test
    internal fun whenBuild_withColumnNamesOnly_shouldInvokeFormat() {
        val format = mockk<Format>()
        every { format.headerCell(any()) } answers { " ${it.invocation.args[0]} "}
        every { format.headerLine(any()) } answers { it.invocation.args.joinToString("|")}
        every { format.isHeaderDividerRowEnabled } returns false
        val builder = TableBuilder(format)
            .columns {
                add("col1")
                add("col2")
                add("col3")
            }

        val irrelevant = builder.build()

        verify { format.headerCell("col1") }
        verify { format.headerCell("col2") }
        verify { format.headerCell("col3") }
        verify { format.headerLine(listOf(" col1 ", " col2 ", " col3 ")) }
        verify { format.isHeaderDividerRowEnabled }
    }

    @Test
    internal fun whenBuild_withColumnNames_andSingleLine_shouldInvokeFormat() {
        val format = mockk<Format>()
        every { format.headerCell(any()) } answers { " ${it.invocation.args[0]} "}
        every { format.headerLine(any()) } answers { it.invocation.args.joinToString("|")}
        every { format.cell(any()) } answers { " ${it.invocation.args[0]} "}
        every { format.cellLine(any()) } answers { it.invocation.args.joinToString("|")}
        every { format.isHeaderDividerRowEnabled } returns false
        val builder = TableBuilder(format)
            .columns {
                add("col1")
                add("col2")
                add("col3")
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }

        val irrelevant = builder.build()

        verify { format.cell("r1") }
        verify { format.cell("r2") }
        verify { format.cell("r3") }
        verify { format.cellLine(listOf(" r1 ", " r2 ", " r3 ")) }
    }

}
