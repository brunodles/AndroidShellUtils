package com.brunodles.file_query

import com.brunodles.file_query.ExtraFunctions.present
import com.brunodles.file_query.ExtraFunctions.println
import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File

class QueryBuilder(
    private val jsonDatabase: JsonDatabase,
) {

    private val gson: Gson = jsonDatabase.gson

    /** [toString] will be used to get the value from this return */
    private var selectFunction: (SelectFieldContext.() -> Unit)? = null

    /** filter to detect if the file matches a *table* */
    private var fromFilter: FileQueryContext.() -> Boolean = { true }

    /** filter to detect if the content */
    private var whereFunction: FileQueryContext.() -> Boolean = { true }

    /** prints the result */
    private var presentation: ((List<List<String>>) -> String)? = null

    fun select(function: SelectFieldContext.() -> Unit): QueryBuilder {
        selectFunction = function
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
            .mapNotNull { file ->
                val fileText = file.readText()
                GSON_CLASSES.firstNotNullOfOrNull { classType ->
                    try {
                        Element(gson.fromJson(fileText, classType))
                    } catch (_: Exception) {
                        null
                    }
                }?.let { element -> FileQueryContext(file, element) }
            }
            .filter { context -> fromFilter(context) && whereFunction(context) }
            .map { context ->
                val selectFieldContext = SelectFieldContext(context)
                selectFunction?.invoke(selectFieldContext)
                        ?: throw IllegalArgumentException("The 'select' is missing.")
                selectFieldContext.result
            }
            .toList()
        presentation?.let {
            result
                .let(it)
                .println()
        }
        return result
    }

    @JvmOverloads
    fun tablePresentation(
        format: FormatDefault = FormatDefault.simple,
        columnsBlock: TableBuilder.ColumnBlock.() -> Unit,
    ) {
        presentation = { data ->
            data.present(format, columnsBlock)
        }
    }

    data class FileQueryContext(
        val file: File,
        val field: Element<*>,
    ) {
        fun field(key: String): Element<*> = field[key]
    }

    data class SelectFieldContext(
        private val fileQueryContext: FileQueryContext
    ) {
        internal val result = mutableListOf<String>()
        private val field = fileQueryContext.field
        private val file = fileQueryContext.file

        @JvmOverloads
        fun field(key: String, function: Element<*>.() -> Any? = { this }) {
            add(function(field[key]))
        }

        @JvmOverloads
        fun file(function: File.() -> Any? = { this }) {
            add(function(file))
        }

        fun dateFormat(key: String, format: String) {
            add(ExtraFunctions.dateFormat(field[key], format))
        }

        fun func(function: FileQueryContext.() -> Any) {
            add(function(fileQueryContext))
        }

        fun add(content: Any?) {
            result += content?.toString() ?: "null"
        }
    }

    companion object {
        private val GSON_CLASSES = listOf(
            JsonObject::class.java,
            JsonArray::class.java,
            JsonPrimitive::class.java,
        )
    }
}
