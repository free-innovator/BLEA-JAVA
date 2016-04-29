package com.practice.myapplication.data;

/**
 * Created by hagtfms on 2016-04-24.
 */
public class LocationData {
    private boolean mIsTCup;
    private boolean mIsCenter;
    private boolean mIsHcup; // holecup
    private boolean mIsMiddle;

    public LocationData(boolean isTCup, boolean isCenter, boolean isHCup, boolean isMiddle){
        mIsTCup = isTCup;
        mIsCenter = isCenter;
        mIsHcup = isHCup;
        mIsMiddle = isMiddle;
    }

    public boolean isTCup(){ return mIsTCup; }
    public boolean isCenter(){ return mIsCenter; }
    public boolean isHCup(){ return mIsHcup; }
    public boolean isMiddle(){ return mIsMiddle; }
}
