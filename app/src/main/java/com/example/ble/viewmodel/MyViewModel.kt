package com.example.ble.viewmodel

import com.example.ble.base.SafeMutableLiveData
import com.me.blelib.bean.ConnectInfo

class MyViewModel {
    var isConnected: SafeMutableLiveData<Boolean> = SafeMutableLiveData()
   // var escInfo: SafeMutableLiveData<EscInfo> = SafeMutableLiveData()
    //var monitor: SafeMutableLiveData<Monitor> = SafeMutableLiveData()
   // var baseParams: SafeMutableLiveData<BaseParams> = SafeMutableLiveData()
    //var advParams: SafeMutableLiveData<AdvParams> = SafeMutableLiveData()
    var firmwares: SafeMutableLiveData<MutableList<String>> = SafeMutableLiveData()
    var comStatus: ComStatus = ComStatus.Monitor
    var lastConnect: ConnectInfo = ConnectInfo()

    enum class ComStatus {
        Monitor,
        Params,
        Firmware,
    }

    init {
        isConnected.postValue(false)
     //   escInfo.postValue(EscInfo())
       // monitor.postValue(Monitor())
       // baseParams.postValue(BaseParams())
       // advParams.postValue(AdvParams())
      //  firmwares.postValue(mutableListOf())
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MyViewModel()
        }
    }
}