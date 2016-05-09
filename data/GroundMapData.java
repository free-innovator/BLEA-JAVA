package com.practice.myapplication.data;

import android.graphics.Bitmap;

/**
 * Created by hagtfms on 2016-05-05.
 */
public class GroundMapData {
    private int mStoreid;
    private int mGroundid;
    private Bitmap mBitmap;

    public static final String TABLE_NAME = "tbl_ground_map";
    public static final String COLUMN_NAME_STOREID = "Gstoreid";
    public static final String COLUMN_NAME_GROUNDID = "Ggroundid";
    public static final String COLUMN_NAME_MAPSTREAM = "Gmapstream";

    public GroundMapData(int storeid, int groundid, Bitmap bitmap){
        mStoreid = storeid;
        mGroundid = groundid;
        mBitmap = bitmap;
    }

    public int getStoreid() { return mStoreid; }
    public int getGroundid() { return mGroundid; }
    public Bitmap getBitmap() { return mBitmap; }
}
