package com.brunodles.adbshell

import com.brunodles.adbshell.AndroidEnv.adbPath
import com.brunodles.adbshell.AndroidEnv.externalPath
import com.brunodles.shell.CustomShell
import com.brunodles.shell.Shell
import com.brunodles.shell.listeners.SyncShellListener
import java.io.File

class AdbShell(
    private var appPackage: String = "",
    private var workingDir: String = "",
) : Shell {
    private val shellListener = SyncShellListener()
    private val shell = CustomShell(
        initialCommand = "$adbPath shell",
        listener = shellListener
    )

    init {
        if (appPackage.isNotBlank())
            runAs(appPackage)
        if (workingDir.isNotBlank())
            workingDir(workingDir)
    }


    fun runAs(appPackage: String) {
        this.appPackage = appPackage
        execute("run-as $appPackage")
    }

    fun workingDir(workingDir: String) {
        this.workingDir = workingDir
        execute("cd $workingDir")
    }

    fun runChildCommand(block: AdbShell.() -> Unit) {
        Thread {
            val child = AdbShell(appPackage, workingDir)
            block(child)
            child.stop()
        }.start()
    }

    fun readFile(fileName: String): String {
        shell.execute("cat $fileName")
        return getResult()
    }

    fun writeFile(fileName: String, content: String) {
        shell.execute("echo '$content'>$fileName")
    }

    fun getResult() = shellListener.getResult()

    override fun execute(command: String) = shell.execute(command)
    override fun stop() = shell.stop()

}
