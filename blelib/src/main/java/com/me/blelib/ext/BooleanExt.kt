package com.zydtech.library.ext

import java.text.DecimalFormat

//import kotlin.contracts.ExperimentalContracts
//import kotlin.contracts.InvocationKind
//import kotlin.contracts.contract

//@OptIn(ExperimentalContracts::class)
//fun Boolean?.yes(block: () -> Unit): Boolean? {
//    contract {
//        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
//    }
//    if (this == true) block()
//    return this
//}
//
//@OptIn(ExperimentalContracts::class)
//fun Boolean?.no(block: () -> Unit): Boolean? {
//    contract {
//        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
//    }
//    if (this != true) block()
//    return this
//}
//
//infix fun <T> Boolean.then(param: T): T? = if (this) param else null

sealed class BooleanExt<out T>

class TransferData<T>(val data: T) : BooleanExt<T>()
object Otherwise : BooleanExt<Nothing>()

inline fun <T> Boolean?.yes(block: () -> T): BooleanExt<T> =
    when {
        this == true -> TransferData(block.invoke())
        else -> Otherwise
    }

inline fun <T> BooleanExt<T>.otherwise(block: () -> T): T =
    when (this) {
        is Otherwise -> block()
        is TransferData -> data
    }

internal fun Boolean.int(): Int {
    return if (this) 1 else 0
}