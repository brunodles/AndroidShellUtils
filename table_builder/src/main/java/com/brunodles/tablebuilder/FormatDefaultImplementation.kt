package com.brunodles.tablebuilder

import com.brunodles.tablebuilder.format_impl.*
import com.brunodles.tablebuilder.format_impl.FooterDividerFilledWithCharacter.Companion.footerDividerFilledWithCharacter
import com.brunodles.tablebuilder.format_impl.FormatFilledWithCharacter.Companion.filledWithSpace
import com.brunodles.tablebuilder.format_impl.FormatWithBorders.Companion.withBorders
import com.brunodles.utils.createString

sealed class FormatDefaultImplementation(
    val name: String,
) : Format {

    object Csv : Wrapper("csv", CharacterSeparatedValues(",", ","))
    object CsvWithFooterDivider : Wrapper(
        name = "csv",
        innerFormat = CharacterSeparatedValues(
            headerSeparator = ",",
            bodySeparator = ",",
            footerDividerCellContent = "-"
        )
    )

    object Tsv : Wrapper("tsv", CharacterSeparatedValues("\t", "\t", null))
    object Simple : Wrapper("simple", SpacedTable())
    object Terminal : Wrapper(
        name = "terminal",
        innerFormat = CharacterSeparatedValues(
            headerSeparator = " | ",
            bodySeparator = " | ",
            headerDividerSeparator = " + ",
            footerDividerSeparator = " + ",
            footerDividerCellContent = null,
            footerDividerFillingChar = '=',
        ).filledWithSpace()
    )

    object JiraSpaced : Wrapper("jira_spaced", SpacedTableWithBorders())
    object JiraSpacedWithoutFooterDivider : Wrapper(
        name = "jira_spaced_without_footer_divider",
        innerFormat = FormatWithBorders(
            headerBorders = "|| " to " ||",
            bodyBorders = "|  " to "  |",
            innerFormat = FormatFilledWithCharacter(
                innerFormat = CharacterSeparatedValues(
                    headerSeparator = " || ",
                    bodySeparator = " |  ",
                    footerDividerCellContent = null,
                    footerDividerFillingChar = null
                ),
            ),
        )
    )

    object JiraSpaced2 : Wrapper(
        name = "jira_spaced2",
        innerFormat = CharacterSeparatedValues(
            headerSeparator = " || ",
            bodySeparator = " |  ",
            footerDividerCellContent = null
        ).footerDividerFilledWithCharacter('-')
            .filledWithSpace()
            .withBorders(
                headerBorders = "|| " to " ||",
                bodyBorders = "|  " to "  |",
            ),
    )

    object Jira : Wrapper(
        name = "jira",
        innerFormat = FormatWithBorders(
            innerFormat = CharacterSeparatedValues(
                headerSeparator = "||",
                bodySeparator = "|",
                footerDividerCellContent = null
            ),
            headerBorders = "||" to "||",
            bodyBorders = "|" to "|",
        )
    )

    object Markdown : FormatDefaultImplementation("markdown") {

        private val contentFormat = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "| " + content.joinToString(" | ") + " |"

            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                content.withSize(size, direction)
        }
        override val headerFormat: Format.ContentFormat = contentFormat
        override val headerDividerFormat: Format.ContentFormat? = object : Format.ContentFormat {
            override fun line(content: List<String>): String = contentFormat.line(content)
            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                when (direction) {
                    ColumnDirection.left -> ":---".padEnd(size, '-')
                    ColumnDirection.center -> ":---".padEnd(size - 1, '-') + ":"
                    ColumnDirection.right -> "---:".padStart(size, '-')
                    else -> createString('-', size)
                }
        }
        override val bodyFormat: Format.ContentFormat = contentFormat
        override val footerDivider: Format.ContentFormat? = object : Format.ContentFormat {
            override fun line(content: List<String>): String = contentFormat.line(content)
            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                createString('=', size)
        }
        override val footerFormat: Format.ContentFormat? = contentFormat
    }

    open class Wrapper(
        name: String,
        private val innerFormat: Format,
    ) : FormatDefaultImplementation(name), Format by innerFormat

    override fun toString(): String = name

    companion object {
    }
}
