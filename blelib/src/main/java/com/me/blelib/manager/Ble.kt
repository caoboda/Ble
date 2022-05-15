package com.me.blelib.manager

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.utils.ByteUtils
import com.me.blelib.bean.ConnectInfo
import com.me.blelib.constant.CRC16
import com.me.blelib.constant.Config
import com.me.blelib.enum.ConnectStatus
import com.me.blelib.enum.DataType
import com.me.blelib.ext.*
import java.util.*

internal object Ble {
    private val client: BleClient = BleClient.instance
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private var isSuperBle: Boolean = false
    private var isConnected: Boolean = false
    private var isIdleBusy: Boolean = false
    private var hasInfo: Boolean = false


    private val listener: BlelListener = object : BlelListener {
        override fun onMessageResponseClient(msg: ByteArray, dataType: DataType) {
            when (dataType) {
                DataType.Cmd -> {

                }
                DataType.Data -> {

                }
            }
        }

        override fun onConnectStatusChanged(status: ConnectStatus) {
            "Core Connect status changed: $status".logA()
            Config.dataListener?.onConnectStatusChanged(status)
            when (status) {
                ConnectStatus.CONNECTED -> {
                    isConnected = true
                    isIdleBusy = false
                }
                ConnectStatus.DISCONNECTED -> {
                    isConnected = false
                }
                ConnectStatus.PWD_FAILED -> {
                    disconnect()
                }
                ConnectStatus.PWD_TIMEOUT -> {
                    disconnect()
                }
                else -> {}
            }
        }

        override fun onGetMtu(isSuper: Boolean) {
            "Set MTU Succeed: $isSuper".logD()
            isSuperBle = isSuper
        }

        override fun onDeviceFound(result: BleDevice) {
            Config.dataListener?.onDeviceFound(result)
        }

        override fun onDeviceFound(result: MutableList<BleDevice>) {
            Config.dataListener?.onDeviceFound(result)
        }

        override fun onSendFinish() {
            mHandler.postDelayed({

            }, 300)
        }
    }

    fun init() {
        client.setListener(listener)
    }


    fun startScan() {
        client.scan(true)
    }

    fun stopScan() {
        client.scan(false)
    }

    //蓝牙是否打开
    fun isBleEnable(context: Context): Boolean {
        return client.isBleEnable(context)
    }

    //打开蓝牙
    fun turnOnBlueTooth(activity: Activity) {
        client.turnOnBlueTooth(activity)
    }

    fun connect(info: ConnectInfo) {
        hasInfo = false
        isIdleBusy = false
        stopScan()
        client.setDevice(info)
        client.connect()
    }

    fun disconnect() {
        isConnected = false
        client.disconnect()
    }


