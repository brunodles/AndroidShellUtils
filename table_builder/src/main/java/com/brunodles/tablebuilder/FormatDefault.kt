package com.brunodles.tablebuilder

import com.brunodles.utils.createString

enum class FormatDefault : Format {

    simple {
        private val separator = " | "

        private val contentFormat: Format.ContentFormat = Format.SimpleContentFormat(separator)
        private val dividerFormat: Format.ContentFormat = Format.SimpleDividerFormat( separator, '-')

        override val headerFormat: Format.ContentFormat = contentFormat
        override val headerDividerFormat: Format.ContentFormat? = dividerFormat
        override val bodyFormat: Format.ContentFormat = contentFormat
        override val footerDivider: Format.ContentFormat? = dividerFormat
        override val footerFormat: Format.ContentFormat? = contentFormat
    },
    markdown {
        private val minDashesOnDivider = 3

        private val contentFormat = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "| " + content.joinToString(" | ") + " |"
            override fun cell(content: String, size: Int, direction: ColumnDirection): String=
                    content.withSize(size, direction)
        }
        override val headerFormat: Format.ContentFormat = contentFormat
        override val headerDividerFormat: Format.ContentFormat?= object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "| " + content.joinToString(" | ") + " |"
            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                when (direction) {
                    ColumnDirection.left -> ":".padEnd(size.coerceAtLeast(minDashesOnDivider), '-')
                    ColumnDirection.center -> ":" + createString('-', (size - 2).coerceAtLeast(minDashesOnDivider)) + ":"
                    ColumnDirection.right -> ":".padStart(size.coerceAtLeast(minDashesOnDivider), '-')
                    else -> createString('-', size)
                }
        }
        override val bodyFormat: Format.ContentFormat = contentFormat
        override val footerDivider: Format.ContentFormat? = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "| " + content.joinToString(" | ") + " |"
            override fun cell(content: String, size: Int, direction: ColumnDirection): String=
                createString('=', size)
        }
        override val footerFormat: Format.ContentFormat? = contentFormat
    },
    jira {
        override val headerFormat: Format.ContentFormat = object : Format.ContentFormat {
            override fun line(content: List<String>): String =
                "|| " + content.joinToString(" || ") + " ||"

            override fun cell(content: String, size: Int, direction: ColumnDirection): String=
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
                "   " + content.joinToString(" | ")
            override fun cell(content: String, size: Int, direction: ColumnDirection): String =
                createString('-', size)
        }
        override val footerFormat: Format.ContentFormat? = bodyFormat
    },
    csv {
        private val cellSeparator = ","
        private val contentFormat = object :Format.ContentFormat {
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
                "---"
        }
        override val footerFormat: Format.ContentFormat? = contentFormat
    },
    ;
}