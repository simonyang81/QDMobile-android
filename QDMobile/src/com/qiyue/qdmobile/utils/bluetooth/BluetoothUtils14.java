package com.qiyue.qdmobile.utils.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;

@TargetApi(14)
public class BluetoothUtils14 extends BluetoothUtils8 {

    @Override
    public boolean isBTHeadsetConnected() {
        if (bluetoothAdapter != null) {
            return (bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothAdapter.STATE_CONNECTED);
        }
        return false;
    }
}
