package com.brunodles.droidshell.shellapp

import com.brunodles.adbshell.AdbShell
import com.brunodles.utils.println
import java.io.File
import java.util.*

interface ShellContext {
    val currentPath : String
    val shell : AdbShell
    fun history() : List<String>
}

class ShellBuilder(
    val configuration : Configuration,
    commandRegister : ShellBuilder.() -> Unit
) {
    private val commands = mutableListOf<Command>()

    init {
        commandRegister.invoke(this)
    }

    fun registerCommand(command: Command): ShellBuilder {
        commands.add(command)
        return this
    }

    fun build() : ShellApplication {
        return ShellApplication(
            configuration, commands
        );
    }
}

class ShellApplication(
    val configuration : Configuration,
    val commands : List<Command>
) : ShellContext {
    val history = File(".droidShell.history")
    override val shell = AdbShell(configuration.application, configuration.workingDir)

    val scanner = Scanner(System.`in`)
    var result = ""
    override var currentPath = ""

    fun loop() {
        var command: String = ""
        do {
            if (command.isNotBlank()) {
                result = evaluate(command)
                result.println()
            }
            // current path
            shell.execute("pwd")
            currentPath = shell.getResult()
            // PS1
            print("$currentPath $ ")
            // input
            command = scanner.nextLine()
            history.appendText("$command\n")
        } while (command != "exit")
        shell.stop()
    }

    private fun evaluate(inputLine : String) :String {
        return evaluate(CommandQuery(inputLine))
    }
    private fun evaluate(commandQuery : CommandQuery) :String {
        // Alias
        if (configuration.aliases.containsKey(commandQuery.firstOrNull())) {
            configuration.aliases[commandQuery.firstOrNull()]?.let {
                val newInputLine = commandQuery.inputLine.replace(commandQuery.firstOrNull()!!, it)
                history.appendText("\t$newInputLine\n")
                return evaluate(newInputLine)
            }
        }

        // custom commands
        commands.forEach {
            if (it.isApplicable(commandQuery)) {
                return it.invoke(commandQuery, this)
            }
        }

        // default shell
        shell.execute(commandQuery.inputLine)
        return shell.getResult()
    }

    override fun history(): List<String> = history.readLines()
}