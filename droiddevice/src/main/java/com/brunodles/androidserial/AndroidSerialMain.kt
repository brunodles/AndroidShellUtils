package com.brunodles.androidserial

import com.brunodles.androidserial.Device.Companion.addAlias
import com.brunodles.androidserial.Device.Companion.aliasEqualsTo
import com.brunodles.androidserial.Device.Companion.serialEqualsTo
import com.brunodles.androidserial.Device.Companion.updateSelection
import com.brunodles.androidserial.gateway.AdbWrapper
import com.brunodles.androidserial.gateway.DatabaseGateway
import com.brunodles.tablebuilder.TableBuilder
import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import java.util.*

private val APP_NAME = "droid-device"

fun main(args: Array<String>) {
    val database = DatabaseGateway()
    val adbWrapper = AdbWrapper()

    AndroidSerial(CommandDependencies(database, adbWrapper)).main(args)

    database.save()
    adbWrapper.terminate()
}

class AndroidSerial(val dependencies: Dependencies) : CliktCommand(
        help = "A tool to control current selected android for terminal",
        invokeWithoutSubcommand = true,
        autoCompleteEnvvar = "_ANDROID_SERIAL_COMPLETE"
) {
    private val listDevices = ListDevices(dependencies)
    val adb by option("--adb", help = "Enable or disable Adb interactions")
            .flag("--no-adb", default = true)

    init {
        subcommands(
                listDevices,
                Alias(dependencies),
                Select(dependencies, listDevices),
                Deselect(dependencies, listDevices),
                Interactive(dependencies, listDevices),
                CompletionHelper(dependencies)
        )
    }

    override fun run() {
        val subcommand = currentContext.invokedSubcommand
        val ignoreAdb = subcommand is CompletionHelper
        if (adb && !ignoreAdb)
            dependencies.adbWrapper.waitForDevices(1000)
        if (subcommand == null) {
            listDevices.run()
        }
    }
}

class CompletionHelper(val dependencies: Dependencies) : CliktCommand(
        help = "Completion Helper, not intended to be used",
) {
    val data by option(help = "Which data will be presented")
            .choice("alias", "serial")
            .required()
    val filters by option("--filter", help = "Filter selected")
            .choice("selected", "unselected", "aliased", "aliasless")
            .multiple()
    val separator by option("-s", "--separator")
            .default(" ")

    override fun run() {
        var devices = dependencies.devices()
        filters.forEach { filter ->
            devices = when (filter) {
                "selected" -> devices.filter { it.isSelected }
                "unselected" -> devices.filter { !it.isSelected }
                "aliased" -> devices.filter { it.alias.isNotBlank() }
                "aliasless" -> devices.filter { it.alias.isBlank() }
                else -> devices
            }
        }
        val result = when (data) {
            "alias" -> devices.filter { it.alias.isNotBlank() }.map { it.alias }
            "serial" -> devices.map { it.serial }
            "selected" -> devices.map { it.serial }
            else -> emptyList()
        }
        println(result.joinToString(separator))
    }
}

class ListDevices(val dependencies: Dependencies) : CliktCommand(
        name = "list",
        help = "List devices"
) {
    override fun run() {
        println("List devices")
        val tableBuilder = TableBuilder()
                .columns {
                    add("Alias")
                    add("Serial")
                    add("State")
                    add("Selected")
                    add("General Info")
                }
        dependencies.devices().forEach {
            tableBuilder.newRow {
                add(it.alias)
                add(it.serial)
                add(it.state)
                add(it.isSelected)
                add("${it.brand} ${it.model} ${it.product}")
            }
        }
        println(tableBuilder.build())
    }
}

class Alias(val dependencies: Dependencies) : CliktCommand(
        name = "alias",
        help = "Create an alias for device"
) {
    val serial: String by argument(help = "Serial to be aliased",
            completionCandidates = CompletionCandidates.Custom.fromStdout(
                    "$APP_NAME completion-helper --data=serial --filter=aliasless"
            ))
    val alias: String by argument(help = "Alias that define this device")

    override fun run() {
        dependencies.updateDevice(serialEqualsTo(serial), addAlias(alias)) { device ->
            device?.let {
                println("Alias created for device.")
                println("\t$alias = ${it.serial}")
            } ?: run {
                println("Device not found.")
            }
        }
    }
}

