package com.example.ble.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.example.ble.databinding.ItemDeviceBinding

@SuppressLint("SetTextI18n")
class BinderDevice : QuickViewBindingItemBinder<BleDevice, ItemDeviceBinding>() {
    override fun convert(holder: BinderVBHolder<ItemDeviceBinding>, data: BleDevice) {
        holder.viewBinding.tvName.text = data.bleName
        holder.viewBinding.tvAddress.text = data.bleAddress
    }

    override fun convert(holder: BinderVBHolder<ItemDeviceBinding>, data: BleDevice, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            convert(holder, data)
        } else {
            when (payloads[0] as String) {
                "address" -> holder.viewBinding.tvAddress.text = data.bleAddress
                "name" -> holder.viewBinding.tvName.text = data.bleName
                else -> convert(holder, data)
            }
        }
    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemDeviceBinding {
        return ItemDeviceBinding.inflate(layoutInflater, parent, false)
    }

    class Differ : DiffUtil.ItemCallback<BleDevice>() {
        override fun areItemsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
            return oldItem.bleAddress == newItem.bleAddress
        }

        override fun areContentsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
            return oldItem.bleName == newItem.bleName
        }

        override fun getChangePayload(oldItem: BleDevice, newItem: BleDevice): Any? {
            return when {
                oldItem.bleName != newItem.bleName -> "name"
                else -> null
            }
        }
    }
}