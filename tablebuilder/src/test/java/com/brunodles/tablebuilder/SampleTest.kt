package com.brunodles.tablebuilder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SampleTest {

    @Test
    internal fun simple() {
        val result = TableBuilder(FormatDefault.simple)
            .columns {
                add("col1", ColumnDirection.left)
                add("col2", ColumnDirection.center)
                add("col3", ColumnDirection.right)
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }
            .newFooter{
                add("f1")
                add("f2")
                add("f3")
            }
            .build()

        val expected = """
            col1 | col2 | col3
            ---- | ---- | ----
            r1   |  r2  |   r3
            r1   |  r2  |   r3
            ---- | ---- | ----
            f1   |  f2  |   f3
            
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    internal fun markDown() {
        val result = TableBuilder(FormatDefault.markdown)
            .columns {
                add("col1", ColumnDirection.left)
                add("col2", ColumnDirection.center)
                add("col3", ColumnDirection.right)
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }
            .newFooter {
                add("f1")
                add("f2")
                add("f3")
            }
            .build()

        val expected = """
            | col1 | col2 | col3 |
            | :--- | :--: | ---: |
            | r1   |  r2  |   r3 |
            | r1   |  r2  |   r3 |
            | ---- | ---- | ---- |
            | f1   |  f2  |   f3 |
            
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    internal fun jira() {
        val result = TableBuilder(FormatDefault.jira)
            .columns {
                add("col1", ColumnDirection.left)
                add("col2", ColumnDirection.center)
                add("col3", ColumnDirection.right)
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }
            .build()

        val expected = """
            || col1 || col2 || col3 ||
            |  r1   |   r2  |    r3  |
            |  r1   |   r2  |    r3  |
            
        """.trimIndent()
        assertEquals(expected, result)
    }

}
