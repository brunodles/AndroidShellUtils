package com.brunodles.tablebuilder

import com.brunodles.utils.createString

enum class FormatDefault : Format {
    simple {
        override fun headerCell(content: String): String = content
        override fun headerLine(content: List<String>): String =
            content.joinToString(" | ")

        override fun headerDivider(size: Int, direction: ColumnDirection): String =
            createString('-', size)
        override fun divider(size: Int): String =
            createString('-', size)
        override fun dividers(sizes: List<String>): String =
            sizes.joinToString(" | ")

        override fun cell(content: String): String = content
        override fun cellLine(content: List<String>): String =
            content.joinToString(" | ")

        override val isHeaderDividerRowEnabled: Boolean = true
    },
    markdown {
        override fun headerCell(content: String): String = content
        override fun headerLine(content: List<String>): String =
            "| " + content.joinToString(" | ") + " |"

        override fun headerDivider(size: Int, direction: ColumnDirection): String =
            when (direction) {
                ColumnDirection.left -> ":".padEnd(size, '-')
                ColumnDirection.center -> ":" + createString('-', size - 2) + ":"
                ColumnDirection.right -> ":".padStart(size, '-')
                else -> createString('-', size)
            }
        override fun divider(size: Int): String =
            createString('-', size)
        override fun dividers(sizes: List<String>): String =
            "| " + sizes.joinToString(" | ") + " |"


        override fun cell(content: String): String = content
        override fun cellLine(content: List<String>): String =
            "| " + content.joinToString(" | ") + " |"

        override val isHeaderDividerRowEnabled: Boolean = true
    },
    jira {
        override fun headerCell(content: String): String = content
        override fun headerLine(content: List<String>): String =
            "|| " + content.joinToString(" || ") + " ||"

        override fun headerDivider(size: Int, direction: ColumnDirection): String =
            createString('-', size)
        override fun divider(size: Int): String =
            createString('-', size)

        override fun dividers(sizes: List<String>): String =
            "   " + sizes.joinToString(" | ")

        override fun cell(content: String): String = content
        override fun cellLine(content: List<String>): String =
            "|  " + content.joinToString(" |  ") + "  |"

        override val isHeaderDividerRowEnabled: Boolean = false
    };
}