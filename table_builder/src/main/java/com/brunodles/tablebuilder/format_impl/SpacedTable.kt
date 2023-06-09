package com.brunodles.tablebuilder.format_impl

import com.brunodles.tablebuilder.content_format_impl.CharacterFilledDividerFormat
import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.content_format_impl.SpacedContentFormat

open class SpacedTable(
    separator: String = " | ",
    headerDividerFillingCharacter: Char = '-',
    footerDividerFillingCharacter: Char = '-',
) : Format {

    private val contentFormat: Format.ContentFormat = SpacedContentFormat(separator)

    override val headerFormat: Format.ContentFormat = contentFormat
    override val headerDividerFormat: Format.ContentFormat? =
        CharacterFilledDividerFormat(separator, headerDividerFillingCharacter)
    override val bodyFormat: Format.ContentFormat = contentFormat
    override val footerDivider: Format.ContentFormat? =
        CharacterFilledDividerFormat(separator, footerDividerFillingCharacter)
    override val footerFormat: Format.ContentFormat? = contentFormat
}