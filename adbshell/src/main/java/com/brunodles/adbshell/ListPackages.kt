package com.brunodles.adbshell

import com.brunodles.utils.println
import com.brunodles.utils.runCommand


fun main() {
    "adb shell pm list packages"
        .runCommand()!!
        .split("\n")
        .joinToString("\n") { it.replace("package:", "") }
        .println()
}