package com.qiyue.qdmobile.ui.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.qiyue.qdmobile.BasActivity
import com.qiyue.qdmobile.R
import com.qiyue.qdmobile.api.SipProfile
import com.qiyue.qdmobile.utils.Constants
import com.qiyue.qdmobile.wizards.BasePrefsWizard
import com.qiyue.qdmobile.zxing.CaptureActivity

import kotlinx.android.synthetic.activity_account.*

public class AccountActivity : BasActivity() {

//    private var mScanView: View? = null
//    private var mManuallyView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        super.initBarTintManager()

//        mScanView = findViewById(R.id.add_account_scan)
//        mManuallyView = findViewById(R.id.add_account_manually)

        setListener()

    }

    private fun setListener() {

        if (add_account_scan != null) {
            add_account_scan.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {

                    startActivity(Intent(this@AccountActivity, javaClass<CaptureActivity>()))

                    finishSelf()

                }
            })
        }

        if (add_account_manually != null) {
            add_account_manually.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {

                    val intent = Intent()
                    intent.setClass(this@AccountActivity, javaClass<BasePrefsWizard>())
                    intent.putExtra(SipProfile.FIELD_WIZARD, "BASIC")
                    startActivity(intent)

                    finishSelf()

                }
            })
        }

    }

    private fun finishSelf() {
        if (isFinishing() == false) {
            finish()
        }
    }

}
