package com.example.ble.activity

import android.content.Intent
import android.util.Log
import com.example.ble.R
import com.example.ble.base.BaseActivity
import com.example.ble.bean.CmdData
import com.example.ble.databinding.ActivityBurnRecordBinding
import com.example.ble.util.JsonUtil
import com.google.gson.Gson

class BurnRecordFileInfoActivity : BaseActivity<ActivityBurnRecordBinding>() {


    override fun initView() {
        mBinding.commonTitle.setTitle("烧录文件信息")
        mBinding.commonTitle.setLeftIcon(R.mipmap.left_black_icon)
      //  mBinding.hardwareSettingView.setTitle("硬件版本")
    }

    override fun initData() {
        val cmdData:CmdData=JsonUtil.getJsonData(this)
        mBinding.hardwareSettingView.value=cmdData.PROG_INFO?.HW_VERSION
        mBinding.softworeSettingView.value=cmdData.PROG_INFO?.SW_VERSION
        mBinding.bitrateSettingView.value=cmdData.PROG_INFO?.CAN_BAUDERATE
        mBinding.blockNumSettingView.value=""+cmdData.PROG_INFO?.DATA_BLOCK_NUM

    }

    override fun initListener() {
        mBinding.burnrecordSettingView.setOnSettingItemListener {
            startActivity(Intent(this, ConfigCanCmdActivity::class.java))
        }
    }


}