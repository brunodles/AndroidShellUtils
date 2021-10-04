package com.brunodles.shell

import com.brunodles.shell.listeners.PrintShellListener
import java.io.File
import java.io.IOException

class CustomShell(
    initialCommand: String = "sh",
    workdir : File = File(System.getProperty("user.home")),
    private val listener: Shell.Listener = PrintShellListener()
) : Shell {

    private val process = ProcessBuilder()
        .command(initialCommand.split(" "))
        .directory(workdir)
        .start()
    private val resultReaderBufferSize = 2048
    private val bufferedReader = process.inputStream.bufferedReader()
    private val resultReaderThread = Thread {
        try {
            while (true) {
                val chars = CharArray(resultReaderBufferSize)
                val charsRead: Int = bufferedReader.read(chars, 0, resultReaderBufferSize)
                val result: String = if (charsRead != -1) {
                    String(chars, 0, charsRead)
                } else {
                    ""
                }
                listener.onText(result)
            }
        } catch (e: InterruptedException) {
        } catch (e: IOException) {
        }
    }

    init {
        resultReaderThread.start()
        listener.onStart()
    }

    override fun execute(command: String) {
        with(process.outputStream.bufferedWriter()) {
            write(command)
            write("\n")
            flush()
        }
        listener.onExecute()
    }

    override fun stop() {
        resultReaderThread.interrupt()
        listener.onStop()
        process.destroy()
    }

}
