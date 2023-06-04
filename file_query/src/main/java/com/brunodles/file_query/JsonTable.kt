package com.brunodles.file_query

import java.io.File

class JsonTable(
    val name :String,
    val predicate : (File)-> Boolean = { true }
) {
}