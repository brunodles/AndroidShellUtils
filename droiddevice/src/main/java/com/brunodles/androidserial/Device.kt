package com.brunodles.androidserial

import com.android.ddmlib.IDevice

data class Device(
        val alias: String = "",
        val serial: String,
        val brand: String = "Unknown",
        val model: String = "Unknown",
        val product: String = "Unknown",
        @Transient
        val isSelected: Boolean = false,
        @Transient
        var state: IDevice.DeviceState = IDevice.DeviceState.OFFLINE
) {
    fun aliasOrModel(): String {
        if (alias.isNotBlank())
            return alias
        return model
    }

    companion object {
        fun aliasEqualsTo(alias: String): (Device) -> Boolean = { it.alias == alias }
        fun serialEqualsTo(serial: String): (Device) -> Boolean = { it.serial == serial }
        fun addAlias(alias: String): (Device) -> Device = { it.copy(alias = alias) }
        fun updateSelection(isSelected: Boolean): (Device) -> Device = { it.copy(isSelected = isSelected) }
        fun updateSelection(): (Device, Boolean) -> Device = { device, selection -> device.copy(isSelected = selection) }
    }
}