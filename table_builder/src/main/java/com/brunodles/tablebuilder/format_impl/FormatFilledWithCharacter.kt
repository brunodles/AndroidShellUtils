package com.brunodles.tablebuilder.format_impl

import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.content_format_impl.ContentFormatFilledWithCharacter.Companion.filledWithChar

open class FormatFilledWithCharacter(
    innerFormat: Format,
    fillingChar: Char = ' ',
) : Format {
    override val headerFormat: Format.ContentFormat =
        innerFormat.headerFormat.filledWithChar(fillingChar)
    override val headerDividerFormat: Format.ContentFormat? =
        innerFormat.headerDividerFormat?.filledWithChar(fillingChar)
    override val bodyFormat: Format.ContentFormat =
        innerFormat.bodyFormat.filledWithChar(fillingChar)
    override val footerDivider: Format.ContentFormat? =
        innerFormat.footerDivider?.filledWithChar(fillingChar)
    override val footerFormat: Format.ContentFormat? =
        innerFormat.footerFormat?.filledWithChar(fillingChar)

    companion object {
        fun Format.filledWithSpace(): Format =
            filledWithChar(' ')

        fun Format.filledWithChar(fillingChar: Char): Format =
            FormatFilledWithCharacter(this, fillingChar)
    }
}