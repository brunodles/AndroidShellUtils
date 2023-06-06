package com.brunodles.file_query_interpreter

import com.brunodles.file_query.JsonDatabase
import org.codehaus.groovy.control.CompilerConfiguration

class GroovyScratch {

    static void main(String[] args) {
        def binding = new Binding()
        def config = new CompilerConfiguration()
        config.scriptBaseClass = QueryScript.class.name
//        config.scriptBaseClass = DelegatingScript.class.name
        def shell = new GroovyShell(this.class.classLoader, binding, config)

        QueryScript script = (QueryScript) shell.parse("""
            import static com.brunodles.tablebuilder.ColumnDirection.*

            select { 
                field("key")
                field("name")
                field("imagesCount")
                field("pinCount")
                field("pins") { it.count() }
                field("enabled")
                field("lastChange")
                file()
            }
            from { file.absolutePath.contains("boards") }
            where {
                field["name"].contains("card")
            }
            tablePresentation {
                add("key")
                add("name")
                add("imagesCount", right)
                add("pinCount", right)
                add("realPinCount", right)
                add("enabled")
                add("lastChange")
                add("file")
            }
        """.stripIndent())
        JsonDatabase jsonDatabase = new JsonDatabase(new File("/home/bruno/workspace/android/endeavor/wallpaper/database/filesv4/_output/boards"))

//        jsonDatabase.newQuery { queryBuilder ->
//            script.delegate = queryBuilder
//            script.run()
//        }
        script.run()
        jsonDatabase.newQuery {
            it.select { selectContext ->
                script.selectFieldClosure.delegate = selectContext
                script.selectFieldClosure()
            }
            it.from { fromContext ->
                script.fromClosure.delegate = fromContext
                script.fromClosure()
            }
            it.where { whereContext ->
                script.whereClosure.delegate = whereContext
                script.whereClosure()
            }
            if (script.tablePresentationClosure!=null) {
                it.tablePresentation() { tablePresentationContext ->
                    script.tablePresentationClosure.delegate = tablePresentationContext
                    script.tablePresentationClosure()
                }
            }
        }
    }

}
