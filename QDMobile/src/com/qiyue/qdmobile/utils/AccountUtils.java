package com.qiyue.qdmobile.utils;

import android.database.Cursor;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.QDMobileApplication;
import com.qiyue.qdmobile.api.SipProfile;

/**
 * Created by Simon on 6/25/15.
 */
public class AccountUtils {

    private static final String TAG = AccountUtils.class.getSimpleName();

    public static SipProfile getAccount() {

        SipProfile acc = null;

        Cursor c = null;
        try {
            c = QDMobileApplication.getContextQD().getContentResolver().query(
                    SipProfile.ACCOUNT_URI, Constants.ACC_PROJECTION,
                    SipProfile.FIELD_ACTIVE + "=?",
                    new String[]{"1"},
                        null);
            if (c != null && c.moveToFirst()) {
                acc = new SipProfile(c);
            }

        } catch (Exception e) {
            Log.e(TAG, "getAccount(), " + e.toString());
        } finally {
            try {
                if (c != null && c.isClosed() == false) {
                    c.close();
                }
            } catch (Exception e) {
            }

            return acc;
        }

    }

    public static String getLBSAccountID() {

        SipProfile acc = getAccount();

        if (acc == null || acc.id == SipProfile.INVALID_ID) {
            return "";
        }

        return acc.getSipUserName() + "@" + acc.getDefaultDomain();
    }

}
