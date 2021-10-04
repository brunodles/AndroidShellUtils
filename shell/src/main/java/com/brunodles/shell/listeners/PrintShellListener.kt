package com.brunodles.shell.listeners

import com.brunodles.shell.Shell

class PrintShellListener : Shell.Listener {
    override fun onText(line: String) {
        print(line)
    }
}