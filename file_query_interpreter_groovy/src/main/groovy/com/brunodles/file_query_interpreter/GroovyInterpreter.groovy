package com.brunodles.file_query_interpreter

import com.brunodles.file_query.JsonDatabase
import org.codehaus.groovy.control.CompilerConfiguration

class GroovyInterpreter {

    static void main(String[] args) {
        def binding = new Binding()
        def config = new CompilerConfiguration()
        config.scriptBaseClass = QueryScript.class.name
        def shell = new GroovyShell(this.class.classLoader, binding, config)

        def defaultImports = """
            import static com.brunodles.tablebuilder.ColumnDirection.*
            import static com.brunodles.tablebuilder.FormatDefault.*
        """.stripIndent()

        def workingDir = new File("./")
        def file = new File(args.last())
        def fileContent = file
                .readLines()
                .findAll {!it.startsWith("#")}
                .join("\n")

        def scriptStr = "${defaultImports}\n${fileContent}"
        QueryScript script = (QueryScript) shell.parse(scriptStr)
        script.run()

        JsonDatabase jsonDatabase = new JsonDatabase(workingDir)
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
            if (script.tablePresentationClosure != null) {
                it.tablePresentation() { tablePresentationContext ->
                    script.tablePresentationClosure.delegate = tablePresentationContext
                    script.tablePresentationClosure()
                }
            }
        }
    }

}
