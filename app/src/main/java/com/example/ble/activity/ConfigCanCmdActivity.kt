package com.example.ble.activity

import android.bluetooth.BluetoothGattCharacteristic
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.blankj.utilcode.util.LogUtils
import com.example.ble.R
import com.example.ble.base.BaseActivity
import com.example.ble.databinding.ActivityConfigCancmdBinding
import com.example.ble.util.DataUtils
import com.example.ble.view.DialogManager
import com.me.blelib.constant.Common
import com.me.blelib.enum.ConnectStatus
import com.me.blelib.enum.ScanStatus
import com.me.blelib.ext.logE
import com.me.blelib.ext.toHexString
import com.me.blelib.manager.BleDataListener
import com.me.blelib.manager.BleManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.experimental.and
import kotlin.experimental.xor

class ConfigCanCmdActivity : BaseActivity<ActivityConfigCancmdBinding>() {


    override fun initView() {
        mBinding.commonTitle.setTitle("配置CAN命令")
        mBinding.commonTitle.setLeftIcon(R.mipmap.left_black_icon)
      //  mBinding.hardwareSettingView.setTitle("硬件版本")
    }

    override fun initData() {
        BleManager.instance.setListener(bleDataListener)
    }


    private val bleDataListener = object : BleDataListener() {

        override fun onResponseData(resultBytes: ByteArray) {
            super.onResponseData(resultBytes)
            "BLE设备返回数据: ${resultBytes.toHexString()}".logE()
            if(resultBytes[10] == 0x03.toByte() && resultBytes[11] ==0x00.toByte()){
                if(resultBytes.size==20){
                    //倒叙
                    val seedTemp132=  (resultBytes[15].toInt() shl 24) + (resultBytes[14].toInt() shl 16) + (resultBytes[13].toInt() shl 8) + (resultBytes[12].toInt() shl 0)
                    "seedTemp132= $seedTemp132".logE()

                    val seedTemp32 =  (((resultBytes[15] xor resultBytes[0]) and 0x00ff.toByte()).toInt() shl 24) + (((resultBytes[14] xor resultBytes[0]) and 0x00ff.toByte()).toInt() shl 16) + (((resultBytes[14] xor resultBytes[0]) and 0x00ff.toByte()).toInt() shl 8) + (resultBytes[12].toInt() shl 0)
                    "seedTemp32= $seedTemp32".logE()

                    val gRandomKey32 = seedTemp32.toByte() xor 0x20211010.toByte()
                    "gRandomKey32= $gRandomKey32".logE()
                    //真正想要的值 2424879451

                }
            }
        }

    }
    override fun initListener() {
        mBinding.queryCancmdSettingView.setOnSettingItemListener {
            BleManager.instance.sendBleQueryCommand()
        }

        mBinding.configCancmdSettingView.setOnSettingItemListener {
            BleManager.instance.sendConfigCanCommand()
        }

        //发送boot指令funByte=0x01
        mBinding.burnrecordSettingView.setOnSettingItemListener {
            lifecycleScope .launch {
                for (i in 1..10){
                    BleManager.instance.sendCommand(i.toByte(),0x01)
                    delay(50)
                }
                delay(100)
                //获取软件信息指令funByte=0x02
                BleManager.instance.sendCommand(0x01,0x02)
                delay(200)
                //请求seed指令funByte=0x03
                BleManager.instance.sendCommand(0x01,0x03)
                //NewGroup/BinaryBleManage
            }



        }
    }


}