package com.qiyue.qdmobile.utils.bluetooth;

import android.content.Context;

import com.qiyue.qdmobile.utils.Compatibility;

public abstract class BluetoothWrapper {
	
    public interface BluetoothChangeListener {
        void onBluetoothStateChanged(int status);
    }
    
	private static BluetoothWrapper instance;
    protected Context context;
    
    protected BluetoothChangeListener btChangesListener;
	
	public static BluetoothWrapper getInstance(Context context) {
		if (instance == null) {
			if (Compatibility.isCompatible(14)) {
				instance = new com.qiyue.qdmobile.utils.bluetooth.BluetoothUtils14();
			} else if (Compatibility.isCompatible(8)) {
				instance = new com.qiyue.qdmobile.utils.bluetooth.BluetoothUtils8();
			} else {
				instance = new com.qiyue.qdmobile.utils.bluetooth.BluetoothUtils3();
			}
			if (instance != null) {
				instance.setContext(context);
			}
		}
		
		return instance;
	}
	
	protected BluetoothWrapper() {}

	protected void setContext(Context ctxt) {
	    context = ctxt;
	}
	
	public void setBluetoothChangeListener(BluetoothChangeListener l) {
	    btChangesListener = l;
	}
	
	public abstract boolean canBluetooth();
	public abstract void setBluetoothOn(boolean on);
	public abstract boolean isBluetoothOn();
	public abstract void register();
	public abstract void unregister();
	public abstract boolean isBTHeadsetConnected();
}
