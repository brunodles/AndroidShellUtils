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
    }
}
