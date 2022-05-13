package com.example.ble.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {


  fun getCurrentDate(){
     var calendar =  Calendar.getInstance()
      "年: " +calendar.get(Calendar.YEAR)
      "月: " + (calendar.get(Calendar.MONTH) + 1)
      "日: " +calendar.get(Calendar.DAY_OF_MONTH)
      "时: " +calendar.get(Calendar.HOUR_OF_DAY)
      "分: " +calendar.get(Calendar.MINUTE)
      "秒: " +calendar.get(Calendar.SECOND)
      "timeInMillis: " +calendar.timeInMillis
       //calendar.time
      val date =Date()
      val sdf =SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val dateNowStr=sdf.format(date)
      println("格式化后的日期：$dateNowStr")
      val str = "2022-3-13 17:26:33" //要跟上面sdf定义的格式一样
      val today = sdf.parse(str)
      println("字符串转成日期：$today")
  }









}