package com.brunodles.adbshell

import java.lang.System.getenv

object AndroidEnv {
    val androidSdkPath get() = getenv("ANDROID_SDK")
    val adbPath get() = androidSdkPath?.let { "$androidSdkPath/platform-tools/adb" } ?: "adb"
    val externalPath get() = getenv("EXTERNAL_PATH") ?: "sdcard"
}