package com.brunodles.tablebuilder.format_impl

import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.content_format_impl.CharacterFilledDividerFormat
import com.brunodles.tablebuilder.content_format_impl.CharacterSeparatedContentFormat
import com.brunodles.tablebuilder.content_format_impl.FixedCellContentFormat

open class CharacterSeparatedValues(
    private val headerSeparator: String = ",",
    private val bodySeparator: String = ",",
    private val headerDividerSeparator: String? = null,
    private val headerDividerFillingChar: Char = '-',
    private val footerDividerSeparator: String = bodySeparator,
    private val footerDividerCellContent: String? = null,
    private val footerDividerFillingChar: Char? = null
) : Format {
    private val contentFormat = CharacterSeparatedContentFormat(bodySeparator)
    override val headerFormat: Format.ContentFormat = CharacterSeparatedContentFormat(headerSeparator)
    override val headerDividerFormat: Format.ContentFormat? = headerDividerSeparator?.let { separator ->
        CharacterFilledDividerFormat(separator, headerDividerFillingChar)
    }
    override val bodyFormat: Format.ContentFormat = contentFormat
    override val footerDivider: Format.ContentFormat? = footerDividerCellContent?.let { cellContent ->
        FixedCellContentFormat(footerDividerSeparator, cellContent)
    } ?: footerDividerFillingChar?.let { fillingChar ->
        CharacterFilledDividerFormat(footerDividerSeparator, fillingChar)
    }
    override val footerFormat: Format.ContentFormat? = contentFormat
}