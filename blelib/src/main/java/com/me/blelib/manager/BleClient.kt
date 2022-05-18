package com.me.blelib.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Handler
import android.os.Looper
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.BleStates.ConnectException
import cn.com.heaton.blelibrary.ble.BleStates.ConnectTimeOut
import cn.com.heaton.blelibrary.ble.callback.*
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.me.blelib.bean.ConnectInfo
import com.me.blelib.constant.Common
import com.me.blelib.constant.Config
import com.me.blelib.constant.Constant
import com.me.blelib.enum.ConnectStatus
import com.me.blelib.enum.ScanStatus
import com.me.blelib.ext.*
import com.me.blelib.ext.logD
import com.me.blelib.ext.logE
import com.me.blelib.ext.logW
import com.me.blelib.ext.toHexString
import java.nio.charset.StandardCharsets

internal class BleClient {
    private var listener: BlelListener? = null
    private var ble: Ble<BleDevice> = Ble.getInstance()
    private var mDevice: BleDevice? = null
   // private val dataBuffer = DataBuffer.instance
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var info: ConnectInfo = ConnectInfo()
    private var fail: Int = 0
    private val failMax: Int = 3
    private var isExit: Boolean = false
    private var isConnected: Boolean = false


    fun setListener(listener: BlelListener?) {
        this.listener = listener
      //  SubPackageOnce.instance.setListener(listener)
    }

    fun scan(isStart: Boolean) {
        if (isStart) {
            try {
                if (!ble.isBleEnable && ble.isScanning) {
                    return
                }
                isConnected = false
                ble.disconnectAll()
                ble.startScan(scanCallback, 10000L)
                handler.postDelayed(scanRunnable, 10000)
                Config.dataListener?.onScanStatusChanged(ScanStatus.Scanning)
            } catch (e: Exception) {
                "${Common.now()} Ble startScan $e".logE()
            }
        } else {
            try {
                if (!ble.isScanning) {
                    return
                }
                ble.stopScan()
                handler.removeCallbacks(scanRunnable)
            } catch (e: Exception) {
                "${Common.now()} Ble stopScan $e".logE()
            }
        }
    }

    private val scanRunnable = Runnable {
        if (ble.isScanning) {
            scan(false)
        }
    }

    //蓝牙是否打开
    fun isBleEnable(context: Context):Boolean{
       return ble.isSupportBle(context) && ble.isBleEnable
    }

    //打开蓝牙
    fun turnOnBlueTooth(activity: Activity){
        ble.turnOnBlueTooth(activity)
    }

    fun connect() {
        if (info.address.isEmpty()) {
            listener?.onConnectStatusChanged(ConnectStatus.CONNECT_FAIL_TYPE_NOT_DEVICE)
            return
        }
        if (isConnected) {
            return
        }
        fail = 0
        isExit = false
      //  dataBuffer.init()
        for (i in ble.connectedDevices) {
            removeDevice(i.bleAddress)
        }
        listener?.onConnectStatusChanged(ConnectStatus.CONNECTING)
        val clientThread = object : Thread("client-Ble") {
            override fun run() {
                super.run()
                connectServer()
            }
        }
        clientThread.start()
    }

    private fun connectServer() {
        synchronized(BleClient::class.java) {
            if (!isConnected) {
                try {
                    ble.connect(info.address, connectCallback)
                } catch (e: Exception) {
                    "${Common.now()} ConnectThread $e".logE()
                }
            }
        }
    }

    fun disconnect() {
        "${Common.now()} disconnect".logV()
//        isConnected = false
        isExit = true
        ble.disconnectAll()
    }

    fun setDevice(v: ConnectInfo) {
        this.info = v
    }


    fun sendData(data: ByteArray ) {
        mDevice?.apply {
            ble.writeByUuid(mDevice, data, Constant.DATA_SERVICE, Constant.DATA_TX,bleWriteCallback)
        }
    }

