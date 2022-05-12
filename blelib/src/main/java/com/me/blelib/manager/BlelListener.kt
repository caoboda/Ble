package com.me.blelib.manager

import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.me.blelib.enum.ConnectStatus
import com.me.blelib.enum.DataType

internal interface BlelListener {
    /**
     * 当接收到系统消息
     * @msg ByteArray 收到回复的字节数组
     * @srcType 消息来源 0:Wi-Fi 1:Ble
     * @dataType 数据类型 0:正常通讯数据 1:指令模式通讯数据
     */
    fun onMessageResponseClient(msg: ByteArray, dataType: DataType)

    /**
     * 当服务状态发生变化时触发
     */
    fun onConnectStatusChanged(status: ConnectStatus)

    /**
     * 当蓝牙设备MTU发生变化时触发
     */
    fun onGetMtu(isSuper: Boolean)

    /**
     * 当搜索到蓝牙设备时触发
     */

    fun onDeviceFound(result: BleDevice)

    fun onDeviceFound(result: MutableList<BleDevice>)

    fun onSendFinish()
}