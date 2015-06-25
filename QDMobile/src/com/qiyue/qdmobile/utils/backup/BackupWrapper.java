package com.qiyue.qdmobile.utils.backup;

import android.content.Context;

import com.qiyue.qdmobile.utils.Compatibility;

public abstract class BackupWrapper {
	private static BackupWrapper instance;
    protected Context context;
	
	public static BackupWrapper getInstance(Context context) {
		if (instance == null) {
			if (Compatibility.isCompatible(8)) {
				instance = new com.qiyue.qdmobile.utils.backup.BackupUtils8();
			} else {
				instance = new com.qiyue.qdmobile.utils.backup.BackupUtils3();
			}
			if (instance != null) {
				instance.setContext(context);
			}
		}
		
		return instance;
	}
	
	protected BackupWrapper() {}

	protected void setContext(Context ctxt) {
	    context = ctxt;
	}
	/**
	 * Notifies the Android backup system that your application wishes to back up new changes to its data. 
	 * A backup operation using your application's BackupAgent subclass will be scheduled when you call this method. 
	 */
	public abstract void dataChanged();
}
