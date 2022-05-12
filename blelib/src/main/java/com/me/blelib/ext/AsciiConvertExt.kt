package com.me.blelib.ext

/**
 * 字节码->ASCII码
 * @param bt
 */
fun Int.byteToASCII():String {
     var chars = Character.toChars(this);
     println("对应的ASCII码为:${String(chars)}" )
     return String(chars)
}

/**
 * ASCII码->字节码
 *
 */
fun Char.ascToByte():Int {
    return this as Int
}