class Select(val dependencies: Dependencies, val listDevices: ListDevices) : CliktCommand(
        name = "select",
        help = "Select this device"
) {
    val alias: String by argument(help = "Device alias",
            completionCandidates = CompletionCandidates.Custom.fromStdout(
                    "$APP_NAME completion-helper --data=alias --filter=aliased --filter=unselected"
            ))

    override fun run() {
        dependencies.updateDevices(aliasEqualsTo(alias), updateSelection()) { device ->
            device?.let {
                println("Selected ${device.alias} - ${device.serial}")
            }
        }
        listDevices.run()
    }
}

class Deselect(val dependencies: Dependencies, val listDevices: ListDevices) : CliktCommand(
        name = "deselect",
        help = "Deselect this device"
) {
    val alias: String by argument(help = "Device alias", completionCandidates = CompletionCandidates.Custom.fromStdout(
            "$APP_NAME completion-helper --data=alias --filter=aliased --filter=selected"
    ))

    override fun run() {
        dependencies.updateDevices(aliasEqualsTo(alias), updateSelection()) { device ->
            device?.let {
                println("Deselected ${device.alias} - ${device.serial}")
            }
        }
        listDevices.run()
    }
}

class Interactive(val dependencies: Dependencies, val listDevices: ListDevices) : CliktCommand(
        name = "interactive",
        help = "Interactive mode"
) {
    override fun run() {
        interactiveRun()
        listDevices.run()
    }

    private fun interactiveRun() {
        val devices = dependencies.devices()
        println("Found ${devices.size} devices.")
        val scanner = Scanner(System.`in`)
        devices.forEachIndexed { index, device ->
            do {
                print(
                        """
                            Device $index - ${device.alias}
                                -serial  : ${device.serial}
                                -brand   : ${device.brand}
                                -model   : ${device.model}
                                -product : ${device.product}
                                
                                -state   : ${device.state}
                                -selected: ${device.isSelected}
                                
                            Do you want to use this device? [y,n,s,d,q,h]: 
                            """.trimIndent()
                )
                val response = validateOptions(scanner.nextLine(), "ynsdqh")
                val repeat = when (response) {
                    'y' -> {
                        dependencies.updateDevice(serialEqualsTo(device.serial), updateSelection(true))
                        false
                    }
                    'n' -> {
                        dependencies.updateDevice(serialEqualsTo(device.serial), updateSelection(false))
                        false
                    }
                    's' -> {
                        false
                    }
                    'd' -> {
                        dependencies.removeDevice(serialEqualsTo(device.serial))
                        false
                    }
                    'q' -> return
                    'h' -> {
                        printHelp()
                        true
                    }
                    else -> {
                        println("Invalid response.")
                        printHelp()
                        true
                    }
                }
            } while (repeat)
        }
    }

    private fun printHelp() {
        println("""
            Help
                y - select
                n - deselect
                s - skip
                d - delete
                q - quit
                h - show this help
        """.trimIndent())
    }

    @Suppress("SameParameterValue")
    private fun validateOptions(response: String, options: String): Char? {
        if (response.isBlank())
            return null
        options.forEach { option ->
            if (response.equals(option.toString(), true))
                return option.toLowerCase()
        }
        return null
    }
}

interface Dependencies {
    val databaseGateway: DatabaseGateway
    val adbWrapper: AdbWrapper

    fun devices(): List<Device>

    fun updateDevice(predicate: (Device) -> Boolean, change: (Device) -> Device, result: ((Device?) -> Unit)? = null)

    fun updateDevices(predicate: (Device) -> Boolean, change: (Device, Boolean) -> Device, result: ((Device?) -> Unit)? = null)

    fun removeDevice(predicate: (Device) -> Boolean)
}
