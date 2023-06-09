package com.brunodles.file_query_interpreter

import com.brunodles.file_query.JsonDatabase
import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import org.codehaus.groovy.control.CompilerConfiguration

class GroovyInterpreter {

    static void main(String[] args) {
        boolean isDebug = args.contains("--debug") || args.contains("-d")

        def binding = new Binding()
        def config = new CompilerConfiguration()
        config.scriptBaseClass = QueryScript.class.name
        def shell = new GroovyShell(this.class.classLoader, binding, config)
        shell.setVariable("args", args)

        def defaultImports = """
            import static com.brunodles.tablebuilder.ColumnDirection.*
            import static com.brunodles.tablebuilder.FormatDefault.*
            import static com.brunodles.file_query.ExtraFunctions.*
            import com.brunodles.tablebuilder.FormatDefault
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
        jsonDatabase.newQuery(
            {counters ->
                if (isDebug) {
                    def builder = new TableBuilder()
                        .columns {
                            it.add("name")
                            it.add("counter")
                        }

                    counters.forEach { key, value ->
                        builder.newRow {
                            it.add(key)
                            it.add(value)
                        }
                    }
                    println builder.build()
                }
            },
            {
                it.select { selectContext ->
                    try {
                        script.selectFieldClosure.delegate = selectContext
                        script.selectFieldClosure()
                    } catch (Exception e) {
                        throw new FailedEvaluationException("select", e)
                    }
                }
                it.from { fromContext ->
                    try {
                        script.fromClosure.delegate = fromContext
                        script.fromClosure()
                    } catch (Exception e) {
                        throw new FailedEvaluationException("from", e)
                    }
                }
                it.where { whereContext ->
                    try {
                        script.whereClosure.delegate = whereContext
                        script.whereClosure()
                    } catch (Exception e) {
                        throw new FailedEvaluationException("where", e)
                    }
                }
                if (script.tablePresentationClosure != null) {
                    it.tablePresentation() { tablePresentationContext ->
                        try {
                            script.tablePresentationClosure.delegate = tablePresentationContext
                            script.tablePresentationClosure()
                        } catch (Exception e) {
                            throw new FailedEvaluationException("presentation", e)
                        }
                    }
                }
            }
        )
    }

    static class FailedEvaluationException extends RuntimeException {
        FailedEvaluationException(String blockName, Throwable cause) {
            super("Failed to evaluate the \"$blockName\" block.", cause)
        }
    }

}
