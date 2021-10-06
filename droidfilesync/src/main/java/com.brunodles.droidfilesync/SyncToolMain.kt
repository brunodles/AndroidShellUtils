package com.brunodles.droidfilesync

import com.brunodles.adbshell.copy.CopyUsingAdbPull
import java.io.File

fun main() {
    val file = File("droidSync.yaml")
    val config = YamlHelper.mapper.readValue(file, Configuration::class.java)
    val threads = config.copyWithAdb.map { (key, value) ->
        val localWorkingDir = File(value.localWorkingDir)
        localWorkingDir.mkdirs()
        val appPackage = if (value.appPackage.isBlank()) config.appPackage else value.appPackage
        key to CopyUsingAdbPull(
            cpFilter = value.cpFilter,
            androidTempDir = value.androidTempDir,
            appPackage = appPackage,
            inAppWorkingDir = value.inAppWorkingDir,
            localWorkingDir = value.localWorkingDir,
            name = key
        ).syncThread(value.delayInMs)
    }

    threads.forEach { (key, thread) ->
        println("Starting \"$key\"")
        thread.start()
    }
}
