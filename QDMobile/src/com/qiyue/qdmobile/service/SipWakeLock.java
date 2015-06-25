package com.qiyue.qdmobile.service;

import android.os.PowerManager;

import com.github.snowdream.android.util.Log;

import java.util.HashSet;

public class SipWakeLock {

    private static final String THIS_FILE = "SipWakeLock";

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private PowerManager.WakeLock mTimerWakeLock;
    private HashSet<Object> mHolders = new HashSet<Object>();

    public SipWakeLock(PowerManager powerManager) {
        mPowerManager = powerManager;
    }

    /**
     * Release this lock and reset all holders
     */
    public synchronized void reset() {
        mHolders.clear();
        release(null);
        if (mWakeLock != null) {
            while (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            Log.v(THIS_FILE, "~~~ hard reset wakelock :: still held : " + mWakeLock.isHeld());
        }
    }

    public synchronized void acquire(long timeout) {
        if (mTimerWakeLock == null) {
            mTimerWakeLock = mPowerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, "SipWakeLock.timer");
            mTimerWakeLock.setReferenceCounted(true);
        }
        mTimerWakeLock.acquire(timeout);
    }

    public synchronized void acquire(Object holder) {
        mHolders.add(holder);
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, "SipWakeLock");
        }
        if (!mWakeLock.isHeld()) mWakeLock.acquire();
        Log.v(THIS_FILE, "acquire wakelock: holder count=" + mHolders.size());
    }

    public synchronized void release(Object holder) {
        mHolders.remove(holder);
        if ((mWakeLock != null) && mHolders.isEmpty()
                && mWakeLock.isHeld()) {
            mWakeLock.release();
        }

        Log.v(THIS_FILE, "release wakelock: holder count=" + mHolders.size());
    }
}