package com.example.ble.manager

import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView

object XpopupManager {

    fun showLoadingPopupView(context: Context, content: String = "加载中"): LoadingPopupView {
        return XPopup.Builder(context)
            .dismissOnBackPressed(false)
            .isLightNavigationBar(true)
            .isViewMode(false)
            //.asLoading(null, R.layout.custom_loading_popup)
            .asLoading(content)
            .show() as LoadingPopupView
    }

}