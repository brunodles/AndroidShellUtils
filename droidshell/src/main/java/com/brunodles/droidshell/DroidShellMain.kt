package com.brunodles.droidshell

import com.brunodles.droidshell.shellapp.commands.Json2PathCommand
import com.brunodles.droidshell.shellapp.commands.JsonPathCommand
import com.brunodles.droidshell.shellapp.Configuration
import com.brunodles.droidshell.shellapp.ShellBuilder
import com.brunodles.droidshell.shellapp.commands.History
import com.google.gson.Gson
import java.io.File
import javax.script.ScriptEngineManager

fun main() {
//    val kotlinEngine = ScriptEngineManager().getEngineByExtension("kts")
//    val kotlinBindings = kotlinEngine.createBindings()
//    kotlinBindings["gson"] = gson
    val gson = Gson()
    val configuration = YamlHelper.mapper.readValue(File("droidShell.yaml"), Configuration::class.java)

    val shell = ShellBuilder(configuration) {
        registerCommand(JsonPathCommand())
        registerCommand(Json2PathCommand(gson))
        registerCommand(History())
    }.build()
    shell.loop()
}
