package com.example.ble.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.me.blelib.widget.dialog.LoadingProgressDialog
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB: ViewBinding>:AppCompatActivity() {
    lateinit var mBinding : VB
    var classSimpleName: String =javaClass.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(reflectToGetRootView())
        initView()
        initData()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingProgressDialog?.clear()
    }



    abstract fun initView()

    abstract fun initData()

    abstract fun initListener()

    private fun reflectToGetRootView(): View {
        val type = javaClass.genericSuperclass as ParameterizedType
        val clazz = type.actualTypeArguments[0] as Class<*>
        if (clazz != ViewBinding::class.java && ViewBinding::class.java.isAssignableFrom(clazz)) {
            try {
                val method = clazz.getDeclaredMethod("inflate", LayoutInflater::class.java)
                //ActivityMainBinding.inflate(layoutInflater)
                mBinding = method.invoke(null, layoutInflater) as VB
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return mBinding.root
    }

    private  var loadingProgressDialog: LoadingProgressDialog? = null

     fun getLoadingProgressDialog(): LoadingProgressDialog? {
        if (!isFinishing) {
            if (loadingProgressDialog == null) {
                loadingProgressDialog = LoadingProgressDialog(this)
            }
        }
        return loadingProgressDialog
    }

     fun hideLoadingDialog() {
        if (loadingProgressDialog != null && loadingProgressDialog!!.isShowing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                loadingProgressDialog?.dismiss()
            } else {
                loadingProgressDialog?.hide()
            }
        }
    }


}