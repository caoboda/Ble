@file:Suppress("unused")

package com.me.blelib.manager

import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.me.blelib.enum.ConnectStatus
import com.me.blelib.enum.ScanStatus

open class BleDataListener {
    /**
     * 当蓝牙扫描状态发生变化时触发
     */
    open fun onScanStatusChanged(status: ScanStatus) {}

    /**
     * 当服务状态发生变化时触发
     */
    open fun onConnectStatusChanged(status: ConnectStatus) {}

    /**
     * 当搜索到蓝牙设备时触发
     */
    open fun onDeviceFound(result: BleDevice) {}

    open fun onDeviceFound(result: MutableList<BleDevice>) {}

    open fun onResponseData(resultBytes: ByteArray) {}


}