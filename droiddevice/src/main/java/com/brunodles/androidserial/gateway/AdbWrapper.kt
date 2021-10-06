package com.brunodles.androidserial.gateway

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.brunodles.androidserial.Device
import com.brunodles.androidserial.child
import java.io.File

class AdbWrapper() {
    private val androidSdk: File? = System.getenv("ANDROID_HOME")
        ?.let { File(it) }
    private val adbPath: File
    private val adb: AndroidDebugBridge

    init {
        checkNotNull(androidSdk) { "SDK path not specified." }
        check(androidSdk.exists()) { "SDK path does not exist." }

        AndroidDebugBridge.init(false)

        adbPath = androidSdk.child("platform-tools/adb")
        adb = AndroidDebugBridge.createBridge(adbPath.absolutePath, false)
    }

    fun waitForDevices(timeout: Int) {
        print("Wait initial device list")
        val maxTime = System.currentTimeMillis() + timeout
        while (!adb.hasInitialDeviceList()) {
            if (System.currentTimeMillis() > maxTime) {
                print("timeout")
                break
            }
            print(".")
            Thread.sleep(100)
        }
        println()
    }

    fun devices(): List<Device> = adb.devices.map {
        Device(
            serial = it.serialNumber,
            brand = it.getPropertySync("ro.product.brand"),
            model = it.getPropertySync("ro.product.model"),
            product = it.getPropertySync("ro.build.product"),
            isSelected = false,
            state = it.state
        )
    }

    fun connectedState(): Map<String, IDevice.DeviceState> = adb.devices
        .map { Pair(it.serialNumber, it.state) }
        .toMap()
    fun connectedSerials(): List<String> = adb.devices.map { it.serialNumber }

    fun terminate() = AndroidDebugBridge.terminate()
}