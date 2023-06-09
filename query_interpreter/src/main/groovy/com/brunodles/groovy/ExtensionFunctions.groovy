package com.brunodles.groovy

import groovy.json.JsonSlurper

class ExtensionFunctions {

    private ExtensionFunctions() {
    }

    public static void registerCustomExtensionFunctions() {
        File.metaClass.toJson = { ->
            def jsonObject = new JsonSlurper().parseText(new String(delegate.readBytes()))
            return MyProxy.create(jsonObject)
//            return jsonObject
        }
        Collection.metaClass.random = { ->
            def random = new Random()
            return delegate.get(random.nextInt(delegate.size()))
        }

    static def tryOrNull(Closure closure) {
        try {
            return closure()
        } catch (Throwable ignore) {
        }
        return null
    }
}
