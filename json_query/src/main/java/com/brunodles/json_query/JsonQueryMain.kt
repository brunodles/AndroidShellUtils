package com.brunodles.json_query

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object JsonQueryMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val database = JsonDatabase(
            rootDir = File("/Users/bruno.lima/workspace/scratch/working_area/tmp_sync/files/simplestore/logoutpreserved/metadata_storage_simple_store/metadata_storage_simple_store"),
            tables = listOf<JsonTable>(
                JsonTable("chunkMetadata") { it.name.startsWith("chunkMetadata") && !it.name.contains("root_key") },
                JsonTable("mediaSegment") { it.name.startsWith("mediaSegment") && !it.name.contains("root_key") },
                JsonTable("tripMetadata") { it.name.startsWith("tripMetadata") && !it.name.contains("root_key") },
            )
        )

//        val query = """
//            select chunkUUID, dateFormat(startTimeInMillis, "yyyy-MM-dd"), dateFormat(endTimeInMillis - startTimeInMillis, "HH:MM:ss")
//            from chunkMetadata
//            where chunkIndexOnSegment == 0
//            and tripUUIDs contains "0e75c598-7180-4345-869c-13414425ec1f"
//            """.trimIndent()
//        val query = """
//            select chunkUUID, dateFormat(startTimeInMillis, "yyyy-MM-dd"), dateFormat(endTimeInMillis - startTimeInMillis, "HH:MM:ss")
//            from chunkMetadata
//            where chunkIndexOnSegment == 0
//            and tripUUIDs contains "0e75c598-7180-4345-869c-13414425ec1f"
//            and dateStr(startTimeInMillis) == "2023-06-02"
//            """.trimIndent()

//        println(query)

//        println("\n\nresult:\n${database.query(query)}")
        println("result")
        println(
            database.newQuery {
                select(
                    { it["chunkUUID"] },
                    { dateFormat(it["startTimeInMillis"], "yyyy.MM.dd") },
                    { dateFormat(it["endTimeInMillis"] - it["startTimeInMillis"], "HH:MM:ss") },
                )
                from("chunkMetadata")
                where {
                    it["chunkIndexOnSegment"] eq 0
                            && it["tripUUIDs"] contains "0e75c598-7180-4345-869c-13414425ec1f"
                }
            }.joinToString("") { "\n${it.joinToString(", ") { it.removeSurrounding("\"") }}" }
        )
    }
}
