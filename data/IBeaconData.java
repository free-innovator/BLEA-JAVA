package com.practice.myapplication.data;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class IBeaconData {
    private String mUuid;
    private int mMajor;
    private int mMinor;
    private int mRssi;

    public IBeaconData(String uuid, int major, int minor, int rssi){
        mUuid = uuid;
        mMajor = major;
        mMinor = minor;
        mRssi = rssi;
    }

    public String getUuid() {  return mUuid;  }
    public int getMajor() {  return mMajor;  }
    public int getMinor() {  return mMinor;  }
    public int getRssi() {  return mRssi;  }

    @Override
    public boolean equals(Object o) {
        if(o instanceof IBeaconData){
            IBeaconData iBeaconData = (IBeaconData)o;
            if(mUuid.equals(iBeaconData.mUuid) &&
                    mMajor == iBeaconData.mMajor &&
                    mMinor == iBeaconData.mMinor &&
                    mRssi == iBeaconData.mRssi)
                return true;
            else
                return false;
        }
        else
            return false;
    }
}
