package com.brunodles.shell.interactive

import java.util.*

typealias CommandExecutor = (List<String>) -> Unit

class Interactive(
    val preCommand: String = "",
    val breakCommand: String = "exit",
    val commandExecutor: CommandExecutor
) {
    fun start() {
        val scanner = Scanner(System.`in`)
        var command = ""
        do {
            commandExecutor(listOf(command))
            print(preCommand)
            command = scanner.nextLine()
        } while (command != breakCommand)
    }

}

class CommandExecutorBuilder {
    private val commands = mutableMapOf<Regex, CommandExecutor>()

    fun put(commandRegex: Regex, command: CommandExecutor): CommandExecutorBuilder {
        commands[commandRegex] = command
        return this
    }

    fun build(): CommandExecutor = { parameters ->
        val input = parameters.joinToString(" ")
        commands.filterKeys { regex -> regex.matches(input) }
            .map { (regex, command) -> Pair(regex.find(input)!!, command) }
            .firstOrNull()
            ?.let { (it, command) ->
                command.invoke(it.groupValues)
            }
    }
}
