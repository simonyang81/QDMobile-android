package com.qiyue.qdmobile.backup;

import android.content.Context;
import android.content.SharedPreferences;

import com.qiyue.qdmobile.QDMobileApplication;
import com.qiyue.qdmobile.utils.Constants;

/**
 * Created by Simon on 7/4/15.
 */
public class SharedPreferenceHelper {

    public static long getCreatePOITime() {

        Context context = QDMobileApplication.getContextQD();
        SharedPreferences sharedPreferences
                = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(Constants.SHARED_PREFERENCES_CREATE_POI_TIME, 0);

    }

    public static void setCreatePOITime(long time) {

        Context context = QDMobileApplication.getContextQD();
        SharedPreferences sharedPreferences
                = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(Constants.SHARED_PREFERENCES_CREATE_POI_TIME, time);
        editor.commit();

    }

}
