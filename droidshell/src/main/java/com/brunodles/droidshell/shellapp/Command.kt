package com.brunodles.droidshell.shellapp

interface Command {
    fun isApplicable(inputLine : CommandQuery) : Boolean

    fun invoke(inputLine: CommandQuery, context : ShellContext) : String
}
