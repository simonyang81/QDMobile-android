package com.qiyue.qdmobile.ui.prefs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.ISipService;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.service.SipService;

public class AudioTester extends Activity implements OnClickListener {

    private final static String THIS_FILE = "AudioTester";

    int currentStatus = R.string.test_audio_prepare;
    private TextView statusTextView;

    private ProgressBar txProgress;
    private ProgressBar rxProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.audio_test_view);
        statusTextView = (TextView) findViewById(R.id.audio_test_text);
        rxProgress = (ProgressBar) findViewById(R.id.rx_bar);
        txProgress = (ProgressBar) findViewById(R.id.tx_bar);
        findViewById(R.id.cancel_bt).setOnClickListener(this);
    }

    private ISipService service;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = ISipService.Stub.asInterface(arg1);
            if (service != null) {
                try {
                    int res = service.startLoopbackTest();
                    if (res == SipManager.SUCCESS) {
                        currentStatus = R.string.test_audio_ongoing;
                    } else {
                        currentStatus = R.string.test_audio_network_failure;
                    }
                    updateStatusDisplay();
                } catch (RemoteException e) {
                    Log.e(THIS_FILE, "Error in test", e);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if (service != null) {
                try {
                    service.stopLoopbackTest();
                } catch (RemoteException e) {
                    Log.e(THIS_FILE, "Error in test", e);
                }
            }
            service = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        currentStatus = R.string.test_audio_prepare;
        updateStatusDisplay();
        bindService(new Intent(this, SipService.class), connection, Context.BIND_AUTO_CREATE);
        if (monitorThread == null) {
            monitorThread = new MonitorThread();
            monitorThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (service != null) {
            try {
                service.stopLoopbackTest();
            } catch (RemoteException e) {
                Log.e(THIS_FILE, "Error in test", e);
            }
        }

        if (connection != null) {
            unbindService(connection);
        }
        if (monitorThread != null) {
            monitorThread.markFinished();
            monitorThread = null;
        }
    }

    private void updateStatusDisplay() {
        if (statusTextView != null) {
            statusTextView.setText(currentStatus);
        }
    }

    private MonitorThread monitorThread;

    private class MonitorThread extends Thread {
        private boolean finished = false;

        public synchronized void markFinished() {
            finished = true;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                if (service != null) {
                    try {
                        long value = service.confGetRxTxLevel(0);
                        runOnUiThread(new UpdateConfLevelRunnable((int) ((value >> 8) & 0xff), (int) (value & 0xff)));
                    } catch (RemoteException e) {
                        Log.e(THIS_FILE, "Problem with remote service", e);
                        break;
                    }
                }

                // End of loop, sleep for a while and exit if necessary
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    Log.e(THIS_FILE, "Interupted monitor thread", e);
                }
                synchronized (this) {
                    if (finished) {
                        break;
                    }
                }
            }
        }
        
        private class UpdateConfLevelRunnable implements Runnable {
            private final int mRx;
            private final int mTx;
            UpdateConfLevelRunnable(int rx, int tx){
                mRx = rx;
                mTx = tx;
            }
            @Override
            public void run() {
                rxProgress.setProgress(mRx);
                txProgress.setProgress(mTx);
            }
            
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel_bt) {
            finish();
        }
    }

    
    
    
}
