package com.brunodles.file_query

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class Element<T : Any?>(
    private val element: T
) {
    operator fun get(key: String): Element<*> {
        if (element is JsonObject)
            return Element(element.get(key))
        this.element?.let { element ->
            val elementClass = element::class.java
            try {
                return Element(elementClass.getDeclaredField(key).get(element))
            } catch (_: Exception) {
            }
            try {
                return Element(elementClass.getDeclaredMethod(key).invoke(elementClass))
            } catch (_: Exception) {
            }
        }
        return Element(null)
    }

    infix fun eq(value: Int): Boolean {
        return eq(value.toString())
    }

    infix fun eq(value: String): Boolean {
        return element.toString() == value
    }

    infix fun eq(value: Boolean): Boolean {
        return element.toString() == value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Element<*>

        if (element != other.element) return false

        return true
    }

    override fun hashCode(): Int {
        return element.hashCode()
    }

    infix fun contains(content: String): Boolean {
        return element.toString().contains(content, true)
    }

    override fun toString(): String {
        return element.toString()
    }

    operator fun minus(other: Element<*>): Element<Double> {
        try {
            val selfNumber = asDouble()
            val otherNumber = other.asDouble()
            return Element(selfNumber - otherNumber)
        } catch (e: Exception) {
            throw IllegalArgumentException("Unable calculate a minus on nom-number element.", e)
        }
    }

    fun asDouble(): Double {
        return when (element) {
            is Number -> element.toDouble()
            is String -> element.toDouble()
            is JsonPrimitive -> element.asDouble
            else -> throw IllegalArgumentException("Unable to parse to Double. Type: \"${elementClassName()}\" Value:\"$element\".")
        }
    }

    fun asLong(): Long {
        return when (element) {
            is Number -> element.toLong()
            is String -> element.toLong()
            is JsonPrimitive -> element.asLong
            else -> throw IllegalArgumentException("Unable to parse to Long. Type: \"${elementClassName()}\" Value:\"$element\".")
        }
    }

    fun count(): Element<Int> {
        return when (element) {
            is Array<*> -> element.size
            is List<*> -> element.size
            is Map<*, *> -> element.size
            is String -> element.length
            is JsonObject -> element.size()
            is JsonArray -> element.size()
            else -> throw IllegalArgumentException("Unable to count on this type. Type: \"${elementClassName()}\" Value:\"$element\".")
        }.let { Element(it) }
    }

    private fun elementClassName(): String = element?.let { it::class.java.simpleName } ?: "null"
}