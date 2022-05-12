@file:Suppress("unused")

package com.me.blelib.ext

import java.util.*
import kotlin.experimental.and

internal fun Byte.toHexString(isUpper: Boolean = true): String {
    val stringBuilder = StringBuilder("")
    val v = this.toInt() and 0xFF
    val hv = Integer.toHexString(v)
    if (hv.length < 2) {
        stringBuilder.append(0)
    }
    stringBuilder.append(hv)
    return if (isUpper) {
        stringBuilder.toString().toUpperCase(Locale.getDefault())
    } else {
        stringBuilder.toString().toLowerCase(Locale.getDefault())
    }
}

internal fun Byte.toBitString(): String {
    return "${(this.toInt() shr 7) and 0x01}${(this.toInt() shr 6) and 0x01}${(this.toInt() shr 5) and 0x01}${(this.toInt() shr 4) and 0x01}${(this.toInt() shr 3) and 0x01}${(this.toInt() shr 2) and 0x01}${(this.toInt() shr 1) and 0x01}${(this.toInt() shr 0) and 0x01}"
}

internal fun Byte.getBit(bit:Int): Int {
    return (this.toInt() and (1 shl bit)) shr bit
}

/**
 *将byte转换为一个长度为8的byte数组，数组每个值代表bit
 */
internal fun Byte.getBoolArray(): ByteArray {
    var b = this
    val array = byteArrayOf(8)
    for (i in 7 downTo 0) {
        array[i] = b and 1
        b = (b.toInt() shr 1).toByte()
    }
    return array
}