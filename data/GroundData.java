package com.practice.myapplication.data;

/**
 * Created by hagtfms on 2016-05-06.
 */
public class GroundData {
    private int mRegulationFigure;
    private String mTactic;
    private String mImageURL;
    private int mImageVer;

    public GroundData(int regFigure, String tactic, String imageURL, int imageVer){
        mRegulationFigure = regFigure;
        mTactic = tactic;
        mImageURL = imageURL;
        mImageVer = imageVer;
    }

    public int getRegulationFigure() { return mRegulationFigure; }
    public String getTactic() { return mTactic; }
    public String getImageURL() { return mImageURL; }
    public int mImageVer() { return mImageVer; }
}
