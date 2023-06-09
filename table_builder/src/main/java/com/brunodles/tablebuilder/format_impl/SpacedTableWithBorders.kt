package com.brunodles.tablebuilder.format_impl

import com.brunodles.tablebuilder.ColumnDirection
import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.withSize
import com.brunodles.utils.createString

open class SpacedTableWithBorders(
    private val headerBoarders: Triple<String, String, String> = Triple("|| ", " || ", " ||"),
    private val bodyBoarders: Triple<String, String, String> = Triple("|  ", " |  ", "  |"),
    private val footerBoarders: Triple<String, String, String> = Triple("|  ", " |  ", "  |"),
    private val footerDividerFillingCharacter: Char = '-',
) : Format {

    private val contentFormat: Format.ContentFormat = object : Format.ContentFormat {
        override fun line(content: List<String>): String =
            bodyBoarders.first + content.joinToString(bodyBoarders.second) + bodyBoarders.third

        override fun cell(content: String, size: Int, direction: ColumnDirection): String =
            content.withSize(size, direction)
    }

    override val headerFormat: Format.ContentFormat = object : Format.ContentFormat {
        override fun line(content: List<String>): String =
            headerBoarders.first + content.joinToString(headerBoarders.second) + headerBoarders.third

        override fun cell(content: String, size: Int, direction: ColumnDirection): String =
            content.withSize(size, direction)
    }
    override val headerDividerFormat: Format.ContentFormat? = null
    override val bodyFormat: Format.ContentFormat = contentFormat
    override val footerDivider: Format.ContentFormat? = object : Format.ContentFormat {
        override fun line(content: List<String>): String =
            footerBoarders.first + content.joinToString(footerBoarders.second) + footerBoarders.third

        override fun cell(content: String, size: Int, direction: ColumnDirection): String =
            createString(footerDividerFillingCharacter, size)
    }
    override val footerFormat: Format.ContentFormat? = contentFormat
}