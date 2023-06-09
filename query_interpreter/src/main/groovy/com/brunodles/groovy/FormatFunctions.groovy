package com.brunodles.groovy

import java.text.SimpleDateFormat

class FormatFunctions {

    public static String dateFormat(Object value, String format) {
        def formatter = new SimpleDateFormat(format)
        Date date
        if (value instanceof Long) {
            date = new Date(value)
        } else if (value instanceof String) {
            try {
                date = new Date(value.toLong())
            } catch (Throwable ignored) {
                date = new Date(value)
            }
        } else {
            return null
        }
        return formatter.format(date)
    }
}
