package com.qiyue.qdmobile.backup;

import android.annotation.TargetApi;
import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.os.ParcelFileDescriptor;

import com.github.snowdream.android.util.Log;

import java.io.IOException;

@TargetApi(8)
public class SipBackupAgent extends BackupAgentHelper {

    private static final String THIS_FILE = "SipBackupAgent";
    private static final String KEY_SHARED_PREFS = "shared_prefs";
    private static final String KEY_DATABASES = "databases";

    /*
     * (non-Javadoc)
     * @see android.app.backup.BackupAgent#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(THIS_FILE, "Create backup agent");
        SipSharedPreferencesHelper sharedPrefsHelper = new SipSharedPreferencesHelper(this);
        addHelper(KEY_SHARED_PREFS, sharedPrefsHelper);
        
        SipProfilesHelper profilesHelper = new SipProfilesHelper(this);
        addHelper(KEY_DATABASES, profilesHelper);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.app.backup.BackupAgent#onRestore(android.app.backup.BackupDataInput
     * , int, android.os.ParcelFileDescriptor)
     */
    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState)
            throws IOException {
        Log.d(THIS_FILE, "App version code : " + appVersionCode);
        super.onRestore(data, appVersionCode, newState);


    }
}
