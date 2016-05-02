package com.practice.myapplication.data;

/**
 * Created by hagtfms on 2016-05-02.
 */
public class StoreData {
    private int mId;
    private String mName;

    public StoreData(int id, String name){
        mId = id;
        mName = name;
    }

    public int getId(){ return mId; }
    public String getName(){ return mName; }
}
