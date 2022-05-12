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
                delay(50)
                //获取软件信息指令funByte=0x02
                BleManager.instance.sendCommand(0x01,0x02)
                delay(50)
                //请求seed指令funByte=0x03
                BleManager.instance.sendCommand(0x01,0x03)
                //NewGroup/BinaryBleManage
            }



        }
    }


}