@file:Suppress("unused")

package com.me.blelib.ext

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.core.content.ContextCompat

/** dp和px转换 **/
fun Context.dp2px(dpValue: Float): Int {
    return (dpValue * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.px2dp(pxValue: Float): Int {
    return (pxValue / resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.sp2px(spValue: Float): Int {
    return (spValue * resources.displayMetrics.scaledDensity + 0.5f).toInt()
}

fun Context.px2sp(pxValue: Float): Int {
    return (pxValue / resources.displayMetrics.scaledDensity + 0.5f).toInt()
}

fun Context.dipToPx(size: Float): Int {
    return (size * resources.displayMetrics.density + 0.5f * if (size >= 0) 1 else -1).toInt()
}

fun Context.dipToPx(size: Int): Int {
    return (size * resources.displayMetrics.density + 0.5f * if (size >= 0) 1 else -1).toInt()
}

fun Context.color(color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun Context.stringArray(@ArrayRes res: Int): Array<String> = resources.getStringArray(res)