package com.brunodles.tablebuilder

import com.brunodles.utils.center


internal fun String.withSize(size: Int?, columnDirection: ColumnDirection, padChar : Char = ' '): String =
    if (size == null || size == 0)
        ""
    else if (columnDirection == ColumnDirection.left)
        this.padEnd(size, padChar)
    else if (columnDirection == ColumnDirection.right)
        this.padStart(size, padChar)
    else if (columnDirection == ColumnDirection.center)
        this.center(size, padChar)
    else
        this.padStart(size, padChar)
