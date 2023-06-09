package com.brunodles.tablebuilder.content_format_impl

import com.brunodles.tablebuilder.Format

class ContentFormatWithBorders(
    private val innerFormat: Format.ContentFormat,
    private val boarders: Pair<String, String> = Pair("|| ", " ||"),
) : Format.ContentFormat by innerFormat {
    override fun line(content: List<String>): String =
        boarders.first + innerFormat.line(content) + boarders.second

    companion object {
        fun Format.ContentFormat.withBorders(
            boarders: Pair<String, String> = Pair("|| ", " ||"),
        ): Format.ContentFormat =
            ContentFormatWithBorders(
                innerFormat = this,
                boarders = boarders,
            )
    }
}