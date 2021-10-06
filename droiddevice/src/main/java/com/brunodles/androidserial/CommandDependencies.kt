package com.brunodles.androidserial

import com.android.ddmlib.IDevice
import com.brunodles.androidserial.gateway.AdbWrapper
import com.brunodles.androidserial.gateway.DatabaseGateway

class CommandDependencies(
        override val databaseGateway: DatabaseGateway,
        override val adbWrapper: AdbWrapper
) : Dependencies {

    private var isDeviceListUpdated = false

    override fun devices(): List<Device> {
        if (isDeviceListUpdated)
            return databaseGateway.data().devices
        val connectedState = adbWrapper.connectedState()
        val adbDevices = adbWrapper.devices()
        val dbDevices = databaseGateway.data().devices
        val mergedDevices = (dbDevices + adbDevices)
                .distinctBy { it.serial }
                .map {
                    it.copy(
                            state = connectedState.getOrDefault(it.serial, IDevice.DeviceState.OFFLINE),
                            isSelected = databaseGateway.selectedSerial == it.serial
                    )
                }
        databaseGateway.data().devices = mergedDevices
        isDeviceListUpdated = true
        return mergedDevices
    }

    override fun updateDevice(
            predicate: (Device) -> Boolean,
            change: (Device) -> Device,
            result: ((Device?) -> Unit)?
    ) = databaseGateway.data().updateDevice(predicate, change, result)

    override fun updateDevices(
            predicate: (Device) -> Boolean,
            change: (Device, Boolean) -> Device,
            result: ((Device?) -> Unit)?
    ) {
        databaseGateway.data().devices = devices()
                .map { change(it, predicate(it)) }
    }

    override fun removeDevice(predicate: (Device) -> Boolean) {
        databaseGateway.data().devices = devices()
                .filterNot(predicate)
    }
}
