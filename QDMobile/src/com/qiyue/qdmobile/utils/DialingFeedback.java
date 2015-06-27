package com.qiyue.qdmobile.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;

import java.util.Timer;
import java.util.TimerTask;

public class DialingFeedback {

	/** The length of vibrate (haptic) feedback in milliseconds */
	private static final int HAPTIC_LENGTH_MS = 50;
	
    /** The length of DTMF tones in milliseconds */
    private static final int TONE_LENGTH_MS = 150;

    /** The DTMF tone volume relative to other sounds in the stream */
    private static final int TONE_RELATIVE_VOLUME = 80;

    private boolean inCall;
    private int toneStream;
    private Activity context;
    
	private ToneGenerator toneGenerator = null;
	private Object toneGeneratorLock = new Object();
	private Vibrator vibrator = null;
	private Timer toneTimer = null;

	private PreferencesWrapper prefsWrapper;
	private boolean dialPressTone = false;
	private boolean dialPressVibrate = false;

	private int ringerMode;

	public DialingFeedback(Activity context, boolean inCall) {
		
		this.context = context;
		this.inCall = inCall;
		toneStream = inCall ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC;
		prefsWrapper = new PreferencesWrapper(context);
	}
	
	public void resume() {
		
		dialPressTone = prefsWrapper.dialPressTone(inCall);
		dialPressVibrate = prefsWrapper.dialPressVibrate();

        if (dialPressTone) {
            // Create dialtone just for user feedback
            synchronized (toneGeneratorLock) {
                if (toneTimer == null) {
                    toneTimer = new Timer("Dialtone-timer");
                }
                if (toneGenerator == null) {
                    try {
                        toneGenerator = new ToneGenerator(toneStream, TONE_RELATIVE_VOLUME);
                        // Allow user to control dialtone
                        if (!inCall) {
                            context.setVolumeControlStream(toneStream);
                        }
                    } catch (RuntimeException e) {
                        // If impossible, nothing to do
                        toneGenerator = null;
                    }
                }
            }
        } else {
            toneTimer = null;
            toneGenerator = null;
        }
		
		//Create the vibrator
		if (dialPressVibrate) {
			if (vibrator == null) {
				vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			}
		} else {
			vibrator = null;
		}

		//Store the current ringer mode
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		ringerMode = am.getRingerMode();

	}
	
	public void pause() {

		//Destroy dialtone
		synchronized (toneGeneratorLock) {
			if (toneGenerator != null) {
			    toneGenerator.stopTone();
				toneGenerator.release();
				toneGenerator = null;
			}
			if(toneTimer != null) {
				toneTimer.cancel();
				toneTimer.purge();
				toneTimer = null;
			}
		}
		
	}

	/**
	 * Tone play to be used when a button in dialpad
	 * is pressed
	 * @param tone The tone associated to the button
	 */
	public void giveFeedback(int tone) {
		
		switch (ringerMode) {
			case AudioManager.RINGER_MODE_NORMAL:
				if (dialPressVibrate) {
				    vibrator.vibrate(HAPTIC_LENGTH_MS);
				}
				if (dialPressTone) {
				    ThreadedTonePlay threadedTone = new ThreadedTonePlay(tone);
				    threadedTone.start();
				}
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				if (dialPressVibrate) {
				    vibrator.vibrate(HAPTIC_LENGTH_MS);
				}
				break;
			case AudioManager.RINGER_MODE_SILENT:
				break;
		}
	}
	
	/**
	 * Haptic vibration to be used when a button in dialpad 
	 * is long pressed for example.
	 */
	public void hapticFeedback() {
		if (dialPressVibrate && ringerMode != AudioManager.RINGER_MODE_SILENT) {
			vibrator.vibrate(HAPTIC_LENGTH_MS);
		}
	}
	
	class ThreadedTonePlay extends Thread {
	    private final int tone;
        
	    ThreadedTonePlay(int t){
	        tone = t;
	    }
	    
	    @Override
	    public void run() {
            synchronized (toneGeneratorLock) {
                if (toneGenerator == null) {
                    return;
                }
                toneGenerator.stopTone();
                toneGenerator.startTone(tone);
                toneTimer.schedule(new StopTimerTask(), TONE_LENGTH_MS);
            }
	    }
	}
	
	class StopTimerTask extends TimerTask{
		@Override
		public void run() {
			synchronized (toneGeneratorLock) {
				if (toneGenerator == null) {
					return;
				}
				toneGenerator.stopTone();
			}
		}
	}

}
