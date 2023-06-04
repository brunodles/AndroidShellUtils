package com.brunodles.json_query

import com.brunodles.json_query.JsonDatabase.Companion.present
import com.brunodles.tablebuilder.ColumnDirection.right
import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import java.io.File

object JsonQueryMain2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val database = JsonDatabase(
            rootDir = File("/home/bruno/workspace/android/endeavor/wallpaper/database/filesv4/_output/boards"),
            tables = listOf<JsonTable>(
                JsonTable("boards"),
            )
        )

        println(
            database.newQuery {
                select(
                    { it["key"] },
                    { it["name"] },
                    { it["imagesCount"] },
                    { it["pinCount"] },
                    { it["pins"].count() },
                    { it["enabled"] },
                    { it["lastChange"] },
                )
                from("boards")
                where {
//                    it["enabled"] eq true
                    it["name"] contains "card"
                }
            }.present {
                add("key")
                add("name")
                add("imagesCount", right)
                add("pinCount", right)
                add("realPinCount", right)
                add("enabled")
                add("lastChange")
            }
        )
    }
}

