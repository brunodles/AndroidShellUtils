package com.brunodles.androidserial

import java.io.File

fun File.child(child: String): File = File(this, child)
