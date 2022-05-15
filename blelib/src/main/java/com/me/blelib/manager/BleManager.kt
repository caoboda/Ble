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

    fun sendCommand(sumNumByte:Byte=0x01,funByte:Byte) {
        Ble.sendCommand(sumNumByte,funByte)
    }

    fun sendValidKeyCommand(sumNumByte:Byte=0x01,funByte:Byte,randomKey:Long) {
        Ble.sendValidKeyCommand(sumNumByte,funByte,randomKey)
    }

    fun sendDateCommand(sumNumByte:Byte=0x01,funByte:Byte) {
        Ble.sendDateCommand(sumNumByte,funByte)
    }

    fun sendLengthAndAddressCommand(sumNumByte:Byte=0x01,funByte:Byte,address: String) {
        Ble.sendLengthAndAddressCommand(sumNumByte,funByte,address)
    }

    fun sendDataBlockCommand(sumNumByte:Byte=0x01,funByte:Byte,data_frame: String) {
        Ble.sendDataBlockCommand(sumNumByte,funByte,data_frame)
    }
    companion object {
        val instance: BleManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BleManager()
        }
    }
}