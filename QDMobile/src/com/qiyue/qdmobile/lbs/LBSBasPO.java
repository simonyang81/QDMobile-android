package com.qiyue.qdmobile.lbs;

/**
 * Created by Simon on 6/23/15.
 */
public class LBSBasPO {

    public int status;
    public String message;
    public String id;

    @Override
    public String toString() {
        return "LBSBasPO{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
