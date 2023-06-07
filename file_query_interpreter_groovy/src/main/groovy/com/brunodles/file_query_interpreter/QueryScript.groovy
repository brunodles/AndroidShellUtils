package com.brunodles.file_query_interpreter

import com.brunodles.file_query.JsonDatabase
import com.brunodles.tablebuilder.FormatDefault

abstract class QueryScript extends Script {

    /** [toString] will be used to get the value from this return */
    public Closure selectFieldClosure = null
    /** filter to detect if the file matches a *table* */
    public Closure<Boolean> fromClosure = { true }
    /** filter to detect if the content */
    public Closure<Boolean> whereClosure = { true }
    /** prints the result */
    public Closure<String> tablePresentationClosure = null

    def select(Closure closure) {
        selectFieldClosure = closure
    }

    def from(Closure<Boolean> closure) {
        fromClosure = closure
    }

    def where(Closure<Boolean> closure) {
        whereClosure = closure
    }

    def tablePresentation(Closure<String> closure) {
        tablePresentationClosure = closure
    }
}
