package com.example.ble.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SafeMutableLiveData<T> : MutableLiveData<T>() {
    private val owners: MutableMap<String, LifecycleOwner> = mutableMapOf()

    fun safeObserve(owner: LifecycleOwner, observer: Observer<T>) {
        if (!owners.containsKey(owner.hashCode().toString())) {
            owners[owner.hashCode().toString()] = owner
            observe(owner, observer)
        }
    }

}