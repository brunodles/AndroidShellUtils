package com.brunodles.json_query

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

class QueryBuilder(
    private val jsonDatabase: JsonDatabase,
) {

    /** [toString] will be used to get the value from this return */
    private var selectFunctions: List<FileQueryContext.() -> Any?> = listOf()

    private var fromFilter: FileQueryContext.() -> Boolean = { true }
    private var whereFunction: FileQueryContext.() -> Boolean = { true }
    private val gson: Gson = jsonDatabase.gson

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
        return jsonDatabase.rootDir
            .walk()
            .filter { it.isFile }
            .map { file -> FileQueryContext(file, Element(gson.fromJson(file.readText(), JsonObject::class.java))) }
            .filter { context -> fromFilter(context) && whereFunction(context) }
            .map { context -> selectFunctions.map { f -> f(context)?.toString() ?: "null" } }
            .toList()
    }

    data class FileQueryContext(
        val file: File,
        val field: Element<*>,
    ) {
    }

}
