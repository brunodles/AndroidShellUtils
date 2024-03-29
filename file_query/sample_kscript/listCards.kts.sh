#!/usr/bin/env kscript
@file:DependsOn("com.brunodles.file_query:file_query:1.0.0")

import com.brunodles.file_query.JsonDatabase
import com.brunodles.file_query.ExtraFunctions.present
import com.brunodles.file_query.ExtraFunctions.println
import com.brunodles.tablebuilder.ColumnDirection.right
import java.io.File

object JsonQueryMain2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val database = JsonDatabase(
            rootDir = File("/home/bruno/workspace/android/endeavor/wallpaper/database/filesv4/_output/boards"),
        )

        database.newQuery {
            select(
                { field["key"] },
                { field["name"] },
                { field["imagesCount"] },
                { field["pinCount"] },
                { field["pins"].count() },
                { field["enabled"] },
                { field["lastChange"] },
                { file }
            )
            from { file.absolutePath.contains("boards") }
            where {
//                    field["enabled"] eq true
                field["name"] contains "card"
            }
        }.present {
            add("key")
            add("name")
            add("imagesCount", right)
            add("pinCount", right)
            add("realPinCount", right)
            add("enabled")
            add("lastChange")
            add("file")
        }.println()
    }
}

