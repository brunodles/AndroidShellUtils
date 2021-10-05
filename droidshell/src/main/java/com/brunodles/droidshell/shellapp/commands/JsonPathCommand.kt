package com.brunodles.droidshell.shellapp.commands

import com.brunodles.droidshell.shellapp.Command
import com.brunodles.droidshell.shellapp.CommandQuery
import com.brunodles.droidshell.shellapp.ShellContext
import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath

class JsonPathCommand() : Command {
    override fun isApplicable(inputLine: CommandQuery): Boolean = inputLine.firstOrNull() == "json"

    override fun invoke(inputLine: CommandQuery, context: ShellContext): String {
        return try {
            val file = inputLine[1]!!
            val path = inputLine[2]!!
            val fileContent = context.shell.readFile(file)
            JsonPath.read<Any>(fileContent, path).toString()
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}

class Json2PathCommand(
    val gson: Gson
) : Command {
    override fun isApplicable(inputLine: CommandQuery): Boolean = inputLine.firstOrNull() == "json2"

    override fun invoke(inputLine: CommandQuery, context: ShellContext): String {
        return try {
            val file = inputLine[1]!!
            val path = inputLine[2]!!
            val fileContent = context.shell.readFile(file)
            val jsonString = gson.fromJson(fileContent, String::class.java)
            JsonPath.read<Any>(jsonString, path).toString()
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}