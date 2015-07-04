package com.qiyue.qdmobile.lbs;

import java.util.ArrayList;

/**
 * Created by Simon on 7/4/15.
 */
public class LBSListPO {

    public int status;
    public int size;
    public long total;
    public ArrayList<LBSPoisPO> pois = new ArrayList();
    public String message;

    @Override
    public String toString() {
        return "LBSListPO{" +
                "status=" + status +
                ", size=" + size +
                ", total=" + total +
                ", pois=" + pois +
                ", message='" + message + '\'' +
                '}';
    }
}
