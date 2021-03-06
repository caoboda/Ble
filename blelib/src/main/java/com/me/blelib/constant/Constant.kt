package com.me.blelib.constant

import java.util.*

internal object Constant {
    val DATA_SERVICE: UUID = UUID.fromString("0000FFB0-0000-1000-8000-00805F9B34FB")
    val DATA_TX: UUID = UUID.fromString("0000FFB1-0000-1000-8000-00805F9B34FB")
    val DATA_RX: UUID = UUID.fromString("0000FFB2-0000-1000-8000-00805F9B34FB")
    val CMD_SERVICE: UUID = UUID.fromString("0000FFB0-0000-1000-8000-00805F9B34FB")
    val AT_TX: UUID = UUID.fromString("0000FFFB1-0000-1000-8000-00805F9B34FB")
    val AT_RX: UUID = UUID.fromString("0000FFB2-0000-1000-8000-00805F9B34FB")
}