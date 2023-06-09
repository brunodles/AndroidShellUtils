package com.brunodles.groovy

public class MyProxy extends groovy.util.Proxy {

    @Override
    Object invokeMethod(String name, Object args) {
        if (name == "contains") {
            return contains(args)
        }
        try {
            return super.invokeMethod(name, args)
        } catch (Throwable ignored) {
        }
        return null
    }

    // Do not use any properties here, it would cause stackoverflow
    @Override
    Object getProperty(String propertyName) {
        if (getAdaptee() instanceof Map) {
            return getAdaptee()[propertyName]
        }
        try {
            return super.getProperty(propertyName)
        } catch (Throwable ignored) {
        }
        return null
    }

    private boolean contains(Object args) {
        if (getAdaptee() instanceof Map) {
            return (getAdaptee() as Map).keySet().contains(args)
        }
        try {
            return getAdaptee().contains(args)
        } catch (Throwable ignored) {
        }
        return false
    }

    @Override
    public String toString() {
        return getAdaptee().toString()
    }

    public static MyProxy create(Object object) {
        def proxy = new MyProxy()
        proxy.setAdaptee(object)
        return proxy
    }
}
