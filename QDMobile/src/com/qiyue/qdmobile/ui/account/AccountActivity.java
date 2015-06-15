package com.qiyue.qdmobile.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.Constants;
import com.qiyue.qdmobile.wizards.BasePrefsWizard;
import com.qiyue.qdmobile.zxing.CaptureActivity;

public class AccountActivity extends Activity {

    private View mScanView, mManuallyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mScanView = findViewById(R.id.add_account_scan);
        mManuallyView = findViewById(R.id.add_account_manually);

        setListener();

    }

    private void setListener() {

        if (mScanView != null) {
            mScanView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(AccountActivity.this, CaptureActivity.class);
                    startActivity(i);

                    finishSelf();

                }
            });
        }

        if (mManuallyView != null) {
            mManuallyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setClass(AccountActivity.this, BasePrefsWizard.class);
                    intent.putExtra(SipProfile.FIELD_WIZARD, "BASIC");
                    startActivity(intent);

                    finishSelf();

                }
            });
        }

    }

    private void finishSelf() {
        if (isFinishing() == false) {
            finish();
        }
    }

}
