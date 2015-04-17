package com.qiyue.qdmobile.ui.warnings;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.ui.warnings.WarningUtils.WarningBlockView;

public class WarningNoStun extends WarningBlockView {

    public WarningNoStun(Context context) {
        this(context, null);
    }
    public WarningNoStun(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public WarningNoStun(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void bindView() {
        super.bindView();
        findViewById(R.id.warn_stun_on).setOnClickListener(this);
    }
    
    @Override
    protected int getLayout() {
        return R.layout.warning_no_stun;
    }
    
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if(id == R.id.warn_stun_on) {
            SipConfigManager.setPreferenceBooleanValue(getContext(), SipConfigManager.ENABLE_STUN, true);
            if(onWarnChangedListener != null) {
                onWarnChangedListener.onWarningRemoved(getWarningKey());
            }
            getContext().sendBroadcast(new Intent(SipManager.ACTION_SIP_REQUEST_RESTART));
        }
    }
    @Override
    protected String getWarningKey() {
        return WarningUtils.WARNING_NO_STUN;
    }
    

}