   private val  bleWriteCallback = object : BleWriteCallback<BleDevice>(){

        override fun onWriteSuccess(device: BleDevice?,characteristic: BluetoothGattCharacteristic?) {
            //  "命令返回成功".logE()
        }

        override fun onWriteFailed(device: BleDevice?, failedCode: Int) {
            super.onWriteFailed(device, failedCode)
            " Ble data write failed: failCode".logE()
        }
    }

    fun sendData(data: ByteArray, isCmd: Boolean): Boolean {
        "${Common.now()} isCmd: $isCmd SendData: ${data.toHexString()}".logI()
        var result = false
        mDevice?.let {
            if (isCmd) {
                ble.writeByUuid(mDevice, data, Constant.CMD_SERVICE, Constant.AT_TX, object : BleWriteCallback<BleDevice>() {
                    override fun onWriteSuccess(device: BleDevice?, characteristic: BluetoothGattCharacteristic?) {
                        result = true
                    }

                    override fun onWriteFailed(device: BleDevice?, failedCode: Int) {
                        super.onWriteFailed(device, failedCode)
                        "${Common.now()} Ble cmd write failed: failCode".logE()
                    }
                })
            } else {
                if (data.size > 20) {
                    ble.writeEntity(mDevice, data, 20, 30, object : BleWriteEntityCallback<BleDevice>() {
                        override fun onWriteSuccess() {
                            result = true
                        }

                        override fun onWriteFailed() {
                            "${Common.now()} Ble data write failed".logE()
                        }

                        override fun onWriteProgress(progress: Double) {
                            if (progress == 1.0) {
                                "${Common.now()} Send big data: $progress".logD()
                                /*if (data[0] == Constant.HEAD_ESC && data[1] == Constant.CMD_UPDATE_FM) {
                                    listener?.onSendFinish()
                                }*/
                            }
                        }
                    })
                } else {
                    ble.writeByUuid(mDevice, data, Constant.DATA_SERVICE, Constant.DATA_TX, object : BleWriteCallback<BleDevice>() {
                        override fun onWriteSuccess(device: BleDevice?, characteristic: BluetoothGattCharacteristic?) {
                            result = true
                        }

                        override fun onWriteFailed(device: BleDevice?, failedCode: Int) {
                            super.onWriteFailed(device, failedCode)
                            "${Common.now()} Ble data write failed: failCode".logE()
                        }
                    })
                }
            }
        }
        return result
    }

    private val scanCallback = object : BleScanCallback<BleDevice>() {
        override fun onLeScan(device: BleDevice?, rssi: Int, scanRecord: ByteArray?) {
            device?.let {
                if (it.bleName != null && it.bleAddress != null && it.bleName.isNotEmpty() && it.bleAddress.isNotEmpty()) {
                    "Ble Found : ${it.bleName}  address: ${it.bleAddress}".logD()
                    listener?.onDeviceFound(it)
                }
            }
        }

        override fun onStop() {
            super.onStop()
            Config.dataListener?.onScanStatusChanged(ScanStatus.Stopped)
        }
    }

    private val connectCallback = object : BleConnectCallback<BleDevice>() {
        override fun onConnectionChanged(device: BleDevice?) {
            device?.let {
                when {
                    it.isConnected -> {
                        "${Common.now()} onConnectionChanged isConnected".logD()
                        isConnected = true
                        mDevice = it
                    }
                    it.isDisconnected -> {
                        "${Common.now()} onConnectionChanged isDisconnected : $isConnected".logD()
                        if (isConnected) {
                            isConnected = false
                            listener?.onConnectStatusChanged(ConnectStatus.DISCONNECTED)
                        }
                        mDevice = null
                    }
                    it.isConnecting -> {
                        "${Common.now()}  CONNECTING".logD()
                    }
                }
            }
        }

        override fun onReady(device: BleDevice?) {
            super.onReady(device)
            //该方法一般是在发现服务后，进行设置的，设置该方法的目的是让硬件在数据改变的时候，发送数据给app，app则通过onCharacteristicChanged方法回调给用户，从参数中可获取到回调回来的数据。
            ble.enableNotify(device, true, notifyCallback)
//            ble.enableNotifyByUuid(device, true, Constant.CMD_SERVICE, Constant.AT_RX, notifyCallback)
//            ble.enableNotifyByUuid(device, true, Constant.DATA_SERVICE, Constant.DATA_RX, notifyCallback)
            ble.setMTU(info.address, 200, object : BleMtuCallback<BleDevice>() {
                override fun onMtuChanged(device: BleDevice?, mtu: Int, status: Int) {
                    super.onMtuChanged(device, mtu, status)
                    listener?.onGetMtu(mtu > 100)
                    "${Common.now()} Device: ${device?.bleName}  MTU: $mtu  Status: $status".logD()
                }
            })
        }

        override fun onConnectCancel(device: BleDevice?) {
            super.onConnectCancel(device)
            "${Common.now()} onConnectCancel".logD()
        }

        override fun onConnectFailed(device: BleDevice?, errorCode: Int) {
            super.onConnectFailed(device, errorCode)
            "${Common.now()} onConnectFailed Name: ${device?.bleName}, error: $errorCode".logD()
            if (errorCode == ConnectException || errorCode == ConnectTimeOut) {
                listener?.onConnectStatusChanged(ConnectStatus.DISCONNECTED)
            } else {
                if (fail < failMax) {
                    fail++
                    ble.connect(device, this)
                } else {
                    disconnect()
                }
            }
        }
    }

