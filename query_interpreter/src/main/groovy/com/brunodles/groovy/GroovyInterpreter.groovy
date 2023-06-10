package com.brunodles.groovy

import com.brunodles.tablebuilder.ColumnDirection
import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.FormatDefaultImplementation
import com.brunodles.tablebuilder.TableBuilder
import org.apache.groovy.ginq.provider.collection.runtime.NamedRecord
import org.codehaus.groovy.control.CompilerConfiguration

import static com.brunodles.groovy.ExtensionFunctions.first
import static com.brunodles.groovy.ExtensionFunctions.registerCustomExtensionFunctions
import static com.brunodles.groovy.ExtensionFunctions.tryOrNull

class GroovyInterpreter {

    private static String DEFAULT_IMPORTS = """
            import static com.brunodles.tablebuilder.ColumnDirection.*
            import static com.brunodles.tablebuilder.FormatDefault.*
            import com.brunodles.tablebuilder.FormatDefault
            import static com.brunodles.tablebuilder.FormatDefaultImplementation.*
            import com.brunodles.tablebuilder.FormatDefaultImplementation
            import static com.brunodles.groovy.FormatFunctions.*
            import static com.brunodles.groovy.ExtensionFunctions.*
            import static java.lang.Math.*
            import java.lang.Math
            import static org.apache.groovy.ginq.GinqGroovyMethods.*
        """.stripIndent()

    private def scriptResult

    private Format presentationFormat = FormatDefaultImplementation.Simple.INSTANCE
    private String workingDir = "./"

    GroovyInterpreter() {
    }

    private void runScript(String[] args) {
        def binding = new Binding()
        def config = new CompilerConfiguration()
//        config.scriptBaseClass =
        config.scriptExtensions += DelegatingScript.class.name

        Map<String, String> params = args.drop(1)
            .findAll { it.contains("=") }
            .collectEntries {
                it.split("=")
            }

        GroovyShell shell = new GroovyShell(this.class.classLoader, binding, config)
        shell.setVariable("args", MyProxy.create(args.drop(1)))
        shell.setVariable("params", MyProxy.create(params))
        shell.setVariable("database", { -> database() })
        shell.setVariable("workingDir", { newPath -> workingDir = newPath })
        shell.setVariable("format", { newFormat ->
            presentationFormat = first(
                    {
                        if (newFormat instanceof Format)
                            return newFormat as Format
                        else if (newFormat instanceof Class<FormatDefaultImplementation>)
                            return newFormat.INSTANCE
                        else if (newFormat instanceof FormatDefaultImplementation)
                            return newFormat
                        else
                            return null
                    },
                    {
                        if (newFormat instanceof String)
                            return tryOrNull { FormatDefaultImplementation.valueOf(newFormat) } ?:
                                    tryOrNull { FormatDefault.valueOf(newFormat.toString()) }
                        else
                            return null
                    },
                    { FormatDefaultImplementation.valueOf(newFormat.toString()) },
                    { FormatDefault.valueOf(newFormat.toString()) },
                    {
                        println "Failed to find format \"${newFormat.toString()}\". Defaulting to 'simple'."
                        FormatDefaultImplementation.Simple.INSTANCE
                    }
            )
        })

        def file = new File(args.first())
        def fileContent = file
                .readLines()
                .findAll { !it.startsWith("#") && !it.startsWith("//") }
                .join("\n")

        def scriptStr = "${DEFAULT_IMPORTS}\n${fileContent}"
        Script script = shell.parse(scriptStr)
//        script.delegate = scriptDelegate

        scriptResult = script.run()
    }

    private void buildPresentationTable() {
        List<NamedRecord> resultList = scriptResult.toList()

        if (resultList.isEmpty()) {
            println "Empty. No data available."
            return
        }

        def tableBuilder = new TableBuilder(presentationFormat)
            .columns { columnBlock ->
                resultList.collect { record ->
                    if (record == null)
                        return null
                (record.nameList ?: (0..record.size()).collect { "col-$it" })
                        .withIndex().collectEntries { name, index ->
                        def direction
                        if (record[index].toString().isNumber())
                            direction = ColumnDirection.right
                        else
                            direction = ColumnDirection.left
                        [index, [name: name, direction: direction]]
                    }
                }.inject { aggregator, value ->
                    aggregator + value
                }.sort()
                    .forEach { index, data ->
                        columnBlock.add(data.name as String, data.direction)
                    }
            }

        resultList.forEach { record ->
            tableBuilder.newRow { rowBlock ->
                record.forEach { cell ->
                    rowBlock.add(cell)
                }
            }
        }

        println tableBuilder.build()
    }

    List<Object> database() {
        def databaseFileList = new ArrayList<File>()
//        new File("./")
        new File(workingDir)
            .eachFileRecurse { file -> if (file.isFile()) databaseFileList.add(file) }
        return databaseFileList.collect { file ->
            first(
                { file.readCsv() },
                { List.of(file.readJson()) },
                { List.of(file.readYaml()) }
            ) ?: Collections.emptyList()
        }.flatten()
    }

    static void main(String[] args) {
        registerCustomExtensionFunctions()

        def interpreter = new GroovyInterpreter()
        interpreter.runScript(args)
        interpreter.buildPresentationTable()
    }
}
