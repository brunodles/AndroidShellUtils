package com.brunodles.file_query

import com.brunodles.tablebuilder.ColumnDirection.right
import com.brunodles.tablebuilder.FormatDefault
import java.io.File

object JsonQueryMain2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val database = JsonDatabase(
            rootDir = File("/home/bruno/workspace/android/endeavor/wallpaper/database/filesv4/_output/boards"),
        )

        database.newQuery {
            select {
                field("key")
                field("name")
                field("imagesCount")
                field("pinCount")
                field("pins") { count() }
                field("enabled")
                field("lastChange")
                file()
            }
            from { file.absolutePath.contains("boards") }
            where {
                field["name"] contains "card"
            }
            tablePresentation(format = FormatDefault.markdown) {
                add("key")
                add("name")
                add("imagesCount", right)
                add("pinCount", right)
                add("realPinCount", right)
                add("enabled")
                add("lastChange")
                add("file")
            }
        }
    }
}

