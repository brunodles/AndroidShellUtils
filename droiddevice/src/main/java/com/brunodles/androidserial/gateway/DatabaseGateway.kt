package com.brunodles.androidserial.gateway

import com.brunodles.androidserial.Device
import com.brunodles.androidserial.Device.Companion.serialEqualsTo
import com.brunodles.androidserial.Device.Companion.updateSelection
import com.brunodles.androidserial.child
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class DatabaseGateway {

    val selectedSerial: String = System.getenv("ANDROID_SERIAL") ?: ""

    private val selectedSerialOutputFile = System.getenv("HOME")
            ?.let { File(it) }
            ?.child(".droiddevice.out")
    private val databaseFile: File? = System.getenv("HOME")
            ?.let { File(it) }
            ?.child(".droiddevice.db")

    private var objectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    private var databaseObject: DatabaseObject? = null

    init {
        checkNotNull(databaseFile) { "Unable to check database file" }
    }

    fun save() {
        databaseObject?.let { objectMapper.writeValue(databaseFile, it) }
        val selectedSerial = databaseObject
                ?.devices
                ?.firstOrNull { it.isSelected }
                ?.serial ?: ""
        selectedSerialOutputFile?.writeText(selectedSerial)
    }

    fun data(): DatabaseObject {
        databaseObject?.let { return it }
        val dataFromDb = dataFromDb()
        if (selectedSerial.isNotBlank()) {
            if (dataFromDb.devices.find { it.serial == selectedSerial } != null)
                dataFromDb.updateDevice(serialEqualsTo(selectedSerial), updateSelection(true))
            else
                dataFromDb.devices += Device(serial = selectedSerial, isSelected = true)
        }
        return dataFromDb
    }

    private fun dataFromDb(): DatabaseObject {
        if (databaseFile?.exists() != false)
            try {
                val databaseObject = objectMapper.readValue(databaseFile, DatabaseObject::class.java)
                this.databaseObject = databaseObject
                return databaseObject
            } catch (e: Exception) {
                println("Failed to load database")
                e.printStackTrace()
            }
        val databaseObject = DatabaseObject()
        this.databaseObject = databaseObject
        return databaseObject
    }

    data class DatabaseObject(
            var devices: List<Device> = emptyList()
    ) {
        fun updateDevice(predicate: (Device) -> Boolean, change: (Device) -> Device, result: ((Device?) -> Unit)? = null) {
            var changedDevice: Device? = null
            devices = devices
                    .map {
                        if (predicate(it)) {
                            val newDevice = change(it)
                            changedDevice = newDevice
                            newDevice
                        } else {
                            it
                        }
                    }
            result?.invoke(changedDevice)
        }
    }
}
