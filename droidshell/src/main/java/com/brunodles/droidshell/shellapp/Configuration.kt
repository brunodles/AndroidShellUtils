package com.brunodles.droidshell.shellapp

data class Configuration(
    val application: String = "",
    val workingDir: String = "",
    val aliases: Map<String, String> = emptyMap()
)