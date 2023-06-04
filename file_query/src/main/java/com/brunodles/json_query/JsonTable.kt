package com.brunodles.json_query

import java.io.File

class JsonTable(
    val name :String,
    val predicate : (File)-> Boolean = { true }
) {
}