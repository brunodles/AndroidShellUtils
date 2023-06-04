package com.brunodles.json_query

import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import java.text.SimpleDateFormat
import java.util.*

object ExtraFunctions {

    fun dateFormat(element: Element<*>, format: String): Element<String> {
        val sdf = SimpleDateFormat(format)
        return Element(sdf.format(Date(element.asLong())))
    }

    fun List<List<String>>.present(
        format: FormatDefault = FormatDefault.simple,
        columnsBlock: TableBuilder.ColumnBlock.() -> Unit,
    ): String {
        val tableBuilder = TableBuilder(tableFormat = format)
            .columns(columnsBlock)

        this.forEach { row ->
            tableBuilder.newRow {
                row.forEach { cell ->
                    add(cell.removeSurrounding("\""))
                }
            }
        }

        return tableBuilder.build()
    }

    fun String.println() {
        println(this)
    }
}
