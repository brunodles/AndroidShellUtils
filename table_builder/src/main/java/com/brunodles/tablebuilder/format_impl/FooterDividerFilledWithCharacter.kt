package com.brunodles.tablebuilder.format_impl

import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.content_format_impl.ContentFormatFilledWithCharacter.Companion.filledWithChar
import com.brunodles.tablebuilder.format_impl.FormatFilledWithCharacter.Companion.filledWithChar
import com.brunodles.tablebuilder.format_impl.FormatFilledWithCharacter.Companion.filledWithSpace

open class FooterDividerFilledWithCharacter(
    innerFormat: Format,
    fillingCharacter: Char,
) : Format by innerFormat {
    override val footerDivider: Format.ContentFormat? =
        innerFormat.bodyFormat.filledWithChar(fillingCharacter)

    companion object {
        fun Format.footerDividerFilledWithCharacter(fillingCharacter: Char): Format =
            FooterDividerFilledWithCharacter(this, fillingCharacter)
    }
}