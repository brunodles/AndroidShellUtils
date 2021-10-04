package com.brunodles.utils

class DelayedAtomicBoolean(
    private val defaultValue: Boolean,
    private val timeout: Long
) {
    private val timeoutRunnable: Runnable = Runnable {
        try {
            Thread.sleep(timeout)
            value = defaultValue
        } catch (e: InterruptedException) {
        }
    }
    private var timeoutThread = Thread()
    var value: Boolean = defaultValue
        get() = field
        set(newValue) = synchronized(this) {
            field = newValue
            if (newValue != defaultValue) {
                if (timeoutThread.isAlive || !timeoutThread.isInterrupted) {
                    timeoutThread.interrupt()
                }
                timeoutThread = Thread(timeoutRunnable)
                timeoutThread.start()
            }

        }
}