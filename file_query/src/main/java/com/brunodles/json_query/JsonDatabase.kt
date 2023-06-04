package com.brunodles.json_query

import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

class JsonDatabase(
    val rootDir: File,
) {

    val gson = Gson()

    fun newQuery(block: QueryBuilder.() -> Unit): List<List<String>> {
        val queryBuilder = QueryBuilder(this)
        block(queryBuilder)
        return queryBuilder.execute()
    }
}
