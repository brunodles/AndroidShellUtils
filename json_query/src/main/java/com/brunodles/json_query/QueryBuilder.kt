package com.brunodles.json_query

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.*

class QueryBuilder(
    private val jsonDatabase: JsonDatabase,
) {

    private var selectFunctions: List<SelectBuilder.(Element<*>) -> Element<*>> = listOf()
    private var tableName: String = ""
    private var whereFunction: (Element<*>) -> Boolean = { true }
    private val gson: Gson = jsonDatabase.gson

    fun select(vararg functions: SelectBuilder.(Element<*>) -> Element<*>): QueryBuilder {
        selectFunctions = functions.toList()
        return this
    }

    fun from(tableName: String): QueryBuilder {
        this.tableName = tableName
        return this
    }

    infix fun where(function: (Element<*>) -> Boolean): QueryBuilder {
        whereFunction = function
        return this
    }

    fun execute(): List<List<String>> {
        val selectBuilder = SelectBuilder()
        val table = jsonDatabase.tables.firstOrNull { it.name == tableName }
            ?: throw IllegalArgumentException("Table not found with given name \"$tableName\".")
        return jsonDatabase.rootDir
            .walk()
            .filter { it.isFile }
            .filter(table.predicate)
            .map { file ->
                file to Element(gson.fromJson(file.readText(), JsonObject::class.java))
            }.filter { (file, element) ->
                whereFunction(element)
            }.map { (file, element) ->
                selectFunctions.map { f -> f(selectBuilder, element).toString() }
            }
            .toList()
    }

    override fun toString(): String {
        return "QueryBuilder(selectFunctions=$selectFunctions, tableName='$tableName', whereFunction=$whereFunction)"
    }

    class SelectBuilder {

        fun dateFormat(element: Element<*>, format: String): Element<String> {
            val sdf = SimpleDateFormat(format)
            return Element(sdf.format(Date(element.asLong())))
        }
    }
}
