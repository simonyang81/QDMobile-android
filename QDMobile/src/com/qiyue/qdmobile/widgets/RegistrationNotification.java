package com.qiyue.qdmobile.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfileState;
import com.qiyue.qdmobile.wizards.WizardUtils;
import com.qiyue.qdmobile.wizards.WizardUtils.WizardInfo;

import java.util.ArrayList;

public class RegistrationNotification extends RemoteViews {

    private static final Integer[] cells = new Integer[] {
            R.id.cell1,
            R.id.cell2,
            R.id.cell3,
    };

    private static final Integer[] icons = new Integer[] {
            R.id.icon1,
            R.id.icon2,
            R.id.icon3,
    };

    private static final Integer[] texts = new Integer[] {
            R.id.account_label1,
            R.id.account_label2,
            R.id.account_label3,
    };

    public RegistrationNotification(String aPackageName) {
        super(aPackageName, R.layout.notification_registration_layout);
    }

//    public RegistrationNotification(Context ctxt) {
//        this(ctxt.getPackageName());
//    }
//
//    public RegistrationNotification(Context ctxt, AttributeSet attr) {
//        this(ctxt.getPackageName());
//    }
//
//    public RegistrationNotification(Context ctxt, AttributeSet attr, int defStyle) {
//        this(ctxt.getPackageName());
//    }

    /**
     * Reset all registration info for this view, ie hide all accounts cells
     */
    public void clearRegistrations() {
        for (Integer cellId : cells) {
            setViewVisibility(cellId, View.GONE);
        }
    }

    /**
     * Apply account information to remote view
     * 
     * @param context application context for resources retrieval
     * @param activeAccountsInfos List of sip profile state to show in this
     *            notification view
     */
    public void addAccountInfos(Context context, ArrayList<SipProfileState> activeAccountsInfos) {
        int i = 0;
        for (SipProfileState accountInfo : activeAccountsInfos) {
            // Clamp to max possible notifications in remote view
            if (i < cells.length) {
                setViewVisibility(cells[i], View.VISIBLE);
                WizardInfo wizardInfos = WizardUtils.getWizardClass(accountInfo.getWizard());
                if (wizardInfos != null) {
                    CharSequence dName = accountInfo.getDisplayName();

                    StringBuilder notificationText = new StringBuilder();
                    notificationText.append(context.getString(R.string.service_ticker_registered_text))
                                    .append(" - ")
                                    .append(dName);

                    setImageViewResource(icons[i], R.drawable.ic_qdmobile_registered);

                    if (notificationText != null && !TextUtils.isEmpty(notificationText.toString())) {
                        setTextViewText(texts[i], notificationText.toString());
                    }
                }
                i++;
            }
        }

    }

    public void setTextsColor(Integer color) {
        if (color == null) {
            return;
        }
        for (int i = 0; i < texts.length; i++) {
            setTextColor(texts[i], color);
        }
    }
}
