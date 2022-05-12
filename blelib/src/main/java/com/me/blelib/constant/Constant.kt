package com.me.blelib.constant

import java.util.*

internal object Constant {
    const val HEAD_ESC = 0x01.toByte()                     // 1.1 电调包头
//    const val HEAD_ESC = 0x02.toByte()                     // 1.1 电调包头  ATO SHELLRIDE
    const val HEAD_TRAN = 0xA5.toByte()                    // 1.2 透传数据包头
    const val END_TRAN = 0x5A.toByte()                     // 1.3 透传数据包尾
    const val HEAD_MONITOR = 0xAB.toByte()                 // 1.4 实时数据包头
//    const val HEAD_MONITOR = 0xAE.toByte()                 // 1.4 实时数据包头  ATO SHELLRIDE

    const val CMD_READ_PARAMETER = 0x03.toByte()           // 2.1 电调参数读取 功能码
    const val CMD_ESC_INFO = 0x07.toByte()                 // 2.2 电调信息 功能码
    const val CMD_BAT_INFO = 0x08.toByte()                 // 2.3 电池信息 功能码
    const val CMD_WRITER_PARAMETER = 0x10.toByte()         // 2.4 电调参数写 功能码
    const val CMD_RW_PARAMETER = 0x17.toByte()             // 2.5 电调参数读写 功能码
    const val CMD_UPDATE_FM = 0x50.toByte()                // 2.6 升级数据包 功能码
    const val CMD_HANDSHAKE = 0x51.toByte()                // 2.7 升级握手 功能码
    const val CMD_ERASE_FLASH = 0x52.toByte()              // 2.8 擦除FLASH 功能码
    const val CMD_BOOT_EXIT = 0x53.toByte()                // 2.9 退出BOOT 功能码

    const val CMD_FAIL = 0x81.toByte()                     // 3.1 未知 错误 功能码
    const val CMD_READ_PARAM_FAIL = 0x83.toByte()          // 3.2 电调参数读取 错误 功能码
    const val CMD_READ_INFO_FAIL = 0x87.toByte()           // 3.3 电调信息读取 错误 功能码
    const val CMD_READ_BAT_FAIL = 0x88.toByte()            // 3.4 电池信息读取 错误 功能码
    const val CMD_WRITE_PARAM_FAIL = 0x90.toByte()         // 3.5 电调参数写入 错误 功能码
    const val CMD_RW_PARAM_FAIL = 0x97.toByte()            // 3.6 电调参数读写 错误 功能码
    const val CMD_HANDSHAKE_FAIL = 0xD0.toByte()           // 3.7 升级握手 错误 功能码
    const val CMD_UPDATE_FAIL = 0xD1.toByte()              // 3.8 升级固件 错误 功能码
    const val CMD_ERASE_FAIL = 0xD2.toByte()               // 3.9 擦除失败 错误 功能码

    const val CMD_TRAN = 0x00.toByte()                     // 4.1 开始透传 功能码
    const val CMD_PACK = 0x01.toByte()                     // 4.2 开始拼包 功能码
    const val CMD_KEEP = 0x02.toByte()                     // 4.3 开始保持实时数据 功能码
    const val CMD_TRAN_STOP = 0xFF.toByte()                // 4.4 结束透传 功能码
    const val CMD_PACK_STOP = 0xFE.toByte()                // 4.5 结束拼包 功能码
    const val MAX_PACKET_SIZE = 65535
    const val PACKET_SIZE = 8
    const val INFO_PACKET_SIZE = 16
    const val FULL_PACKET_SIZE = 80
    const val SUB_PACKET_MIN = 20
    const val SUB_PACKET_MAX = 130

    val DATA_SERVICE: UUID = UUID.fromString("0000FFB0-0000-1000-8000-00805F9B34FB")
    val DATA_TX: UUID = UUID.fromString("0000FFB1-0000-1000-8000-00805F9B34FB")
    val DATA_RX: UUID = UUID.fromString("0000FFB2-0000-1000-8000-00805F9B34FB")
    val CMD_SERVICE: UUID = UUID.fromString("0000FFB0-0000-1000-8000-00805F9B34FB")
    val AT_TX: UUID = UUID.fromString("0000FFFB1-0000-1000-8000-00805F9B34FB")
    val AT_RX: UUID = UUID.fromString("0000FFB2-0000-1000-8000-00805F9B34FB")
}