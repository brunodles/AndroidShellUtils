package com.brunodles.groovy

import groovy.json.JsonSlurper
import groovy.yaml.YamlBuilder
import groovy.yaml.YamlSlurper

class ExtensionFunctions {

    private ExtensionFunctions() {
    }

    public static void registerCustomExtensionFunctions() {
        File.metaClass.readJson = { ->
            if ((delegate as File).absolutePath.endsWith('.json')) {
                def readObject = new JsonSlurper().parse(delegate as File)
                def result = MyProxy.create(readObject)
                result.putExtraProperty("__sourceType", "json")
                result.putExtraProperty("__sourceFile", delegate)
                return result
            }
            return null
        }
        File.metaClass.readYaml = { ->
            def path = (delegate as File).absolutePath
            if (path.endsWith('.yaml') || path.endsWith(".yml")) {
                def readObject = new YamlSlurper().parse(delegate as File)
                def result = MyProxy.create(readObject)
                result.putExtraProperty("__sourceType", "yaml")
                result.putExtraProperty("__sourceFile", delegate)
                return result
            }
            return null
        }
        File.metaClass.readCsv = { ->
            if ((delegate as File).absolutePath.endsWith('.csv')) {
                return readCharacterSeparatedValueFile(delegate as File, ",", "csv")
            }
            return null
        }
        File.metaClass.readTsv = { ->
            if ((delegate as File).absolutePath.endsWith('.tsv')) {
                return readCharacterSeparatedValueFile(delegate as File, "\t", "tsv")
            }
            return null
        }

        Collection.metaClass.random = { ->
            def random = new Random()
            return delegate.get(random.nextInt(delegate.size()))
        }
    }

    static def readCharacterSeparatedValueFile(File file, String separator, String format) {
        List<String> lines = file.readLines()
        List<Tuple2<String, Integer>> headers = lines.first()
                .split(separator)
                .collect { it.trim() }
                .withIndex()
        List<Object> content = lines.drop(1)
                .findAll { line -> !line.trim().isEmpty() }
                .collect { line ->
                    List<String> row = line.split(separator)
                    Map<String, Object> content = headers.collectEntries { name, index ->
                        [name, row[index]]
                    }
                    def result = MyProxy.create(content)
                    result.putExtraProperty("__sourceType", format)
                    result.putExtraProperty("__sourceFile", file)
                    return result
                }
        return content
    }

    static <T> T first(Closure<T>... closures) {
        for (final def closure in closures) {
            def result = tryOrNull { closure() }
            if (result != null) {
                return result
            }
        }
        return null
    }

    static <T> T tryOrNull(Closure<T> closure) {
        try {
            return closure()
        } catch (Throwable ignore) {
        }
        return null
    }
}
