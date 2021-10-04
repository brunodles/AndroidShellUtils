package com.brunodles.utils

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File = File("./")): String? {
    println("> $this")
    try {
        val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        proc!!.waitFor(60, TimeUnit.MINUTES)
        return proc.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        e.printStackTrace()

        return null
    }
}

fun String?.println() = this?.let { println(it) }


fun clear() {
    repeat(100) { print("\r\b") }
//    System.out.print("\033[H\033[2J")
    System.out.flush()
}