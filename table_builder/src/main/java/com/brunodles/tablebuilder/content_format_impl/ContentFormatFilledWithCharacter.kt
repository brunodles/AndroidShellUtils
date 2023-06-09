package com.brunodles.tablebuilder.content_format_impl

import com.brunodles.tablebuilder.ColumnDirection
import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.withSize

class ContentFormatFilledWithCharacter(
    private val innerFormat: Format.ContentFormat,
    private val fillingChar: Char
) : Format.ContentFormat by innerFormat{
    override fun cell(content: String, size: Int, direction: ColumnDirection): String =
        innerFormat.cell(content, size, direction)
            .withSize(size, direction, fillingChar)

    companion object {
        fun Format.ContentFormat.filledWithChar(fillingChar: Char): Format.ContentFormat =
            ContentFormatFilledWithCharacter(this, fillingChar)
    }
}