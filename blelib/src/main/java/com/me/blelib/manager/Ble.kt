package com.me.blelib.manager

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.me.blelib.bean.ConnectInfo
import com.me.blelib.constant.CRC16
import com.me.blelib.constant.Common
import com.me.blelib.constant.Config
import com.me.blelib.constant.Constant
import com.me.blelib.enum.ConnectStatus
import com.me.blelib.enum.DataType
import com.me.blelib.ext.*

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
                else -> { }
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
    fun isBleEnable(context: Context) :Boolean{
        return client.isBleEnable(context)
    }

    //打开蓝牙
    fun turnOnBlueTooth(activity: Activity){
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


    // 发送boot指令
    fun sendCommand(sumByte:Byte=0x01,funByte:Byte=0x01) {
      //  if (isConnected) {
            val buf = ByteArray(14)
            buf[0] = 0xB1.toByte()
            buf[1] = 0x66
            buf[2] = 3+8//n 的范围是[0,8]，因为一帧 CAN 信息最多包含 8 个字 节。
            buf[3] = sumByte //BLE 帧计数 0-255，每发送一帧累加 1，应答信息包含此帧计数， 以确定每一帧的发送状态。
            // can 数据指令01
            buf[4] = funByte //功能码
            buf[5] = 0x42//B Ascii码
            buf[6] = 0x30//0 Ascii码
            buf[7] = 0x20//''Ascii码
            buf[8] = 0x20
            buf[9] = 0x20
            buf[10] =0x20
            buf[11] = 0x20
            val crc = CRC16.MODBUS(buf, 0, buf.size - 2)
            buf[12] = crc.toByte()         //低位
            buf[13] = (crc shr 8).toByte() //高位
            try {
                client.sendData(buf)
                when(funByte){
                    0x01.toByte() -> "发送boot指令 ${buf.toHexString()}".logE()
                    0x02.toByte() -> "发送获取软件信息指令 ${buf.toHexString()}".logE()
                    0x03.toByte() -> "请求seed指令 ${buf.toHexString()}".logE()
                }
            } catch (e: Exception) {
                "发送指令 error.".logE()
                e.printStackTrace()
            }
      //  } else {
       //     "Ble Device isn't connected".logE()
      //  }
    }





}