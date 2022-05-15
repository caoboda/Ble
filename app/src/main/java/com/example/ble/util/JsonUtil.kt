package com.example.ble.util

import android.content.Context
import android.util.Log
import com.example.ble.bean.CmdData
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

object JsonUtil {

    // 读取json文件的方法，也可写成工具类方便使用
    private fun getJson(context: Context, fileName: String?): String {
        // 将json数据变成字符串
        val stringBuilder = StringBuilder()
        // 获得assets资源管理器
        val assetManager = context.assets
        // 使用IO流读取json文件内容
        try {
            val bufferedReader = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName!!), "utf-8"
                )
            )
            var line: String?=null
            while (bufferedReader.readLine()?.also { line = it } != null) {
                stringBuilder.append(line?.trim { it <= ' ' })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }


    fun getJsonData(context: Context):CmdData{
        val  jsonStr= getJson(context,"V1.0.3.220410_BinaryInstruments.json")
        val cmdData: CmdData = Gson().fromJson(jsonStr, CmdData::class.java)
        Log.e("cmdData= "," $cmdData ")
        return cmdData
    }
}