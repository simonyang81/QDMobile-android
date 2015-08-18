package com.qiyue.qdmobile

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.view.Window
import android.view.WindowManager

import com.qiyue.qdmobile.utils.Constants
import com.readystatesoftware.systembartint.SystemBarTintManager

/**
 * Created by Simon on 6/27/15.
 */
public open class BasFragmentActivity : FragmentActivity() {

    protected fun initBarTintManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true)
        }
        val tintManager = SystemBarTintManager(this)
        tintManager.setStatusBarTintEnabled(true)
        tintManager.setTintColor(Color.parseColor(Constants.HOME_SCREEN_STATUS_BAR_COLOR))
    }

    TargetApi(19)
    protected fun setTranslucentStatus(on: Boolean) {
        val win = getWindow()
        val winParams = win.getAttributes()
        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.setAttributes(winParams)
    }


}
