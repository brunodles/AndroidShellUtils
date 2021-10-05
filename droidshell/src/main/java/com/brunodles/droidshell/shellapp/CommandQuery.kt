package com.brunodles.droidshell.shellapp

data class CommandQuery(
    val inputLine : String
) {
    val splited : List<String> by lazy { inputLine.split(" ") }

    fun firstOrNull() = splited.firstOrNull()

    operator fun get(i: Int): String? = splited.getOrNull(i)

}