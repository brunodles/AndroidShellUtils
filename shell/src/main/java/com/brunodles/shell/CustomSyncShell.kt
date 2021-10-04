package com.brunodles.shell

import com.brunodles.shell.listeners.SyncShellListener
import java.util.*

fun main() {
    val shellListener = SyncShellListener()
    val shell = CustomShell(listener = shellListener)

    val scanner = Scanner(System.`in`)
    var command = ""
    do {
        if (command.isNotBlank()) {
            shell.execute(command)
            println(shellListener.getResult())
        }
        print("\n>")
        command = scanner.nextLine()
    } while (command != "exit")
    shell.stop()
}