    // 发送 BLE 查询 CAN 命令
    fun sendBleQueryCommand() {
        if (isConnected) {
            val buf = ByteArray(5)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x65
            buf[2] = 0x02
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[3] = crc.toByte()         //低位
            buf[4] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                "发送BLE查询CAN命令 ${buf.toHexString()}".logE()
            } catch (e: Exception) {
                "发送BLE查询CAN命令 error.".logE()
                e.printStackTrace()
            }
        } else {
            "Ble Device isn't connected".logE()
        }
    }


    // 发送Ble配置CAN命令
    fun sendConfigCanCommand() {
        if (isConnected) {
            val buf = ByteArray(16)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x64
            buf[2] = 0x0d
            buf[3] = 0x02
            buf[4] = 0x01
            buf[5] = 0x01
            buf[6] = 0x35
            buf[7] = 0x35
            buf[8] = 0x04
            buf[9] = 0x01
            buf[10] = 0x00
            buf[11] = 0x35
            buf[12] = 0x35
            buf[13] = 0x04
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[14] = crc.toByte()         //低位
            buf[15] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                "发送Ble配置CAN命令 ${buf.toHexString()}".logE()
            } catch (e: Exception) {
                "发送Ble配置CAN命令 error.".logE()
                e.printStackTrace()
            }
        } else {
            "Ble Device isn't connected".logE()
        }
    }


    // 发送指令
    fun sendCommand(sumNumByte: Byte = 0x01, funByte: Byte = 0x01) {
        if (isConnected) {
            val buf = ByteArray(14)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x66
            buf[2] = 3 + 8//n 的范围是[0,8]，因为一帧 CAN 信息最多包含 8 个字 节。
            buf[3] = sumNumByte //BLE 帧计数 0-255，每发送一帧累加 1，应答信息包含此帧计数， 以确定每一帧的发送状态。
            // can 数据指令01
            buf[4] = funByte //功能码
            buf[5] = 0x42//B Ascii码
            buf[6] = 0x30//0 Ascii码
            buf[7] = 0x20//''Ascii码
            buf[8] = 0x20
            buf[9] = 0x20
            buf[10] = 0x20
            buf[11] = 0x20
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[12] = crc.toByte()         //低位
            buf[13] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                when (funByte) {
                    0x01.toByte() -> "发送boot指令 ${buf.toHexString()}".logE()
                    0x02.toByte() -> "发送获取软件信息指令 ${buf.toHexString()}".logE()
                    0x03.toByte() -> "请求seed指令 ${buf.toHexString()}".logE()
                }
            } catch (e: Exception) {
                "发送指令 error.".logE()
                e.printStackTrace()
            }
        } else {
            "Ble Device isn't connected".logE()
        }
    }

    // 校验key指令
    fun sendValidKeyCommand(sumNumByte: Byte = 0x01, funByte: Byte = 0x01, randomKey: Long) {
        if (isConnected) {
            val buf = ByteArray(14)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x66
            buf[2] = 3 + 8//n 的范围是[0,8]，因为一帧 CAN 信息最多包含 8 个字 节。
            buf[3] = sumNumByte //BLE 帧计数 0-255，每发送一帧累加 1，应答信息包含此帧计数， 以确定每一帧的发送状态。
            // can 数据指令01
            buf[4] = funByte //功能码
            buf[5] = 0x00
            buf[6] = (randomKey shr 0).toByte()
            buf[7] = (randomKey shr 8).toByte()
            buf[8] = (randomKey shr 16).toByte()
            buf[9] = (randomKey shr 24).toByte()
            buf[10] = 0x00
            buf[11] = 0x00
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[12] = crc.toByte()         //低位
            buf[13] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                "校验key指令 ${buf.toHexString()}".logE()
            } catch (e: Exception) {
                "发送指令 error.".logE()
                e.printStackTrace()
            }
        } else {
            "Ble Device isn't connected".logE()
        }
    }


    // 编程日期指令
    fun sendDateCommand(sumNumByte: Byte = 0x01, funByte: Byte = 0x01) {
        if (isConnected) {
            val buf = ByteArray(14)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x66
            buf[2] = 3 + 8//n 的范围是[0,8]，因为一帧 CAN 信息最多包含 8 个字 节。
            buf[3] = sumNumByte //BLE 帧计数 0-255，每发送一帧累加 1，应答信息包含此帧计数， 以确定每一帧的发送状态。
            // can 数据指令01
            buf[4] = funByte //功能码
            buf[5] = 0x00
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = (calendar.get(Calendar.MONTH) + 1)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            var hour = calendar.get(Calendar.HOUR_OF_DAY)
            var minute = calendar.get(Calendar.MINUTE)
            var second = calendar.get(Calendar.SECOND)
            ("年: $year").logE()
            ("月: $month").logE()
            ("日: $day").logE()
            ("时: $hour").logE()
            ("分: $minute").logE()
            //  ("秒: $second").logE()
            buf[6] = (year shr 8).toByte()
            buf[7] = (year and 0x00ff).toByte()
            buf[8] = month.toByte()
            buf[9] = day.toByte()
            buf[10] = hour.toByte()
            buf[11] = minute.toByte()
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[12] = crc.toByte()         //低位
            buf[13] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                "编程日期指令 ${buf.toHexString()}".logE()
            } catch (e: Exception) {
                "发送指令 error.".logE()
                e.printStackTrace()
            }
        } else {
            "Ble Device isn't connected".logE()
        }
    }

    // 发送数据长度与地址指令
    fun sendDataBlockCommand(sumNumByte: Byte = 0x01, funByte: Byte = 0x01, data_frame: String) {
        var hexDataFrame= data_frame.replace(" ","")
        Log.e("TAG", " hexDataFrame= $hexDataFrame")
        val hexDataFrameBytes=ByteUtils.hexStr2Bytes(hexDataFrame)
        if (isConnected) {
            val buf = ByteArray(14)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x66
            buf[2] = 3 + 8//n 的范围是[0,8]，因为一帧 CAN 信息最多包含 8 个字 节。
            buf[3] = sumNumByte //BLE 帧计数 0-255，每发送一帧累加 1，应答信息包含此帧计数， 以确定每一帧的发送状态。
            // can 数据指令01
            buf[4] =  hexDataFrameBytes[0] //功能码
            buf[5] = hexDataFrameBytes[1]
            buf[6] =  hexDataFrameBytes[2]
            buf[7] =  hexDataFrameBytes[3]
            buf[8] =  hexDataFrameBytes[4]
            buf[9] =  hexDataFrameBytes[5]
            buf[10] = hexDataFrameBytes[6]
            buf[11] = hexDataFrameBytes[7]
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[12] = crc.toByte()         //低位
            buf[13] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                "发送数据块指令 ${buf.toHexString()}".logE()
            } catch (e: Exception) {
                "发送指令 error.".logE()
                e.printStackTrace()
            }
        } else {
            "Ble Device isn't connected".logE()
        }
    }

    // 发送数据长度与地址指令
    fun sendLengthAndAddressCommand(sumNumByte: Byte = 0x01, funByte: Byte = 0x01, address: String) {
        var hexAddrss= address.replace(" ","")
        Log.e("TAG", " hexAddrss= $hexAddrss")
        val addressBytes=ByteUtils.hexStr2Bytes(hexAddrss)
        if (isConnected) {
            val buf = ByteArray(14)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x66
            buf[2] = 3 + 8//n 的范围是[0,8]，因为一帧 CAN 信息最多包含 8 个字 节。
            buf[3] = sumNumByte //BLE 帧计数 0-255，每发送一帧累加 1，应答信息包含此帧计数， 以确定每一帧的发送状态。
            // can 数据指令01
            buf[4] =  addressBytes[0] //功能码
            buf[5] = addressBytes[1]
            buf[6] =  addressBytes[2]
            buf[7] =  addressBytes[3]
            buf[8] =  addressBytes[4]
            buf[9] =  addressBytes[5]
            buf[10] = addressBytes[6]
            buf[11] = addressBytes[7]
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[12] = crc.toByte()         //低位
            buf[13] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                "发送数据长度与地址指令 ${buf.toHexString()}".logE()
            } catch (e: Exception) {
                "发送指令 error.".logE()
                e.printStackTrace()
            }
        } else {
            "Ble Device isn't connected".logE()
        }
    }

}