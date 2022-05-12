@file:Suppress("unused")

package com.me.blelib.ext

import android.view.View
import java.text.DecimalFormat
import java.util.*

/*
    转换为字节数组
 */
internal fun Short.toByteArray(isReverse: Boolean = false): ByteArray {
    val result = ByteArray(2)
    if (isReverse) {
        result[0] = this.toByte()
        result[1] = (this.toInt() shr 8).toByte()
    } else {
        result[0] = (this.toInt() shr 8).toByte()
        result[1] = this.toByte()
    }
    return result
}

/*
    转换为16进制字符串
 */
internal fun Short.toHexString(isReverse: Boolean = false, hasSpace: Boolean = true, isUpper: Boolean = true): String {
    return this.toByteArray(isReverse).toHexString(hasSpace, isUpper)
}

/*
    转换为字节数组
 */
internal fun Int.toByteArray(isReverse: Boolean = false): ByteArray {
    val result: ByteArray
    if (this > Short.MAX_VALUE || this < Short.MIN_VALUE) {
        result = ByteArray(4)
        if (isReverse) {
            result[0] = this.toByte()
            result[1] = (this shr 8).toByte()
            result[2] = (this shr 16).toByte()
            result[3] = (this shr 24).toByte()
        } else {
            result[0] = (this shr 24).toByte()
            result[1] = (this shr 16).toByte()
            result[2] = (this shr 8).toByte()
            result[3] = this.toByte()
        }
    } else {
        result = this.toShort().toByteArray(isReverse)
    }
    return result
}

/*
    转换为16进制字符串
 */
internal fun Int.toHexString(isReverse: Boolean = false, hasSpace: Boolean = true, isUpper: Boolean = true): String {
    return this.toByteArray(isReverse).toHexString(hasSpace, isUpper)
}

/*
    转换为字节数组
 */
internal fun Long.toByteArray(isReverse: Boolean = false): ByteArray {
    val result: ByteArray
    if (this > Int.MAX_VALUE || this < Int.MIN_VALUE) {
        result = ByteArray(8)
        if (isReverse) {
            result[0] = this.toByte()
            result[1] = (this shr 8).toByte()
            result[2] = (this shr 16).toByte()
            result[3] = (this shr 24).toByte()
            result[4] = (this shr 32).toByte()
            result[5] = (this shr 40).toByte()
            result[6] = (this shr 48).toByte()
            result[7] = (this shr 56).toByte()
        } else {
            result[0] = (this shr 56).toByte()
            result[1] = (this shr 48).toByte()
            result[2] = (this shr 40).toByte()
            result[3] = (this shr 32).toByte()
            result[4] = (this shr 24).toByte()
            result[5] = (this shr 16).toByte()
            result[6] = (this shr 8).toByte()
            result[7] = this.toByte()
        }
    } else if (this > Short.MAX_VALUE || this < Short.MIN_VALUE) {
        result = this.toInt().toByteArray(isReverse)
    } else {
        result = this.toShort().toByteArray(isReverse)
    }
    return result
}

internal fun Int.dataLong(): String {
    // 这个方法是保证时间两位数据显示，如果为1点时，就为01
    return if (this >= 10)
        this.toString()
    else
        "0$this"
}

/*
    转换为16进制字符串
 */
internal fun Long.toHexString(isReverse: Boolean = false, hasSpace: Boolean = true, isUpper: Boolean = true): String {
    return this.toByteArray(isReverse).toHexString(hasSpace, isUpper)
}

/*
    获取位
 */
internal fun Int.getBit(bit: Int): Int {
    return (this and (1 shl bit)) shr bit
}

/*
    获取格式化字符串
 */
internal fun Int.precisionFormat(): String {
    return "%.${this}f"
}

/*
    测量大小
 */
internal fun Int.measureSize(defaultSize: Int): Int {
    var result = defaultSize
    val specMode = View.MeasureSpec.getMode(this)
    val specSize = View.MeasureSpec.getSize(this)

    if (specMode == View.MeasureSpec.EXACTLY) {
        result = specSize
    } else if (specMode == View.MeasureSpec.AT_MOST) {
        result = result.coerceAtMost(specSize)
    }
    return result
}

/*
    值是否在范围内
 */
internal fun Int.isInRange(a: Int, b: Int): Boolean {
    return if (b > a) this in a..b else this in b..a
}

/*
    摄氏度转华氏度
 */
internal fun Int.ctof(): String {
    val df = DecimalFormat("###.0")
    return "${df.format(this * 1.8 + 32)}°F"
}

/*
    值是否在范围内
 */
internal fun Float.isInRange(a: Float, b: Float): Boolean {
    return if (b > a) this in a..b else this in b..a
}

internal fun Double.addHalf(): Double {
    val start = String.format(Locale.ENGLISH, "%.1f", this).takeWhile { it != '.' && it != ',' }.toDouble()
    var end = String.format(Locale.ENGLISH, "%.1f", this).takeLast(1).toDouble()
    end = when {
        end <= 3 -> 0.toDouble()
        end >= 7 -> 1.toDouble()
        else -> 0.5
    }
    return start + end
}