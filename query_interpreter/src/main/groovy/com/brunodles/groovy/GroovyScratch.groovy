package com.brunodles.groovy

import groovy.json.JsonOutput
import groovy.xml.MarkupBuilder
import groovy.yaml.YamlBuilder
import org.apache.groovy.yaml.util.YamlConverter

import static com.brunodles.groovy.ExtensionFunctions.first
import static com.brunodles.groovy.ExtensionFunctions.registerCustomExtensionFunctions
import static com.brunodles.groovy.ExtensionFunctions.tryOrNull

class GroovyScratch {

    static void main(String[] args) {
        registerCustomExtensionFunctions()

        def file = new File("/home/bruno/workspace/android/AndroidShellUtils/sample_data/boards/a__shipwright.json")
        def json = file.readJson()
        new StringWriter().with { sw ->
            new MarkupBuilder(sw).board(
                    key: "a__shipwright",
                    name: "A. Shipwright",
                    url: null,
                    expectedPins: 0,
                    pinCount: 0,
                    imagesCount: 214,
                    enabled: false,
                    lastChange: "2022.01.06 20:50:13",
                    pins: [],
                    errors: [],
                    imagesMissing: []
            )
            println sw.toString()
        }


//        def fileNames = ["a__shipwright.json"]
//        def dir = new File("/Users/bruno.lima/workspace/AndroidShellUtils/sample_data/boards/")
//        fileNames.collect { fileName ->
//            new File(dir, fileName)
//        }.collect {file ->
//            first(
//                    { file.readCsv() },
//                    { file.readTsv() },
//                    { List.of(file.readJson()) },
//                    { List.of(file.readYaml()) }
//            ) ?: Collections.emptyList()
//        }.each { println it }
//                .flatten()
//                .forEach { item ->
//                    println "${item.__sourceType} - ${item.name} - ${item.lastChange}."
//                }

//        jsonDir.listFiles()
//            .sort()
//            .take(10)
//            .each {file ->
//                tryOrNull {
//                    def outputFIle = new File(file.absolutePath.replace(".json", ".yaml"))
//                    outputFIle.write(YamlConverter.convertJsonToYaml(file.newReader()))
//                }
//            }
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
