package com.example.ble.bean

data class CmdData(
    val PROG_INFO: PROGINFO? = PROGINFO(),
    val DATA_BLOCK_1: DATABLOCK1? = DATABLOCK1(),
    val DATA_BLOCK_2: DATABLOCK2? = DATABLOCK2(),
    val DATA_BLOCK_3: DATABLOCK3? = DATABLOCK3(),
    val DATA_BLOCK_4: DATABLOCK4? = DATABLOCK4(),
    val DATA_BLOCK_5: DATABLOCK5? = DATABLOCK5(),
    val DATA_BLOCK_6: DATABLOCK6? = DATABLOCK6(),
    val DATA_BLOCK_7: DATABLOCK7? = DATABLOCK7(),
    val DATA_BLOCK_8: DATABLOCK8? = DATABLOCK8(),
    val DATA_BLOCK_9: DATABLOCK9? = DATABLOCK9(),
    val DATA_BLOCK_10: DATABLOCK10? = DATABLOCK10(),
    val CHECK_BLOCK: CHECKBLOCK? = CHECKBLOCK()
)

data class PROGINFO(
    val HW_VERSION: String? = "",
    val SW_VERSION: String? = "",
    val CAN_BAUDERATE: String? = "",
    val DATA_BLOCK_NUM: Int? = 0
)

data class DATABLOCK1(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK2(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK3(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK4(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK5(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK6(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK7(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK8(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK9(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class DATABLOCK10(
    val DATA_FRAME_ARRAW_LEN: Int? = 0,
    val ADDR_FRAME: String? = "",
    val DATA_FRAME_ARRAY: List<String>? = listOf()
)

data class CHECKBLOCK(
    val CRC_FRAME: String? = ""
)