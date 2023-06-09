package com.brunodles.groovy

import groovy.json.JsonOutput

import static com.brunodles.groovy.ExtensionFunctions.registerCustomExtensionFunctions

class GroovyScratch {

    static void main(String[] args) {
        registerCustomExtensionFunctions()

        def jsonDir = new File("/Users/bruno.lima/workspace/AndroidShellUtils/sample_data/images/")
        jsonDir.mkdirs()
        def json = new File("/Users/bruno.lima/workspace/AndroidShellUtils/sample_data/images.json")
            .toJson()

        json.forEach { content->
            content.pins = []
            content.url = null
            content.originalUrl = null
            def boardFile = new File(jsonDir, "${content.key}.json")
            boardFile.write(JsonOutput.toJson(content))
            println "completed ${content.key}"
        }
    }

}
