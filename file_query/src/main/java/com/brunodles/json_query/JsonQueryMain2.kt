package com.brunodles.json_query

import com.brunodles.json_query.ExtraFunctions.present
import com.brunodles.tablebuilder.ColumnDirection.right
import java.io.File

object JsonQueryMain2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val database = JsonDatabase(
            rootDir = File("/home/bruno/workspace/android/endeavor/wallpaper/database/filesv4/_output/boards"),
        )

        println(
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
//                    it["enabled"] eq true
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
            }
        )
    }
}

