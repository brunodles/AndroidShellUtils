package com.brunodles.groovy

import static com.brunodles.groovy.ExtensionFunctions.registerCustomExtensionFunctions
import static com.brunodles.groovy.FormatFunctions.*
import static java.lang.Math.*

class GroovyScratch {

    static void main(String[] args) {
        registerCustomExtensionFunctions()
        def files = new ArrayList<File>()
//        new File("./")
        new File("/Users/bruno.lima/workspace/scratch/working_area/tmp_sync")
                .eachFileRecurse { file -> if (file.isFile()) files.add(file) }

//        files.collect { it.toJson() }
//            .findAll { it.chunkUUID }
////            .forEach { println it["chunkUUID"] }
//            .forEach {
//                println it
//                try {
//                    println "\t${it.chunkUUID}"
//                } catch (Exception e) {
//                    println "\tError ${e.message}"
//                }
//            }
        def result = GQ {
            from chunk in files.collect { it.toJson() }
            where chunk.chunkUUID != null
            select chunk.chunkUUID,
                    chunk.chunkIndexOnSegment,
                    round((chunk.endTimeInMillis - chunk.startTimeInMillis) / 60000) as duration,
                    dateFormat(chunk.startTimeInMillis, "HH:MM:ss") as date
        }

        def resultList = result.toList()
        println resultList.collect {it.nameList.withIndex().collectEntries { value, index -> [index, value] }
        }.inject { aggregator, value -> aggregator+value}
            .sort()
            .collect { index, name -> name }
        println resultList.join("\n")
    }

}
