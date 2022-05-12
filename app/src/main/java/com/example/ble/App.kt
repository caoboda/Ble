package com.example.ble

import android.app.Application
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.util.Log
import android.webkit.WebView
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.utils.UuidUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.me.blelib.manager.BleManager
import java.io.File
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        BleManager.instance.init(this)
        BleManager.instance.setLogEnabled(true)

        initBle()
        WebView(this).destroy()
        val crashDir = File(getFilePath("Crash"))
        CrashUtils.init(crashDir) { crashInfo ->
            crashInfo?.let {
                Log.e(App::class.java.simpleName, it.throwable?.message ?: "Error")
            }
        }
    }

    private fun getFilePath(dir: String, fileName: String = ""): String {
        val directoryPath: String = if (MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            getExternalFilesDir(dir)?.path ?: ""
        } else {
            filesDir.toString() + File.separator + dir
        }
        val file = File(directoryPath)
        if (FileUtils.createOrExistsDir(file)) {
            Log.d(App::class.java.simpleName, "Dir Create: ${file.absoluteFile}")
        }
        return if (fileName.isEmpty()) {
            "$directoryPath/"
        } else {
            "$directoryPath/$fileName"
        }
    }

    private fun initBle() {
        Ble.options() //开启配置
            .setLogBleEnable(false) //设置是否输出打印蓝牙日志（非正式打包请设置为true，以便于调试）
            .setThrowBleException(false) //设置是否抛出蓝牙异常 （默认true）
            .setAutoConnect(false) //设置是否自动连接 （默认false）
            .setConnectFailedRetryCount(5)
            .setIgnoreRepeat(false) //设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
            .setConnectTimeout(3 * 1000.toLong()) //设置连接超时时长（默认10*1000 ms）
            .setMaxConnectNum(7) //最大连接数量
            .setScanPeriod(10 * 1000.toLong()) //设置扫描时长（默认10*1000 ms）
            .setUuidService(UUID.fromString(UuidUtils.uuid16To128("FFB0"))) //设置主服务的uuid（必填）
            .setUuidWriteCha(UUID.fromString(UuidUtils.uuid16To128("FFB1"))) //设置可写特征的uuid （必填,否则写入失败）
            .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128("FFB2"))) //设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
            .setUuidServicesExtra(arrayOf(UUID.fromString(UuidUtils.uuid16To128("F2F0"))))
            .create<BleDevice>(this, object : Ble.InitCallback {
                override fun success() {
                    LogUtils.e("BLE初始化成功")
                }

                override fun failed(failedCode: Int) {
                    LogUtils.e("BLE初始化失败：$failedCode")
                }
            })
    }
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            App()
        }
    }
}