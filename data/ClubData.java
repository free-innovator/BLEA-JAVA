package com.practice.myapplication.data;

/**
 * Created by hagtfms on 2016-04-29.
 */
public class ClubData {
    public static final String TABLE_NAME = "tbl_club";
    public static final String COLUMN_NAME_NAME = "Cname";
    public static final String COLUMN_NAME_METER = "Cmeter";

    private String mName;
    private int mMeter;

    public ClubData(String name, int meter){
        mName = name;
        mMeter = meter;
    }

    public String getName(){ return mName; }
    public int getMeter(){ return mMeter; }
}
