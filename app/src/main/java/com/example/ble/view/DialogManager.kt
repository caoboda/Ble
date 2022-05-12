package com.example.ble.view

import android.content.Context
import android.text.InputType
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import cn.com.heaton.blelibrary.ble.model.BleDevice
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.files.FileCallback
import com.afollestad.materialdialogs.files.FileFilter
import com.afollestad.materialdialogs.files.fileChooser
import com.afollestad.materialdialogs.input.InputCallback
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.RegexUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.example.ble.R
import com.example.ble.adapter.BinderDevice
import com.example.ble.viewmodel.MyViewModel
import com.fphoenixcorneae.progressbar.SmartProgressBar
import com.me.blelib.bean.ConnectInfo
import com.me.blelib.manager.BleManager
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.io.File

class DialogManager {
    private var adapter: BaseBinderAdapter = BaseBinderAdapter()
    private val model: MyViewModel = MyViewModel.instance
    private var connectingDialog: MaterialDialog? = null
    private var deviceDialog: MaterialDialog? = null
    private var loadingDialog: MaterialDialog? = null
    private var mProgressDialog: MaterialDialog? = null
    private var msgDialog: MaterialDialog? = null
    private var pwdDialog: MaterialDialog? = null
    private var fileDialog: MaterialDialog? = null
    private var mProgress: SmartProgressBar? = null

