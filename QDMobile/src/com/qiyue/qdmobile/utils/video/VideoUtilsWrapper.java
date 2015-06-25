package com.qiyue.qdmobile.utils.video;

import android.content.Context;
import android.text.TextUtils;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.utils.Compatibility;

import java.util.ArrayList;
import java.util.List;

public abstract class VideoUtilsWrapper {
    private static VideoUtilsWrapper instance;

    private static final String THIS_FILE = "VideoUtilsWrapper";

    public class VideoCaptureDeviceInfo {
        public List<VideoCaptureCapability> capabilities;
        public VideoCaptureCapability bestCapability;

        public VideoCaptureDeviceInfo() {
            capabilities = new ArrayList<VideoCaptureCapability>();
        }
    }

    public static class VideoCaptureCapability {
        public int width;
        public int height;
        public int fps;

        public VideoCaptureCapability() {

        }

        public VideoCaptureCapability(String preferenceValue) {
            if (!TextUtils.isEmpty(preferenceValue)) {
                String[] size_fps = preferenceValue.split("@");
                if (size_fps.length == 2) {
                    String[] width_height = size_fps[0].split("x");
                    if (width_height.length == 2) {
                        try {
                            width = Integer.parseInt(width_height[0]);
                            height = Integer.parseInt(width_height[1]);
                            fps = Integer.parseInt(size_fps[1]);
                        } catch (NumberFormatException e) {
                            Log.e(THIS_FILE, "Cannot parse the preference for video capture cap");
                        }
                    }
                }
            }
        }

        public String toPreferenceValue() {
            return (width + "x" + height + "@" + fps);
        }

        public String toPreferenceDisplay() {
            return (width + " x " + height + " @" + fps + "fps");
        }
    }

    public static VideoUtilsWrapper getInstance() {
        if (instance == null) {
            if (Compatibility.isCompatible(5)) {
                instance = new com.qiyue.qdmobile.utils.video.VideoUtils5();
            } else {
                instance = new com.qiyue.qdmobile.utils.video.VideoUtils3();
            }
        }

        return instance;
    }

    protected VideoUtilsWrapper() {
        // By default nothing to do in constructor
    }

    public abstract List<VideoCaptureDeviceInfo> getVideoCaptureDevices(Context ctxt);

}
