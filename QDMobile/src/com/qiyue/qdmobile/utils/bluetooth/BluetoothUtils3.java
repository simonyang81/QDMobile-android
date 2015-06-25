package com.qiyue.qdmobile.utils.bluetooth;

import android.content.Context;

import com.qiyue.qdmobile.service.MediaManager;

public class BluetoothUtils3 extends BluetoothWrapper {


	public boolean canBluetooth() {
		return false;
	}

	public void setBluetoothOn(boolean on) {
		// Do nothing
	}

	public boolean isBluetoothOn() {
		return false;
	}

	@Override
	public void setContext(Context context) {
		// Do nothing
	}

    public void setMediaManager(MediaManager aManager) {
     // Do nothing
    }

	@Override
	public void register() {
		//Do nothing
	}

	@Override
	public void unregister() {
		// Do nothing
	}

    @Override
	public boolean isBTHeadsetConnected() {
	    return false;
	}
}
