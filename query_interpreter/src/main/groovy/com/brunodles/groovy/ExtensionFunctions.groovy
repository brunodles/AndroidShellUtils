package com.brunodles.groovy

import groovy.json.JsonSlurper
import groovy.yaml.YamlBuilder
import groovy.yaml.YamlSlurper

class ExtensionFunctions {

    private ExtensionFunctions() {
    }

    public static void registerCustomExtensionFunctions() {
        File.metaClass.readJson = { ->
            def readObject = new JsonSlurper().parse(delegate as File)
            def result = MyProxy.create(readObject)
            result.putExtraProperty("__sourceType", "json")
            result.putExtraProperty("__sourceFile", delegate)
            return result
        }
        File.metaClass.readYaml = { ->
            def readObject = new YamlSlurper().parse(delegate as File)
            def result = MyProxy.create(readObject)
            result.putExtraProperty("__sourceType", "yaml")
            result.putExtraProperty("__sourceFile", delegate)
            return result
        }

        Collection.metaClass.random = { ->
            def random = new Random()
            return delegate.get(random.nextInt(delegate.size()))
        }
    }

    static def first(Closure... closures) {
        for (final def closure in closures) {
            def result = tryOrNull { closure() }
            if (result != null)
                return result
        }
        return null
    }

    static def tryOrNull(Closure closure) {
        try {
            return closure()
        } catch (Throwable ignore) {
        }
        return null
    }
}
