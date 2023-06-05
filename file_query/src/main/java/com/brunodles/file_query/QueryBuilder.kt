package com.brunodles.file_query

import com.brunodles.file_query.ExtraFunctions.present
import com.brunodles.file_query.ExtraFunctions.println
import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

class QueryBuilder(
    private val jsonDatabase: JsonDatabase,
) {

    private val gson: Gson = jsonDatabase.gson

    /** [toString] will be used to get the value from this return */
    private var selectFunctions: List<FileQueryContext.() -> Any?> = listOf()

    private var fromFilter: FileQueryContext.() -> Boolean = { true }
    private var whereFunction: FileQueryContext.() -> Boolean = { true }
    private var presentation: ((List<List<String>>) -> String)? = null

    fun select(vararg functions: FileQueryContext.() -> Any?): QueryBuilder {
        selectFunctions = functions.toList()
        return this
    }

    fun from(function: FileQueryContext.() -> Boolean): QueryBuilder {
        fromFilter = function
        return this
    }

    infix fun where(function: FileQueryContext.() -> Boolean): QueryBuilder {
        whereFunction = function
        return this
    }

    fun execute(): List<List<String>> {
        val result = jsonDatabase.rootDir
            .walk()
            .filter { it.isFile }
            .map { file -> FileQueryContext(file, Element(gson.fromJson(file.readText(), JsonObject::class.java))) }
            .filter { context -> fromFilter(context) && whereFunction(context) }
            .map { context -> selectFunctions.map { f -> f(context)?.toString() ?: "null" } }
            .toList()
        presentation?.let {
            result
                .let(it)
                .println()
        }
        return result
    }

    fun tablePresentation(
        format: FormatDefault = FormatDefault.simple,
        columnsBlock: TableBuilder.ColumnBlock.() -> Unit,
    ) {
        presentation = {data ->
            data.present(format, columnsBlock)

        }
    }

    data class FileQueryContext(
        val file: File,
        val field: Element<*>,
    ) {
    }
}
