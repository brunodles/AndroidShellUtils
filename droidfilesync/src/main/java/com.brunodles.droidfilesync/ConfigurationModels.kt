package com.brunodles.droidfilesync

data class Configuration(
    val appPackage: String ="",
    val copyWithAdb: Map<String, CopyWithAdbData> = emptyMap(),
    val copyWithText: Map<String, CopyWithTextData> = emptyMap()
)

data class CopyWithTextData(
    /** filter as used by cp command */
    val regexFilterText: String = ".*\\.txt",
    /** Application package */
    val appPackage: String ="",
    /** Workdir of the app, where the files will be pulled from */
    val inAppWorkingDir: String = "files/" ,
    /** Workdir of the tool, where the files will be copied too */
    val localDir: String = "data",
    /** delay between sync in milliseconds */
    val delayInMs: Long = 5000
)

data class CopyWithAdbData(
    /** filter as used by cp command */
    val cpFilter: String = "*.*",
    /** name of temporary folder user to copy files from app to accessible app from adb pull. "/sdcard/tmp/<context>" */
    val androidTempDir: String = "/sdcard/tmp",
    /** Application package */
    val appPackage: String = "",
    /** Workdir of the app, where the files will be pulled from */
    val inAppWorkingDir: String = "files/",
    /** Workdir of the tool, where the files will be copied too */
    val localWorkingDir: String = "data",
    /** delay between sync in milliseconds */
    val delayInMs: Long = 5000,
    /** deletion should be sync, remove local files */
    val syncDelete : Boolean = false
)
