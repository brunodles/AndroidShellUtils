package com.brunodles.file_query

import com.google.gson.Gson
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
