package com.brunodles.tablebuilder.format_impl

import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.content_format_impl.ContentFormatWithBorders.Companion.withBorders

open class FormatWithBorders(
    innerFormat: Format,
    headerBorders: Pair<String, String> = Pair("|| ", " ||"),
    bodyBorders: Pair<String, String> = Pair("|  ", "  |"),
    headerDividerBorders: Pair<String, String> = bodyBorders,
    footerDividerBorders: Pair<String, String> = bodyBorders,
    footerBorders: Pair<String, String> = bodyBorders,
) : Format {
    override val headerFormat: Format.ContentFormat =
        innerFormat.headerFormat.withBorders(headerBorders)
    override val headerDividerFormat: Format.ContentFormat? =
        innerFormat.headerDividerFormat?.withBorders(headerDividerBorders)
    override val bodyFormat: Format.ContentFormat =
        innerFormat.bodyFormat.withBorders(bodyBorders)
    override val footerDivider: Format.ContentFormat? =
        innerFormat.footerDivider?.withBorders(footerDividerBorders)
    override val footerFormat: Format.ContentFormat? =
        innerFormat.footerFormat?.withBorders(footerBorders)

    companion object {
        fun Format.withBorders(
            headerBorders: Pair<String, String> = Pair("|| ", " ||"),
            bodyBorders: Pair<String, String> = Pair("|  ", "  |"),
            headerDividerBorders: Pair<String, String> = bodyBorders,
            footerDividerBorders: Pair<String, String> = bodyBorders,
            footerBorders: Pair<String, String> = bodyBorders,
        ): Format =
            FormatWithBorders(
                innerFormat = this,
                headerBorders = headerBorders,
                headerDividerBorders = headerDividerBorders,
                bodyBorders = bodyBorders,
                footerDividerBorders = footerDividerBorders,
                footerBorders = footerBorders,
            )
    }
}