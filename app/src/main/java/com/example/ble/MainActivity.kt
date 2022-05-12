package com.example.ble

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.asLiveData
import cn.com.heaton.blelibrary.ble.Ble.REQUEST_ENABLE_BT
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.blankj.utilcode.util.*
import com.example.ble.activity.BurnRecordFileInfoActivity
import com.example.ble.base.BaseActivity
import com.example.ble.bean.CmdData
import com.example.ble.databinding.ActivityMainBinding
import com.example.ble.util.DataUtils
import com.example.ble.util.JsonUtil
import com.example.ble.view.DialogManager
import com.example.ble.viewmodel.MyViewModel
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.me.blelib.bean.ConnectInfo
import com.me.blelib.enum.ConnectStatus
import com.me.blelib.enum.ScanStatus
import com.me.blelib.ext.showToast
import com.me.blelib.manager.BleDataListener
import com.me.blelib.manager.BleManager
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val checkPosition =0
    private val model: MyViewModel = MyViewModel.instance
    private var isConnected: Boolean = false
    private var isShowPolicy: Boolean = false

    override fun initView() {
        mBinding.commonTitle.setRightVisibility(View.VISIBLE)
        mBinding.commonTitle.imgRight.setImageResource(R.mipmap.main_add_icon)
        mBinding.commonTitle.setRightListener{
            if (isGpsEnabled() && isLocationEnabled()) {
                checkPermissions()
            } else {
                DialogManager.instance.showMsgDialog(this, R.string.permission_denied, messageId = R.string.location_not_enabled, pbId = R.string.yes, pbCallback = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    ActivityUtils.startActivity(intent)
                }, nbId = R.string.no)
            }
           /* XPopup.Builder(this)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asCenterList("Device",
                    arrayOf("device1", "device1" ),
                    null,
                    checkPosition
                ) { position, text ->
                    showToast("click $text")
                }
                .show()*/
        }

    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("设备不支持蓝牙4.0")
            finish()
        }
        BleManager.instance.turnOnBlueTooth(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
           when(requestCode){
               REQUEST_ENABLE_BT  ->{
                   if(resultCode!=RESULT_OK){
                       finish()
                   }
               }
           }
     }



    override fun initListener() {

        BleManager.instance.setListener(bleDataListener)
        model.isConnected.observe(this) {
            isConnected = it ?: false
        }
        DataUtils.readDataSerializable(stringPreferencesKey("LastConnect")).asLiveData().observe(this) {
            model.lastConnect = it?.let {
                it as ConnectInfo
            } ?: ConnectInfo()
        }
        mBinding.shaolvBtn.setOnClickListener {
                startActivity(Intent(this, BurnRecordFileInfoActivity::class.java))
        }
    }


    private val bleDataListener = object : BleDataListener() {
        // 蓝牙扫描状态，Scanning 正在扫描，Stopped 已停止扫描
        override fun onScanStatusChanged(status: ScanStatus) {
            if (status == ScanStatus.Stopped) {
                DialogManager.instance.finishRefresh()
            }
        }

        //蓝牙连接状态,
        //    CONNECTING, 正在连接
        //    CONNECTED, 已连接
        //    DISCONNECTING, 正在断开连接
        //    DISCONNECTED, 已断开连接
        //    PWD_FAILED, 密码错误
        //    PWD_TIMEOUT, 密码校验超时
        //    CONNECT_FAIL_TYPE_NOT_DEVICE 连接失败，没有设备
        override fun onConnectStatusChanged(status: ConnectStatus) {
            LogUtils.v("Connect status changed: $status")
            when (status) {
                ConnectStatus.CONNECTING -> {
                    DialogManager.instance.showConnecting(this@MainActivity)
                }
                ConnectStatus.CONNECTED -> {
                    DialogManager.instance.closeConnecting()
                    GlobalScope.launch {
                        DataUtils.saveDataSerializable(stringPreferencesKey("LastConnect"), model.lastConnect)
                    }
                    model.isConnected.postValue(true)
                }
                ConnectStatus.DISCONNECTED -> {
                    DialogManager.instance.closeConnecting()
                    DialogManager.instance.closeLoading()
                    model.isConnected.postValue(false)
                }
                else -> {}
            }
        }

        // 扫描到蓝牙设备，刷新蓝牙设备列表
        override fun onDeviceFound(result: BleDevice) {
            DialogManager.instance.addDevice(result)
        }

        // 扫描到蓝牙设备，刷新蓝牙设备列表
        override fun onDeviceFound(result: MutableList<BleDevice>) {
            DialogManager.instance.notifyDeviceDialog(result)
        }


    /*    // 部分指令错误信息反馈
        override fun onCommandErrorChanged(result: String) {
            LogUtils.d("Result: $result")
        }*/
    }

    /**
     * 判断Gps是否可用
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    private fun isGpsEnabled(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * 判断定位是否可用
     *
     */
    @Suppress("DEPRECATION")
    private fun isLocationEnabled(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                lm.isLocationEnabled
            }
            else -> {
                var locationMode = 0
                try {
                    locationMode = Settings.Secure.getInt(Utils.getApp().contentResolver, Settings.Secure.LOCATION_MODE)
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                }
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(
                    LocationManager.GPS_PROVIDER) || locationMode != Settings.Secure.LOCATION_MODE_OFF
            }
        }
    }

    private fun checkPermissions() {
        PermissionX.init(this)
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, getString(R.string.location_not_enabled), getString(R.string.ok))
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, getString(R.string.location_not_enabled), getString(R.string.ok))
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    DialogManager.instance.showDeviceList(this)
                } else {
                    ToastUtils.showLong("您拒绝了如下权限：$deniedList")
                }
            }
    }



}