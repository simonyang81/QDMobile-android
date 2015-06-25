package com.qiyue.qdmobile.utils.video;

import android.content.Context;

import org.webrtc.videoengine.CaptureCapabilityAndroid;
import org.webrtc.videoengine.VideoCaptureDeviceInfoAndroid;

import java.util.ArrayList;
import java.util.List;

public class VideoUtils5 extends VideoUtilsWrapper {

    @Override
    public List<VideoCaptureDeviceInfo> getVideoCaptureDevices(Context ctxt) {
        VideoCaptureDeviceInfoAndroid deviceInfoAndroid = VideoCaptureDeviceInfoAndroid.CreateVideoCaptureDeviceInfoAndroid(0, ctxt);
        List<VideoCaptureDeviceInfo> arr = new ArrayList<VideoCaptureDeviceInfo>();
        if (deviceInfoAndroid == null) {
            return arr;
        }
        for (int i = 0; i < deviceInfoAndroid.NumberOfDevices(); i++) {
            String deviceName = deviceInfoAndroid.GetDeviceUniqueName(i);
            CaptureCapabilityAndroid[] caps = deviceInfoAndroid.GetCapabilityArray(deviceName);
            VideoCaptureDeviceInfo vcdi = new VideoCaptureDeviceInfo();
            int orientation = deviceInfoAndroid.GetOrientation(deviceName);
            boolean invertWidthHeight = false;
            if (orientation == 90 || orientation == 270) {
                invertWidthHeight = true;
            }

            for (CaptureCapabilityAndroid cap : caps) {
                VideoCaptureCapability vcc = new VideoCaptureCapability();
                vcc.height = invertWidthHeight ? cap.width : cap.height;
                vcc.width = invertWidthHeight ? cap.height : cap.width;
                vcc.fps = cap.maxFPS;
                vcdi.capabilities.add(vcc);
            }

            CaptureCapabilityAndroid bcap = deviceInfoAndroid.GetBestCapability(deviceName);
            if (bcap != null) {
                vcdi.bestCapability = new VideoCaptureCapability();
                vcdi.bestCapability.width = invertWidthHeight ? bcap.width : bcap.width;
                vcdi.bestCapability.height = invertWidthHeight ? bcap.height : bcap.height;
                vcdi.bestCapability.fps = bcap.maxFPS;
            }

            arr.add(vcdi);
        }

        return arr;
    }

}
