package com.me.blelib.ext

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

/**
 * 弹出toast
 */
fun LifecycleOwner.showToast( message:String, duration:Int=Toast.LENGTH_SHORT) {
    if (this is Activity) {
        Toast.makeText(this,message,duration).show()
    } else if (this is Fragment) {
        Toast.makeText(requireContext(),message,duration).show()
    }
}

