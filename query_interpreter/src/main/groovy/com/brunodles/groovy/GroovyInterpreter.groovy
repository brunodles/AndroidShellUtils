package com.brunodles.groovy

import com.brunodles.tablebuilder.ColumnDirection
import com.brunodles.tablebuilder.Format
import com.brunodles.tablebuilder.FormatDefault
import com.brunodles.tablebuilder.TableBuilder
import org.apache.groovy.ginq.provider.collection.runtime.NamedRecord
import org.codehaus.groovy.control.CompilerConfiguration

import static com.brunodles.groovy.ExtensionFunctions.registerCustomExtensionFunctions

class GroovyInterpreter {

    private static String DEFAULT_IMPORTS = """
            import static com.brunodles.tablebuilder.ColumnDirection.*
            import static com.brunodles.tablebuilder.FormatDefault.*
            import com.brunodles.tablebuilder.FormatDefault
            import static com.brunodles.groovy.FormatFunctions.*
            import static java.lang.Math.*
            import java.lang.Math
            import static org.apache.groovy.ginq.GinqGroovyMethods.*
        """.stripIndent()

    private def scriptResult

    private FormatDefault presentationFormat = FormatDefault.valueOf("simple")
    private String workingDir = "./"

    GroovyInterpreter() {
    }

    private void runScript(String[] args) {
        def binding = new Binding()
        def config = new CompilerConfiguration()
//        config.scriptBaseClass =
        config.scriptExtensions += DelegatingScript.class.name

        Map<String, String> params = args.drop(1)
            .findAll{it.contains("=") }
            .collectEntries{
                it.split("=")
            }

        GroovyShell shell = new GroovyShell(this.class.classLoader, binding, config)
        shell.setVariable("args", MyProxy.create(args.drop(1)))
        shell.setVariable("params", MyProxy.create(params))
        shell.setVariable("database", {-> database()})
        shell.setVariable("workingDir", {newPath-> workingDir = newPath})
        shell.setVariable("format", {newFormat-> presentationFormat = FormatDefault.valueOf(newFormat.toString())})

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

        def tableBuilder = new TableBuilder(presentationFormat)
            .columns { columnBlock ->
                resultList.collect {record ->
                    record.nameList.withIndex().collectEntries { name, index ->
                        def direction
                        if (record[index].toString().isNumber())
                            direction = ColumnDirection.right
                        else
                            direction = ColumnDirection.left
                        [index, [name: name, direction: direction]] }
                }.inject { aggregator, value ->
                    aggregator + value
                }.sort()
                    .forEach { index, data ->
                        columnBlock.add(data.name as String, data.direction)
                    }
            }

        resultList.forEach {record ->
            tableBuilder.newRow {rowBlock ->
                record.forEach { cell->
                    rowBlock.add(cell)
                }
            }
        }

        println tableBuilder.build()
    }

    List<File> database() {
        def databaseFileList = new ArrayList<File>()
//        new File("./")
        new File(workingDir)
                .eachFileRecurse { file -> if (file.isFile()) databaseFileList.add(file) }
        return databaseFileList.collect { it.toJson() }
    }

    static void main(String[] args) {
        registerCustomExtensionFunctions()

        def interpreter = new GroovyInterpreter()
        interpreter.runScript(args)
        interpreter.buildPresentationTable()
    }
}
