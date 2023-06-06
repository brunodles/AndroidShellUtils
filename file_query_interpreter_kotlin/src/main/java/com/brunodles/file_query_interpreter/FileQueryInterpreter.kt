package com.brunodles.file_query_interpreter

import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import java.util.*

object FileQueryInterpreter {
    @JvmStatic
    fun main(args: Array<String>) {
        val context: Context = Context.enter() //

        context.optimizationLevel = -1 // this is required[2]
        val scope: Scriptable = context.initStandardObjects()

        scope.addVar("user", "bruno")
        scope.addVar("userId", UUID.randomUUID().toString())

        val result: Any = context.evaluateString(scope, "this.userId", "<cmd>", 1, null)
        println("Result: $result")

        Context.exit()
    }

    fun Scriptable.addVar(name: String, value: Any) {
        val jsOut = Context.javaToJS(value, this)
        ScriptableObject.putProperty(this, name, jsOut)
    }
}