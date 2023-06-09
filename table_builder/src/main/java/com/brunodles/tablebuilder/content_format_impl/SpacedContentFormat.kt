package com.brunodles.tablebuilder.content_format_impl

import com.brunodles.tablebuilder.ColumnDirection
import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.withSize

class SpacedContentFormat(
    private val separator: String
) : Format.ContentFormat {
    override fun line(content: List<String>): String =
        content.joinToString(separator)
    override fun cell(content: String, size: Int, direction: ColumnDirection): String =
        content.withSize(size, direction)
}