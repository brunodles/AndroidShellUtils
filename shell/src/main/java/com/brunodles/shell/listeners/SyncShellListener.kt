package com.brunodles.shell.listeners

import com.brunodles.shell.Shell
import com.brunodles.utils.DelayedAtomicBoolean

open class SyncShellListener : Shell.Listener {

    private val resultBuffer = StringBuilder()
    private val isReading = DelayedAtomicBoolean(false, 100)
    private val isWaitingToRead = DelayedAtomicBoolean(false, 1000)

    override fun onExecute() {
        isWaitingToRead.value = true
    }

    override fun onText(line: String) {
        isReading.value = true
        isWaitingToRead.value = false
        resultBuffer.append(line)//.append("\n")
    }

    fun clearResultBuffer() = resultBuffer.clear()

    fun getResult() : String {
        do {
            Thread.sleep(100)
        } while (isReading.value || isWaitingToRead.value)
        val result = resultBuffer.toString().trimEnd()
        resultBuffer.clear()
        return result
    }
}
