package com.qiyue.qdmobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.qiyue.qdmobile.BasActivity;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.widgets.EditSipUri;
import com.qiyue.qdmobile.widgets.EditSipUri.ToCall;

public class PickupSipUri extends BasActivity implements OnClickListener {

    private EditSipUri sipUri;
    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pickup_uri);
        initBarTintManager();

        okBtn = (Button) findViewById(R.id.ok);
        okBtn.setOnClickListener(this);
        Button btn = (Button) findViewById(R.id.cancel);
        btn.setOnClickListener(this);

        sipUri = (EditSipUri) findViewById(R.id.sip_uri);
        sipUri.getTextField().setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int action, KeyEvent arg2) {
                if (action == EditorInfo.IME_ACTION_GO) {
                    sendPositiveResult();
                    return true;
                }
                return false;
            }
        });
//        sipUri.setShowExternals(false);

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.ok) {
            sendPositiveResult();
        } else if (vId == R.id.cancel) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void sendPositiveResult() {
        Intent resultValue = new Intent();
        ToCall result = sipUri.getValue();
        if (result != null) {
            // Restore existing extras.
            Intent it = getIntent();
            if (it != null) {
                Bundle b = it.getExtras();
                if (b != null) {
                    resultValue.putExtras(b);
                }
            }
            resultValue.putExtra(Intent.EXTRA_PHONE_NUMBER,
                    result.getCallee());
            resultValue.putExtra(SipProfile.FIELD_ID,
                    result.getAccountId());
            setResult(RESULT_OK, resultValue);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

}
