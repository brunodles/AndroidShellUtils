package com.brunodles.groovy

import groovy.json.JsonOutput
import groovy.yaml.YamlBuilder
import org.apache.groovy.yaml.util.YamlConverter

import static com.brunodles.groovy.ExtensionFunctions.registerCustomExtensionFunctions
import static com.brunodles.groovy.ExtensionFunctions.tryOrNull

class GroovyScratch {

    static void main(String[] args) {
        registerCustomExtensionFunctions()

        def jsonDir = new File("/Users/bruno.lima/workspace/AndroidShellUtils/sample_data/boards/")
        jsonDir.listFiles()
            .sort()
            .take(10)
            .each {file ->
                tryOrNull {
                    def outputFIle = new File(file.absolutePath.replace(".json", ".yaml"))
                    outputFIle.write(YamlConverter.convertJsonToYaml(file.newReader()))
                }
            }
//        def file = new File("/Users/bruno.lima/workspace/AndroidShellUtils/sample_data/boards/a_certain_scientific_railgun.json")
//        def json = file.readJson()

//        def builder = new YamlBuilder()
//        outputFIle.write(builder(json).toString())

//        json.forEach { content->
//            content.pins = []
//            content.url = null
//            content.originalUrl = null
//            def boardFile = new File(jsonDir, "${content.key}.json")
//            boardFile.write(JsonOutput.toJson(content))
//            println "completed ${content.key}"
//        }
    }

}
