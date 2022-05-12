package com.me.blelib.enum

enum class ConnectStatus {
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    DISCONNECTED,
    PWD_FAILED,
    PWD_TIMEOUT,
    /**
     * 空设备
     */
    CONNECT_FAIL_TYPE_NOT_DEVICE
}