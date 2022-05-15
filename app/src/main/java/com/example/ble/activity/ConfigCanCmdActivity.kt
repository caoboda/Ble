package com.example.ble.activity

import androidx.lifecycle.lifecycleScope
import com.example.ble.R
import com.example.ble.base.BaseActivity
import com.example.ble.bean.CmdData
import com.example.ble.databinding.ActivityConfigCancmdBinding
import com.example.ble.util.JsonUtil
import com.example.ble.util.RamdomUtil
import com.me.blelib.ext.logE
import com.me.blelib.ext.toHexString
import com.me.blelib.manager.BleDataListener
import com.me.blelib.manager.BleManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfigCanCmdActivity : BaseActivity<ActivityConfigCancmdBinding>() {
    private lateinit var cmdData: CmdData
    private var blockCmdCount=0//块指令条数
    private var currentCmdBlock=1//当前发送指令所在块

    override fun initView() {
        mBinding.commonTitle.setTitle("配置CAN命令")
        mBinding.commonTitle.setLeftIcon(R.mipmap.left_black_icon)
      //  mBinding.hardwareSettingView.setTitle("硬件版本")
    }

    override fun initData() {
        cmdData = JsonUtil.getJsonData(this)
        BleManager.instance.setListener(bleDataListener)
    }


    private val bleDataListener = object : BleDataListener() {

        override fun onResponseData(resultBytes: ByteArray) {
            super.onResponseData(resultBytes)
            "设备返回数据: ${resultBytes.toHexString()}".logE()

            if(resultBytes[10] == 0x03.toByte() && resultBytes[11] ==0x00.toByte()){ //seed返回
                if(resultBytes.size==20) {
                    val randomBytes = byteArrayOf(resultBytes[15],resultBytes[14],resultBytes[13],resultBytes[12])
                    val  randomKey32 = RamdomUtil.ramDomNum("",randomBytes)
                    //校验key指令funByte=0x04
                    BleManager.instance.sendValidKeyCommand(funByte =0x04, randomKey = randomKey32)
                }
            }else if(resultBytes[4] == 0x04.toByte() && resultBytes[5] ==0x00.toByte()){//校验key指令返回
                //编程日期指令funByte=0x05
                if(resultBytes.size==14) {
                    BleManager.instance.sendDateCommand(funByte =0x05)
                }
            }else if(resultBytes[10] == 0x05.toByte() && resultBytes[11] ==0x00.toByte()){//编程日期指令返回
                if(resultBytes.size==20) {
                    "--------共${cmdData.PROG_INFO?.DATA_BLOCK_NUM}个块----------".logE()
                    "发送第1块数据块指令".logE()
                    //数据长度与地址指令
                    currentCmdBlock=1
                    BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_1?.ADDR_FRAME!!)
                }
            }else if(resultBytes[10] == 0x06.toByte() && resultBytes[11] ==0x00.toByte()){//数据长度与指令返回
                if(resultBytes.size==20) {
                    //数据块指令
                    lifecycleScope.launch {
                        when (currentCmdBlock){
                            1 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_1?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_1?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            2 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_2?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_2?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            3 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_3?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_3?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            4 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_4?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_4?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            5 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_5?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_5?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            6 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_6?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_6?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            7 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_7?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_7?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            8 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_8?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_8?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            9 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_9?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_9?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                            10 ->{
                                for (i in 0 until cmdData.DATA_BLOCK_10?.DATA_FRAME_ARRAW_LEN!!){
                                    BleManager.instance.sendDataBlockCommand(funByte =0x07, data_frame = cmdData?.DATA_BLOCK_10?.DATA_FRAME_ARRAY!![i])
                                    blockCmdCount++
                                    delay(50)
                                }
                            }
                        }
                    }


                }
            }else if(resultBytes[4] == 0x07.toByte() && resultBytes[5] ==0x00.toByte()){//校验key指令返回
                //编程日期指令funByte=0x05
                if(resultBytes.size==14) {
                    when (blockCmdCount){
                        cmdData.DATA_BLOCK_1?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第2块数据块指令".logE()
                            currentCmdBlock=2
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_2?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_2?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第3块数据块指令".logE()
                            currentCmdBlock=3
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_3?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_3?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第4块数据块指令".logE()
                            currentCmdBlock=4
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_4?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_4?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第5块数据块指令".logE()
                            currentCmdBlock=5
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_5?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_5?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第6块数据块指令".logE()
                            currentCmdBlock=6
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_6?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_6?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第7块数据块指令".logE()
                            currentCmdBlock=7
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_7?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_7?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第8块数据块指令".logE()
                            currentCmdBlock=8
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_8?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_8?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第9块数据块指令".logE()
                            currentCmdBlock=9
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_9?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_9?.DATA_FRAME_ARRAW_LEN ->{
                            "发送第10块数据块指令".logE()
                            currentCmdBlock=10
                            blockCmdCount=0
                            //数据长度与地址指令
                            BleManager.instance.sendLengthAndAddressCommand(funByte =0x06,address=cmdData?.DATA_BLOCK_10?.ADDR_FRAME!!)
                        }
                        cmdData.DATA_BLOCK_10?.DATA_FRAME_ARRAW_LEN ->{
                            "---------------10块数据块指令发送完成----------------".logE()
                            "发送完成编程指令".logE()
                            //完成编程指令
                        }
                    }
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
                BleManager.instance.sendCommand(funByte = 0x02)
                delay(300)
                //请求seed指令funByte=0x03
                BleManager.instance.sendCommand(funByte =0x03)
                //NewGroup/BinaryBleManage
            }

        }
    }


}