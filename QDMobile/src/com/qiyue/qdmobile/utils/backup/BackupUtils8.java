package com.qiyue.qdmobile.utils.backup;

import android.annotation.TargetApi;
import android.app.backup.BackupManager;

@TargetApi(8)
public class BackupUtils8 extends BackupWrapper {

    /* (non-Javadoc)
     * @see com.qiyue.qdmobile.utils.backup.BackupWrapper#onDataChanged()
     */
    @Override
    public void dataChanged() {
        BackupManager bmgr = new BackupManager(context);
        bmgr.dataChanged();
    }

}
