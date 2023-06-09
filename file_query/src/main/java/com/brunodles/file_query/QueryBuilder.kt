package com.brunodles.file_query

import com.brunodles.file_query.ExtraFunctions.present
import com.brunodles.file_query.ExtraFunctions.println
import com.brunodles.tablebuilder.ColumnDirection
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
    private var fromFilterFunction: FileQueryContext.() -> Boolean = { true }

    /** filter to detect if the content */
    private var whereFunction: FileQueryContext.() -> Boolean = { true }

    /** prints the result */
    private var presentationFunction: ((List<List<String>>) -> String)? = null

    fun select(function: SelectFieldContext.() -> Unit): QueryBuilder {
        selectFunction = function
        return this
    }

    fun from(function: FileQueryContext.() -> Boolean): QueryBuilder {
        fromFilterFunction = function
        return this
    }

    infix fun where(function: FileQueryContext.() -> Boolean): QueryBuilder {
        whereFunction = function
        return this
    }

    fun execute(
        debuggingBlock: (String) -> Unit = {}
    ): List<List<String>> {
        val indexNameSuggestion = mutableMapOf<Int,Pair<String, ColumnDirection>>()
        val result = jsonDatabase.rootDir
            .walk()
            .filter { it.isFile }
            .onEach { debuggingBlock("files") }
            .mapNotNull { file ->
                val fileText = file.readText()
                GSON_CLASSES.firstNotNullOfOrNull { classType ->
                    try {
                        Element(gson.fromJson(fileText, classType))
                    } catch (_: Exception) {
                        null
                    }
                }?.let { element ->
                    debuggingBlock(". json")
                    FileQueryContext(file, element, FileType.gson)
                }
            }
            .filter { context -> fromFilterFunction(context)}
            .onEach { debuggingBlock("from") }
            .filter { context -> whereFunction(context) }
            .onEach { debuggingBlock("where") }
            .map { context ->
                val selectFieldContext = SelectFieldContext(context) { index, name, direction -> indexNameSuggestion[index] = name to direction}
                selectFunction?.invoke(selectFieldContext)
                        ?: throw IllegalArgumentException("The 'select' is missing.")
                selectFieldContext.result
            }
            .onEach { debuggingBlock("select") }
            .toList()
        (presentationFunction ?: { data ->
            data.present(format = FormatDefault.simple) {
                indexNameSuggestion.toSortedMap().forEach { (_, data) ->
                    add(data.first, data.second)
                }
            }
        }).let { function->
            result
                .let(function)
                .println()
        }
        return result
    }

    @JvmOverloads
    fun tablePresentation(
        format: FormatDefault = FormatDefault.simple,
        columnsBlock: TableBuilder.ColumnBlock.() -> Unit,
    ) {
        presentationFunction = { data ->
            data.present(format, columnsBlock)
        }
    }

    data class FileQueryContext(
        val file: File,
        val field: Element<*>,
        val fileType: FileType,
    ) {
        fun field(key: String): Element<*> = field[key]
    }

    data class SelectFieldContext(
        private val fileQueryContext: FileQueryContext,
        private val fieldNameCallback : (Int, String, ColumnDirection) -> Unit,
    ) {
        private var currentFieldIndex = 0
        internal val result = mutableListOf<String>()
        private val field = fileQueryContext.field
        private val file = fileQueryContext.file

        @JvmOverloads
        fun field(key: String, function: Element<*>.() -> Any? = { this }, named:String = key) {
            add(named, function(field[key]))
        }

        @JvmOverloads
        fun file(named: String = "file", function: File.() -> Any? = { this }) {
            add(named,function(file))
        }
        fun file(function: File.() -> Any? = { this }, named: String) {
            add(named,function(file))
        }

        @JvmOverloads
        fun dateFormat(key: String, format: String, named:String = key) {
            add(named, ExtraFunctions.dateFormat(field[key], format))
        }

        @JvmOverloads
        fun func(named: String = "func", function: FileQueryContext.() -> Any) {
            add(named, function(fileQueryContext))
        }
        fun func(function: FileQueryContext.() -> Any, named: String) {
            add(named, function(fileQueryContext))
        }

        private fun add(fieldName:String, content: Any?) {
            val direction = when {
                content is Number -> ColumnDirection.right
                content is Element<*> && content.isNumber() -> ColumnDirection.right
                else -> ColumnDirection.left
            }
            fieldNameCallback(currentFieldIndex++, fieldName, direction)
            result += content?.toString() ?: "null"
        }
    }

    enum class FileType {
        gson,
        csv,
        ;
    }

    companion object {
        private val GSON_CLASSES = listOf(
            JsonObject::class.java,
            JsonArray::class.java,
            JsonPrimitive::class.java,
        )
    }
}
