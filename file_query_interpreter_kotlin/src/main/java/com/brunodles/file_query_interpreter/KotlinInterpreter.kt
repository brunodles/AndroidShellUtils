package com.brunodles.file_query_interpreter

//import org.jetbrains.kotlin.script.examples.jvm.simple.SimpleScript
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

object KotlinInterpreter {

    @JvmStatic
    fun main(args: Array<String>) {
//        args.lastOrNull() ?: throw IllegalArgumentException("Missing the target file")

        if (args.size != 1) {
            println("usage: <app> <script file>")
        } else {
            val scriptFile = File(args[0])
            println("Executing script $scriptFile")

            val res = evalFile(scriptFile)

            res.reports.forEach {
                if (it.severity > ScriptDiagnostic.Severity.DEBUG) {
                    println(" : ${it.message}" + if (it.exception == null) "" else ": ${it.exception}")
                }
            }
        }
    }

    fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<Any> {
            jvm {
                dependenciesFromCurrentContext(
                    "script", /* script library jar name */
                    "guava",
                    wholeClasspath = true
                )
            }
        }

        return BasicJvmScriptingHost()
            .eval(scriptFile.toScriptSource(), compilationConfiguration, null)
    }
}
