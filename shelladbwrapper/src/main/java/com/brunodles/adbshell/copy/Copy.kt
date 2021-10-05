package com.brunodles.adbshell.copy

interface Copy {
    fun copy()

    /**
     * Start copping files in a new thread
     */
    fun syncThread(delayInMs: Long, before : (() -> Unit)? = null) = Thread {
        try {
            while (true) {
                try {
                    before?.invoke()
                } catch (e : Exception) {
                }
                try {
                    copy()
                } catch (e: Exception) {
                }
                Thread.sleep(delayInMs)
            }
        } catch (e: InterruptedException) {
        }
    }
}