    private val notifyCallback = object : BleNotifyCallback<BleDevice>() {
        override fun onChanged(device: BleDevice?, characteristic: BluetoothGattCharacteristic?) {
            characteristic?.let {
                when (it.uuid) {
                    Constant.AT_RX -> {
                        "CMD received from ${device?.bleAddress}, data: ${ it.value.toHexString() }, string: ${ String(it.value, StandardCharsets.UTF_8) }".logD()
                        handler.removeCallbacksAndMessages(null)
                        Config.dataListener?.onResponseData(it.value)
                        val result = String(it.value, StandardCharsets.UTF_8)
                        when {
                            result.contains("OK+PWD:Y") || result.contains("ERR+AT") -> {
                                listener?.onConnectStatusChanged(ConnectStatus.CONNECTED)
                            }
                            result.contains("OK+PWD:N") -> {
                                listener?.onConnectStatusChanged(ConnectStatus.PWD_FAILED)
                            }

                            else -> {
                                "Ble result : $result".logD()
                            }
                        }
                    }
                    Constant.DATA_RX -> {
                        "${Common.now()} BLE Receive: ${it.value?.toHexString()}".logD()
                      //  dataBuffer.append(it.value)
                        while (true) {
                            //val ret = SubPackageOnce.instance.subData()
                           // if (!ret) break
                        }
                    }
                    else -> {
                        "${Common.now()} BLE Receive error UUID: ${it.uuid}  DATA: ${it.value?.toHexString()}".logE()
                    }
                }
            }
        }

        override fun onNotifySuccess(device: BleDevice?) {
            super.onNotifySuccess(device)
            if (isConnected) {
                listener?.onConnectStatusChanged(ConnectStatus.CONNECTED)
               // sendData("AT+PWD[${info.pwd}]".toByteArray(), true)
               // handler.postDelayed(timeoutRunnable, 300)
            }
        }
    }

    private fun removeDevice(address: String) {
        val oldDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
        oldDevice?.let {
            if (it.bondState == BluetoothDevice.BOND_BONDED) {
                try {
                    val removeBond = it.javaClass.getDeclaredMethod("removeBond")
                    removeBond.invoke(it)
                    "${Common.now()} remove BLE Device success".logD()
                } catch (ex: Exception) {
                    "${Common.now()} remove BLE Device error".logE()
                }
            }
        }
    }

    private val timeoutRunnable = object : Runnable {
        override fun run() {
            "${Common.now()} ble pwd timeout".logW()
            fail++
            if (fail <= failMax) {
                sendData("AT+PWD[${info.pwd}]".toByteArray(), true)
                handler.postDelayed(this, 500)
            } else {
                fail = 0
                listener?.onConnectStatusChanged(ConnectStatus.PWD_TIMEOUT)
            }
        }
    }

    companion object {
        val instance: BleClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BleClient()
        }
    }
}