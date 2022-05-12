package com.me.blelib.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.blankj.utilcode.util.Utils
import com.me.blelib.bean.ConnectInfo
import com.me.blelib.constant.Config

@Suppress("unused")
class BleManager {
    fun init(app: Application) {
        Utils.init(app)
        Ble.init()
    }

    fun setListener(listener: BleDataListener?) {
        Config.dataListener = listener
    }

    fun setLogEnabled(isEnabled: Boolean) {
        Config.isLogEnabled = isEnabled
    }

    fun startScan() {
        Ble.startScan()
    }

    fun stopScan() {
        Ble.stopScan()
    }

    fun isBleEnable(context: Context) :Boolean{
        return Ble.isBleEnable(context)
    }

    //打开蓝牙
    fun turnOnBlueTooth(activity: Activity){
        Ble.turnOnBlueTooth(activity)
    }
    fun connect(info: ConnectInfo) {
        Ble.connect(info)
    }

    fun disconnect() {
        Ble.disconnect()
    }

    fun sendBleQueryCommand() {
        Ble.sendBleQueryCommand()
    }
    fun sendConfigCanCommand() {
        Ble.sendConfigCanCommand()
    }

    fun sendCommand(sumByte:Byte,funByte:Byte) {
        Ble.sendCommand(sumByte,funByte)
    }
    companion object {
        val instance: BleManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BleManager()
        }
    }
}