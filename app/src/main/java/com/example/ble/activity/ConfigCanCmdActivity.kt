package com.example.ble.activity

import androidx.lifecycle.lifecycleScope
import com.example.ble.R
import com.example.ble.base.BaseActivity
import com.example.ble.bean.CmdData
import com.example.ble.databinding.ActivityConfigCancmdBinding
import com.example.ble.manager.XpopupManager
import com.example.ble.util.JsonUtil
import com.example.ble.util.RamdomUtil
import com.lxj.xpopup.impl.LoadingPopupView
import com.me.blelib.ext.logE
import com.me.blelib.ext.toHexString
import com.me.blelib.manager.BleDataListener
import com.me.blelib.manager.BleManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfigCanCmdActivity : BaseActivity<ActivityConfigCancmdBinding>() {
    private lateinit var loadingPopupView: LoadingPopupView
    private lateinit var cmdData: CmdData
    private var blockCmdCount=0//块指令条数
    private var currentCmdBlock=1//当前发送指令所在块
    private var currentCmdBlockAddrFrame:String? = null//当前发送指令所在块地址
    private var delayTime: Long= 60


    override fun initView() {
        mBinding.commonTitle.setTitle("配置CAN命令")
        mBinding.commonTitle.setLeftIcon(R.mipmap.left_black_icon)
      //  mBinding.hardwareSettingView.setTitle("硬件版本")
    }

    override fun initData() {
        cmdData = JsonUtil.getJsonData(this)
        currentCmdBlockAddrFrame= cmdData.DATA_BLOCK_7?.ADDR_FRAME
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
                    var currentBlockContent="==================================发送第1块数据块指令================================="
                    currentBlockContent.logE()
                    loadingPopupView.setTitle(currentBlockContent)
                    //数据长度与地址指令
                    currentCmdBlock=1
                    BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(), address=cmdData?.DATA_BLOCK_1?.ADDR_FRAME!!)
                }
            }else if(resultBytes[10] == 0x06.toByte() && resultBytes[11] ==0x00.toByte()){//数据长度与指令返回
                if(resultBytes.size==20) {
                    //数据块指令
                    lifecycleScope.launch {
                        when (currentCmdBlock){
                            1 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_1?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_1?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_1?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            2 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_2?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_2?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_2?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            3 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_3?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_3?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_3?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            4 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_4?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_4?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_4?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            5 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_5?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_5?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_5?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            6 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_6?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_6?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_6?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            7 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_7?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_7?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_7?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            8 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_8?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_8?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_8?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            9 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_9?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_9?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_9?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                            10 ->{
                                currentCmdBlockAddrFrame=cmdData.DATA_BLOCK_10?.ADDR_FRAME
                                for (i in 0 until cmdData.DATA_BLOCK_10?.DATA_FRAME_ARRAW_LEN!!){
                                    blockCmdCount++
                                    var sumNum=getsumNum(blockCmdCount)
                                    BleManager.instance.sendDataBlockCommand(sumNumByte = sumNum.toByte(),data_frame = cmdData?.DATA_BLOCK_10?.DATA_FRAME_ARRAY!![i])
                                    delay(delayTime)
                                }
                            }
                        }
                    }
                }
            }else if(resultBytes[4] == 0x07.toByte() && resultBytes[5] ==0x00.toByte()){//校验key指令返回
                //编程日期指令funByte=0x05
                if(resultBytes.size==14) {
                    lifecycleScope.launch {
                        when (currentCmdBlockAddrFrame){
                            cmdData.DATA_BLOCK_1?.ADDR_FRAME ->{
                                resetCurrentBlockParam(2)
                                //数据长度与地址指令
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address=cmdData?.DATA_BLOCK_2?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_2?.ADDR_FRAME  ->{
                                resetCurrentBlockParam(3)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address=cmdData?.DATA_BLOCK_3?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_3?.ADDR_FRAME  ->{
                                resetCurrentBlockParam(4)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address=cmdData?.DATA_BLOCK_4?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_4?.ADDR_FRAME->{
                                resetCurrentBlockParam(5)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address=cmdData?.DATA_BLOCK_5?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_5?.ADDR_FRAME ->{
                                resetCurrentBlockParam(6)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address=cmdData?.DATA_BLOCK_6?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_6?.ADDR_FRAME->{
                                resetCurrentBlockParam(7)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address=cmdData?.DATA_BLOCK_7?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_7?.ADDR_FRAME->{
                                resetCurrentBlockParam(8)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address = cmdData?.DATA_BLOCK_8?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_8?.ADDR_FRAME->{
                                resetCurrentBlockParam(9)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(), address = cmdData?.DATA_BLOCK_9?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_9?.ADDR_FRAME->{
                                resetCurrentBlockParam(10)
                                BleManager.instance.sendLengthAndAddressCommand(sumNumByte = currentCmdBlock.toByte(),address=cmdData?.DATA_BLOCK_10?.ADDR_FRAME!!)
                            }
                            cmdData.DATA_BLOCK_10?.ADDR_FRAME ->{
                                //完成编程指令
                                resetCurrentBlockParam(0)
                                BleManager.instance.sendCompleProgramCommand(crc_frame =cmdData?.CHECK_BLOCK?.CRC_FRAME!!)
                            }
                        }
                    }

                }
            }else if(resultBytes[4] == 0x08.toByte() && resultBytes[5] ==0x00.toByte()){//完成编程指令返回
                loadingPopupView.setTitle("完成编程指令")
                loadingPopupView.dismiss()
             }
        }

    }

    private suspend fun resetCurrentBlockParam(currentBlock: Int) {
        currentCmdBlock=currentBlock
        var currentBlockContent:String=if (currentCmdBlock==0){
            "=================================10块数据块指令发送完成  前一块blockCmdCount= ${blockCmdCount}================================="
        }else{
            "=================================发送第${currentCmdBlock}块数据块指令 前一块blockCmdCount= ${blockCmdCount}================================="
        }
        currentBlockContent.logE()
        loadingPopupView.setTitle(currentBlockContent)
        blockCmdCount=0
        delay(delayTime)
    }

    //获取帧数
    private fun getsumNum(blockCmdCount:Int):Int {
        var sumNum = if (blockCmdCount>255){
            blockCmdCount % 255
        }else{
            blockCmdCount
        }
        return sumNum
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
            loadingPopupView = XpopupManager.showLoadingPopupView(this)
            lifecycleScope .launch {
                for (i in 1..10){
                    BleManager.instance.sendCommand(i.toByte(),0x01)
                    delay(50)
                }
                delay(100)
                //获取软件信息指令funByte=0x02
                BleManager.instance.sendCommand(funByte = 0x02)
                delay(200)
                //请求seed指令funByte=0x03
                BleManager.instance.sendCommand(funByte =0x03)
            }
        }
    }
}