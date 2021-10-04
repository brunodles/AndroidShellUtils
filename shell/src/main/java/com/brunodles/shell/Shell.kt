package com.brunodles.shell

interface Shell {
    fun execute(command: String)
    fun stop()
    interface Listener {
        fun onStart() {}
        fun onExecute() {}
        fun onStop() {}
        fun onText(line: String)
    }
}