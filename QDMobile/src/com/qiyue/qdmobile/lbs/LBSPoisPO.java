package com.qiyue.qdmobile.lbs;

import java.util.Arrays;

/**
 * Created by Simon on 7/4/15.
 */
public class LBSPoisPO {

    public String title;
    public double[] location = new double[2];
    public String city;
    public String create_time;
    public int geotable_id;
    public String address;
    public String tags;
    public String province;
    public String district;
    public String SIP_ACCOUNT;
    public long TIME_PERIOD;
    public int city_id;
    public long id;

    @Override
    public String toString() {
        return "LBSPoisPO{" +
                "title='" + title + '\'' +
                ", location=" + Arrays.toString(location) +
                ", city='" + city + '\'' +
                ", create_time='" + create_time + '\'' +
                ", geotable_id=" + geotable_id +
                ", address='" + address + '\'' +
                ", tags='" + tags + '\'' +
                ", province='" + province + '\'' +
                ", district='" + district + '\'' +
                ", SIP_ACCOUNT='" + SIP_ACCOUNT + '\'' +
                ", TIME_PERIOD=" + TIME_PERIOD +
                ", city_id=" + city_id +
                ", id=" + id +
                '}';
    }
}
