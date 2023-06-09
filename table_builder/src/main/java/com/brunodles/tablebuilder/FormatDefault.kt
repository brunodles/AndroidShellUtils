package com.brunodles.tablebuilder

import com.brunodles.tablebuilder.content_format_impl.CharacterFilledDividerFormat
import com.brunodles.tablebuilder.content_format_impl.SpacedContentFormat
import com.brunodles.utils.createString

@Deprecated("Use FormatDefaultImplementation instead.")
enum class FormatDefault : Format {

    @Deprecated(
        message = "Use FormatDefaultImplementation instead.",
        replaceWith = ReplaceWith("FormatDefaultImplementation.Simple", "com.brunodles.tablebuilder.FormatDefaultImplementation")
    )
    simple {
        private val separator = " | "

        private val contentFormat: Format.ContentFormat = SpacedContentFormat(separator)
        private val dividerFormat: Format.ContentFormat = CharacterFilledDividerFormat(separator, '-')

        override val headerFormat: Format.ContentFormat = contentFormat
        override val headerDividerFormat: Format.ContentFormat? = dividerFormat
        override val bodyFormat: Format.ContentFormat = contentFormat
        override val footerDivider: Format.ContentFormat? = dividerFormat
        override val footerFormat: Format.ContentFormat? = contentFormat
    },

    @Deprecated(
        message = "Use FormatDefaultImplementation instead.",
        replaceWith = ReplaceWith("FormatDefaultImplementation.Markdown", "com.brunodles.tablebuilder.FormatDefaultImplementation")
    )
    markdown {
        private val minDashesOnDivider = 3

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
                    ColumnDirection.left -> ":".padEnd(size.coerceAtLeast(minDashesOnDivider), '-')
                    ColumnDirection.center -> ":" + createString(
                        '-',
                        (size - 2).coerceAtLeast(minDashesOnDivider)
                    ) + ":"

                    ColumnDirection.right -> ":".padStart(size.coerceAtLeast(minDashesOnDivider), '-')
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
    },
    @Deprecated(
        message = "Use FormatDefaultImplementation instead.",
        replaceWith = ReplaceWith("FormatDefaultImplementation.Jira", "com.brunodles.tablebuilder.FormatDefaultImplementation")
    )
    jira {
        override val headerFormat: Format.ContentFormat = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "|| " + content.joinToString(" || ") + " ||"

            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                content.withSize(size, direction)
        }
        override val headerDividerFormat: Format.ContentFormat? = null
        override val bodyFormat: Format.ContentFormat = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "|  " + content.joinToString(" |  ") + "  |"

            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                content.withSize(size, direction)

        }
        override val footerDivider: Format.ContentFormat? = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "|  " + content.joinToString(" |  ") + "  |"

            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                createString('-', size)
        }
        override val footerFormat: Format.ContentFormat? = bodyFormat
    },
    @Deprecated(
        message = "Use FormatDefaultImplementation instead.",
        replaceWith = ReplaceWith("FormatDefaultImplementation.Csv", "com.brunodles.tablebuilder.FormatDefaultImplementation")
    )
    csv {
        private val cellSeparator = ","
        private val contentFormat = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                content.joinToString(cellSeparator)

            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                content
        }
        override val headerFormat: Format.ContentFormat = contentFormat
        override val headerDividerFormat: Format.ContentFormat? = null
        override val bodyFormat: Format.ContentFormat = contentFormat
        override val footerDivider: Format.ContentFormat? = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                content.joinToString(cellSeparator)

            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                "-"
        }
        override val footerFormat: Format.ContentFormat? = contentFormat
    },
    ;
}