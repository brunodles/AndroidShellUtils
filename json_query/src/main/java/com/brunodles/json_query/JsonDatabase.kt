package com.brunodles.json_query

import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

class JsonDatabase(
    val rootDir: File,
    val tables: List<JsonTable>,
) {

    val gson = Gson()

    fun query(query: String): String {
        val tableName: String = try {
            FROM_REGEX.find(query)!!.groupValues[1]
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Missing to inform from which 'table'. Example \"select * from my_table_x\".",
                e
            )
        }
        val table = tables.firstOrNull { it.name == tableName }
            ?: throw IllegalArgumentException("Table not found with given name \"$tableName\".")
        val allFiles = rootDir.walk()
            .filter(table.predicate)
            .toList()

        val conditions = try {
            CONDITIONS_REGEX.findAll(query)
                .map { it.groupValues[1] }
        } catch (e: Exception) {
            throw IllegalArgumentException("Something went wrong with the conditions.", e)
        }

        println("\n\nConditions:${conditions.joinToString("") { "\n\t$it" }}\n\n")

        val files = allFiles.map { file ->
            file to gson.fromJson(file.readText(), JsonObject::class.java)
        }.filter { (file, item) ->
            println(file.name)
            conditions
                .map { conditionStr ->
                    evaluate(item, conditionStr)
                }.find { !it } ?: true
        }.map { (file, item) -> file }


        return files.joinToString("\n") { it.name }
    }

    private fun evaluate(item: JsonObject, conditionStr: String): Boolean {
        val names = conditionStr.split(" ")
        println("\texp: $names")
        if (names.contains("==")) {
            println("\t\t${item.getAsJsonPrimitive(names[0]).asString} == ${names[2]}")
            return item.getAsJsonPrimitive(names[0]).asString == names[2]
        } else if (names.contains("contains")) {
            println("\t\t${item.get(names[0]).asString}.contains(${names[2]})")
            return item.getAsJsonArray(names[0]).asString.contains(names[2].unQuote())
        }
        return true
    }

    private fun String.unQuote(): String {
        return this.removeSurrounding("\"")
    }

    fun newQuery(block: QueryBuilder.() -> Unit): List<List<String>> {
        val queryBuilder = QueryBuilder(this)
        block(queryBuilder)
        return queryBuilder.execute()
    }

    companion object {
        private val FROM_REGEX = Regex("from (\\w++)")
        private val CONDITIONS_REGEX = Regex("(?:where|and) (.+?)(?:and|select|csv|tsv|\n)")

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
    }
}