    companion object {
        val instance : DialogManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DialogManager()
        }
    }

    init {
        adapter.addItemBinder(BinderDevice(), BinderDevice.Differ())
        adapter.setList(mutableListOf())
    }

    private fun MaterialDialog.after(go: () -> Unit): MaterialDialog {
        this.onDismiss {
            go()
        }
        return this
    }

    fun showDeviceList(ctx: Context) {
        adapter.data.clear()
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.data[position] as BleDevice
            var isNeedPwd = false
            var name = ""
            var address = ""
            if (model.isConnected.value == true) {
                BleManager.instance.disconnect()
            }
            if (model.lastConnect.address == item.bleAddress) {
                    model.lastConnect.name = item.bleName
                    model.lastConnect.address = item.bleAddress
                    showConnecting(ctx)
                    BleManager.instance.connect(ConnectInfo(model.lastConnect.name, model.lastConnect.address, model.lastConnect.pwd))
            } else {
                    model.lastConnect.name = item.bleName
                    model.lastConnect.address = item.bleAddress
                    showConnecting(ctx)
                    BleManager.instance.connect(ConnectInfo(item.bleName, item.bleAddress, ""))
            }
            deviceDialog?.after {
                deviceDialog = null
                LogUtils.d("Device Dialog dismiss")
                BleManager.instance.stopScan()

            }
            deviceDialog?.dismiss()
        }
        if (deviceDialog == null) {
            deviceDialog = showDialog(ctx, R.string.can_connect_device, custom = R.layout.dialog_device_list, nbId = R.string.cancel)
            val mRecyclerView = deviceDialog?.findViewById<RecyclerView>(R.id.recycler_view_devices)
            mRecyclerView?.let { recyclerView ->
                recyclerView.setHasFixedSize(true)
                if (recyclerView.itemDecorationCount == 0) {
                    val spacingInPixels = ctx.resources.getDimensionPixelSize(R.dimen.one)
                    recyclerView.addItemDecoration(SpaceItemDecoration(spacingInPixels, false))
                }
                recyclerView.adapter = adapter
                val srl = deviceDialog?.findViewById<SmartRefreshLayout>(R.id.srl)
                srl?.setOnRefreshListener {
                    notifyDeviceDialog(mutableListOf())
                    BleManager.instance.startScan()
                }
                srl?.autoRefresh()
            }
        } else {
            LogUtils.d("DeviceDialog已存在")
            deviceDialog?.show()
            deviceDialog?.findViewById<SmartRefreshLayout>(R.id.srl)?.autoRefresh()
        }
    }

    fun finishRefresh() {
        deviceDialog?.findViewById<SmartRefreshLayout>(R.id.srl)?.finishRefresh()
    }

    fun addDevice(result: BleDevice) {
        val list = mutableListOf<Any>().apply {
            addAll(adapter.data)
        }
        val old = list.find { if (it is BleDevice) it.bleAddress == result.bleAddress else false }
        if (old == null) {
            list.add(result)
            adapter.setDiffNewData(list.toMutableList())
        } else {
            val pos = list.indexOf(old)
            list[pos] = result
            adapter.setDiffNewData(list.toMutableList())
        }
    }

    fun notifyDeviceDialog(newData: MutableList<BleDevice>) {
        val list: MutableList<BleDevice> = mutableListOf()
        for (d in newData) {
            list.add(d)
        }
        adapter.setDiffNewData(list.toMutableList())
    }

    fun showConnecting(ctx: Context) {
        if (connectingDialog == null) {
            connectingDialog = showDialog(ctx, cancel = false, titleId = R.string.connecting_the_bluetooth, custom = R.layout.dialog_connecting)
        }
        if (connectingDialog?.isShowing != true) {
            try {
                connectingDialog?.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
                connectingDialog = null
                showConnecting(ctx)
            }
        }
    }

    fun showLoading(ctx: Context) {
        if (loadingDialog == null) {
            loadingDialog = showDialog(ctx, cancel = false, titleId = R.string.loading, custom = R.layout.dialog_loading)
        }
        if (loadingDialog?.isShowing != true) {
            try {
                loadingDialog?.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
                loadingDialog = null
                showLoading(ctx)
            }
        }
    }

    fun showProgress(ctx: Context) {
        if (mProgressDialog == null) {
            mProgressDialog = MaterialDialog(ctx)
                .title(R.string.firmware_update)
                .message(R.string.update_tips)
                .customView(R.layout.dialog_updating)
                .cornerRadius(10f)
                .cancelOnTouchOutside(false)
                .cancelable(false)
                .noAutoDismiss()
//            mProgressDialog = showDialog(ctx, cancel = false, titleId = R.string.firmware_upgrade, messageId = R.string.update_tips, custom = R.layout.dialog_updating, autoDismiss = false)
        }
        mProgress = mProgressDialog?.findViewById(R.id.spb_horizontal)
        if (mProgressDialog?.isShowing != true) {
            try {
                mProgressDialog?.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
                mProgressDialog = null
                showProgress(ctx)
            }
        }
    }

    fun setProgress(progress: Int) {
        mProgress?.setProgress(progress.toFloat())
    }

    fun showMsgDialog(
        ctx: Context,
        @StringRes titleId: Int = -1, title: String = "",
        @StringRes messageId: Int = -1, message: String = "",
        @StringRes pbId: Int = -1, pb: String = "", pbCallback: DialogCallback? = null,
        @StringRes nbId: Int = -1, nb: String = "", nbCallback: DialogCallback? = null,
        @LayoutRes custom: Int = -1,
        dismissCallback: DialogCallback? = null,
        cancel: Boolean = true,
        autoDismiss:Boolean = true,
        isBottom: Boolean = false): MaterialDialog {
        return showDialog(ctx, titleId, title, messageId, message, pbId, pb, pbCallback, nbId, nb, nbCallback, custom, dismissCallback, cancel, autoDismiss, isBottom)
    }


    fun showFile(ctx: Context, dir: File, mFilter: FileFilter, callback: FileCallback): MaterialDialog {
        fileDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            fileDialog = null
        }
        fileDialog = MaterialDialog(ctx).show {
            cornerRadius(10f)
            fileChooser(context = ctx, initialDirectory = dir, filter = mFilter,selection = callback)
        }
        return fileDialog as MaterialDialog
    }

    private fun showDialog(
        ctx: Context,
        @StringRes titleId: Int = -1, title: String = "",
        @StringRes messageId: Int = -1, message: String = "",
        @StringRes pbId: Int = -1, pb: String = "", pbCallback: DialogCallback? = null,
        @StringRes nbId: Int = -1, nb: String = "", nbCallback: DialogCallback? = null,
        @LayoutRes custom: Int = -1,
        dismissCallback: DialogCallback? = null,
        cancel: Boolean = true,
        autoDismiss:Boolean = true,
        isBottom: Boolean = false
    ): MaterialDialog {
        return if (isBottom) MaterialDialog(ctx, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            cornerRadius(10f)
            cancelable(cancel)
            cancelOnTouchOutside(cancel)
            if (titleId != -1) title(titleId) else if (title.isNotEmpty()) title(null, title)
            if (messageId != -1) message(messageId) else if (message.isNotEmpty()) message(null, message)
            if (pbId != -1) positiveButton(pbId, click = pbCallback) else if (pb.isNotEmpty()) positiveButton(null, pb, click = pbCallback)
            if (nbId != -1) negativeButton(nbId, click = nbCallback) else if (nb.isNotEmpty()) negativeButton(null, nb, click = nbCallback)
            if (custom != -1) customView(custom, null, scrollable = true, noVerticalPadding = true, horizontalPadding = false, dialogWrapContent = true)
            if (dismissCallback != null) onDismiss(dismissCallback)
            if (!autoDismiss) noAutoDismiss()
        } else MaterialDialog(ctx).show {
            cornerRadius(10f)
            cancelable(cancel)
            cancelOnTouchOutside(cancel)
            if (titleId != -1) title(titleId) else if (title.isNotEmpty()) title(null, title)
            if (messageId != -1) message(messageId) else if (message.isNotEmpty()) message(null, message)
            if (pbId != -1) positiveButton(pbId, click = pbCallback) else if (pb.isNotEmpty()) positiveButton(null, pb, click = pbCallback)
            if (nbId != -1) negativeButton(nbId, click = nbCallback) else if (nb.isNotEmpty()) negativeButton(null, nb, click = nbCallback)
            if (custom != -1) customView(custom, null, scrollable = true, noVerticalPadding = true, horizontalPadding = false, dialogWrapContent = true)
            if (dismissCallback != null) onDismiss(dismissCallback)
            if (!autoDismiss) noAutoDismiss()
        }
    }

    fun showChoiceDialog(
        ctx: Context,
        @StringRes titleId: Int = -1, title: String = "",
        @StringRes messageId: Int = -1, message: String = "",
        @StringRes pbId: Int = -1, pb: String = "", pbCallback: DialogCallback? = null,
        @StringRes nbId: Int = -1, nb: String = "", nbCallback: DialogCallback? = null,
        @LayoutRes custom: Int = -1,
        @ArrayRes itemsId: Int = -1, items: List<CharSequence> = listOf(), listener: SingleChoiceListener = null,
        wait: Boolean = true,
        now: Int = 0,
        dismissCallback: DialogCallback? = null,
        cancel: Boolean = true,
        autoDismiss:Boolean = true,
        isBottom: Boolean = true): MaterialDialog {
        return if (isBottom) MaterialDialog(ctx, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            cornerRadius(10f)
            cancelable(cancel)
            cancelOnTouchOutside(cancel)
            if (titleId != -1) title(titleId) else if (title.isNotEmpty()) title(null, title)
            if (messageId != -1) message(messageId) else if (message.isNotEmpty()) message(null, message)
            if (pbId != -1) positiveButton(pbId, click = pbCallback) else if (pb.isNotEmpty()) positiveButton(null, pb, click = pbCallback)
            if (nbId != -1) negativeButton(nbId, click = nbCallback) else if (nb.isNotEmpty()) negativeButton(null, nb, click = nbCallback)
            if (custom != -1) customView(custom, null, scrollable = true, noVerticalPadding = true, horizontalPadding = false, dialogWrapContent = true)
            if (itemsId != -1) {
                listItemsSingleChoice(itemsId, initialSelection = now, waitForPositiveButton = wait, selection = listener)
            } else if (items.isNotEmpty()) {
                listItemsSingleChoice(items = items, initialSelection = now, waitForPositiveButton = wait, selection = listener)
            }
            if (dismissCallback != null) onDismiss(dismissCallback)
            if (!autoDismiss) noAutoDismiss()
        } else MaterialDialog(ctx).show {
            cornerRadius(10f)
            cancelable(cancel)
            cancelOnTouchOutside(cancel)
            if (titleId != -1) title(titleId) else if (title.isNotEmpty()) title(null, title)
            if (messageId != -1) message(messageId) else if (message.isNotEmpty()) message(null, message)
            if (pbId != -1) positiveButton(pbId, click = pbCallback) else if (pb.isNotEmpty()) positiveButton(null, pb, click = pbCallback)
            if (nbId != -1) negativeButton(nbId, click = nbCallback) else if (nb.isNotEmpty()) negativeButton(null, nb, click = nbCallback)
            if (custom != -1) customView(custom, null, scrollable = true, noVerticalPadding = true, horizontalPadding = false, dialogWrapContent = true)
            if (itemsId != -1) {
                listItemsSingleChoice(itemsId, initialSelection = now, waitForPositiveButton = wait, selection = listener)
            } else if (items.isNotEmpty()) {
                listItemsSingleChoice(items = items, initialSelection = now, waitForPositiveButton = wait, selection = listener)
            }
            if (dismissCallback != null) onDismiss(dismissCallback)
            if (!autoDismiss) noAutoDismiss()
        }
    }

    fun showInput(
        ctx: Context,
        titleId: Int? = null, title: String = "",
        @StringRes pbId: Int? = null, pb: String = "", pbCallback: DialogCallback? = null,
        @StringRes nbId: Int? = null, nb: String = "", nbCallback: DialogCallback? = null,
        inputCallback: InputCallback = null,
        dismissCallback: DialogCallback? = null,
        @StringRes hintId: Int? = null, hint: String? = null,
        @StringRes prefId: Int? = null, pref: String? = null,
        type: Int = InputType.TYPE_CLASS_TEXT, max: Int? = null,
        autoDismiss: Boolean = true,
        cancel: Boolean = false,
        empty: Boolean = false,
        wait: Boolean = false
    ): MaterialDialog {
        return MaterialDialog(ctx).show {
            cornerRadius(10f)
            cancelable(cancel)
            cancelOnTouchOutside(cancel)
            if (titleId != null) title(titleId) else if (title.isNotEmpty()) title(null, title)
            input(inputType = type, prefillRes = prefId, prefill = pref, hintRes = hintId, hint = hint, maxLength = max, allowEmpty = empty, waitForPositiveButton = wait, callback = inputCallback)
            if (pbId != null) positiveButton(pbId, click = pbCallback) else if (pb.isNotEmpty()) positiveButton(null, pb, click = pbCallback)
            if (nbId != null) negativeButton(nbId, click = nbCallback) else if (nb.isNotEmpty()) negativeButton(null, nb, click = nbCallback)
            if (dismissCallback != null) onDismiss(dismissCallback)
            if (!autoDismiss) noAutoDismiss()
        }
    }

    fun closeConnecting() {
        connectingDialog?.dismiss()
        connectingDialog = null
    }

    fun closeLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun closeProgress() {
        mProgressDialog?.dismiss()
        mProgressDialog = null
    }

    fun closeMsg() {
        msgDialog?.dismiss()
        msgDialog = null
    }

    fun closeFile() {
        fileDialog?.dismiss()
        fileDialog = null
    }
}