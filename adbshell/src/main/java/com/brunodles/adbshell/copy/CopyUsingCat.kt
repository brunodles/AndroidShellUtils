package com.brunodles.adbshell.copy

import com.brunodles.adbshell.AdbShell
import java.io.File

/**
 * Copy files from the inside of apps using Cat and recreating files on local disk.
 */
class CopyUsingCat(
    /** filter as used by cp command */
    val regexFilterText: String,
    /** Application package */
    val appPackage: String,
    /** Workdir of the app, where the files will be pulled from */
    val inAppWorkingDir: String,
    /** Workdir of the tool, where the files will be copied too */
    val localDir: String
) : Copy {
    private val regexFilter = regexFilterText.toRegex()
    private val shell = AdbShell(appPackage, inAppWorkingDir)

    override fun copy() {
        val destinationDir = File(localDir)
        destinationDir.mkdirs()
        shell.execute("ls")

        val files = shell.getResult().split("\n").filter(regexFilter::matches)
        files.forEach { fileName ->
            val fileContent = shell.readFile(fileName)
            File(localDir, fileName).writeText(fileContent)
        }
    }
}
