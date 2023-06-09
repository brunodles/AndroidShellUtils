package com.brunodles.file_query

import com.google.gson.Gson
import java.io.File

class JsonDatabase(
    val rootDir: File,
) {

    val gson = Gson()

    @JvmOverloads
    fun newQuery(
        debuggingBlock: ((Map<String, Int>) -> Unit)? = null,
        block: QueryBuilder.() -> Unit
    ): List<List<String>> {
        val queryBuilder = QueryBuilder(this)
        block(queryBuilder)
        debuggingBlock?.let { debugBlock ->
            val mapCounter = mutableMapOf<String, Int>()
            val result = queryBuilder.execute { counterName ->
                mapCounter.increase(counterName)
            }
            debugBlock(mapCounter)
            return result
        }
        return queryBuilder.execute()
    }

    companion object {
        private fun MutableMap<String, Int>.increase(key: String) {
            this[key] = (this[key] ?: 0) + 1
        }
    }
}
