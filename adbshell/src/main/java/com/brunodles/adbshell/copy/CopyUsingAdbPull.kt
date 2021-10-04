package com.brunodles.adbshell.copy

import com.brunodles.adbshell.AdbShell
import com.brunodles.shell.CustomShell
import com.brunodles.shell.listeners.SyncShellListener
import java.io.File

/**
 * Copy files from the inside of apps to local disk using AdbPull.
 */
class CopyUsingAdbPull(
    /** filter as used by cp command */
    val cpFilter: String,
    /** name of temporary folder user to copy files from app to accessible app from adb pull. "/sdcard/tmp/<context>" */
    val androidTempDir: String,
    /** Application package */
    val appPackage: String,
    /** Workdir of the app, where the files will be pulled from */
    val inAppWorkingDir: String,
    /** Workdir of the tool, where the files will be copied too */
    val localWorkingDir: String,
    /** Name of the current Copy */
    val name: String? = null
) : Copy {

    private val localWorkingDirFile = File(localWorkingDir)
    private val adbShell = AdbShell(appPackage, inAppWorkingDir)

    private val localShellListener = SyncShellListener()
    private val localShell = CustomShell(workdir = localWorkingDirFile, listener = localShellListener)

    /**
     * Copy files
     */
    override fun copy() {
        name?.let { println("[$name] - copy to external folder") }
        adbShell.apply {
            execute("rm $androidTempDir/*")
            execute("mkdir $androidTempDir")
            execute("cp $cpFilter $androidTempDir")
            getResult()
        }
        if (!localWorkingDirFile.exists()) localWorkingDirFile.mkdirs()
        name?.let { println("[$name] - adb pull") }
        localShell.execute("adb pull $androidTempDir")
        localShellListener.getResult()
    }
}
