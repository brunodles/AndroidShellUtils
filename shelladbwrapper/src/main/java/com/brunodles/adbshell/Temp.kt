package com.brunodles.adbshell

import com.brunodles.utils.DelayedAtomicBoolean
import java.io.File
import java.util.*

fun main() {
    val builder = ProcessBuilder()
    builder.command("adb", "shell")
    builder.directory(File(System.getProperty("user.home")))
    val process = builder.start()

    val scanner = Scanner(System.`in`)
    var command = ""
    val bufferedReader = process.inputStream.bufferedReader()
    val resultBuffer = StringBuilder()
    val isReading = DelayedAtomicBoolean(false, 100)
    val isWaitingToRead = DelayedAtomicBoolean(false, 1000)
    val inThread = Thread {
        bufferedReader.lines().forEach {
            isReading.value = true
            resultBuffer.append(it).append("\n")
        }
    }
    inThread.start()
    do {
        if (command.isNotBlank()) {
            with(process.outputStream.bufferedWriter()) {
                write(command)
                write("\n")
                flush()
            }
            do {
                Thread.sleep(100)
            } while (isReading.value || isWaitingToRead.value)
            println(resultBuffer.toString().trimEnd())
            resultBuffer.clear()
        }
        print("\n>")
        isWaitingToRead.value = true
        command = scanner.nextLine()
    } while (command != "exit")

//    Executors.newSingleThreadExecutor().submit()
//    val exitCode = process.waitFor()
//    assert(exitCode == 0)
}

