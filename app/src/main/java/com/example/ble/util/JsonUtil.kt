package com.example.ble.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

object JsonUtil {

    // 读取json文件的方法，也可写成工具类方便使用
    fun getJson(context: Context, fileName: String?): String {
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
}