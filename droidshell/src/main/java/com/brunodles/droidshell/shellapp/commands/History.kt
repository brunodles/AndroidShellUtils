package com.brunodles.droidshell.shellapp.commands

import com.brunodles.droidshell.shellapp.Command
import com.brunodles.droidshell.shellapp.CommandQuery
import com.brunodles.droidshell.shellapp.ShellContext

class History : Command {
    override fun isApplicable(inputLine: CommandQuery): Boolean = inputLine.firstOrNull() == "history"

    override fun invoke(inputLine: CommandQuery, context: ShellContext): String {
        return context.history().joinToString("\n")
    }
}