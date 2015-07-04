package org.pjsip.pjsua;

public class pjsua_call_setting {
    private long swigCPtr;
    protected boolean swigCMemOwn;

    protected pjsua_call_setting(long cPtr, boolean cMemoryOwn) {
        swigCMemOwn = cMemoryOwn;
        swigCPtr = cPtr;
    }

    protected static long getCPtr(pjsua_call_setting obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                pjsuaJNI.delete_pjsua_call_setting(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    public void setFlag(long value) {
        pjsuaJNI.pjsua_call_setting_flag_set(swigCPtr, this, value);
    }

    public long getFlag() {
        return pjsuaJNI.pjsua_call_setting_flag_get(swigCPtr, this);
    }

    public void setReq_keyframe_method(long value) {
        pjsuaJNI.pjsua_call_setting_req_keyframe_method_set(swigCPtr, this, value);
    }

    public long getReq_keyframe_method() {
        return pjsuaJNI.pjsua_call_setting_req_keyframe_method_get(swigCPtr, this);
    }

    public void setAud_cnt(long value) {
        pjsuaJNI.pjsua_call_setting_aud_cnt_set(swigCPtr, this, value);
    }

    public long getAud_cnt() {
        return pjsuaJNI.pjsua_call_setting_aud_cnt_get(swigCPtr, this);
    }

    public void setVid_cnt(long value) {
        pjsuaJNI.pjsua_call_setting_vid_cnt_set(swigCPtr, this, value);
    }

    public long getVid_cnt() {
        return pjsuaJNI.pjsua_call_setting_vid_cnt_get(swigCPtr, this);
    }

    public pjsua_call_setting() {
        this(pjsuaJNI.new_pjsua_call_setting(), true);
    }

}
