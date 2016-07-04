package com.practice.myapplication.data;

/**
 * Created by hagtfms on 2016-05-02.
 */
public class ScoreData {
    public final static int NON_DATA = -10;
    private int[] mScoreList = null;
    private int mCourseid = -1;

    public ScoreData(int courseid, int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8, int s9,
                     int s10, int s11, int s12, int s13, int s14, int s15, int s16, int s17, int s18){
        mCourseid = courseid;
        mScoreList = new int[19];
        mScoreList[0] = NON_DATA;
        mScoreList[1] = s1;
        mScoreList[2] = s2;
        mScoreList[3] = s3;
        mScoreList[4] = s4;
        mScoreList[5] = s5;
        mScoreList[6] = s6;
        mScoreList[7] = s7;
        mScoreList[8] = s8;
        mScoreList[8] = s8;
        mScoreList[9] = s9;
        mScoreList[10] = s10;
        mScoreList[11] = s11;
        mScoreList[12] = s12;
        mScoreList[13] = s13;
        mScoreList[14] = s14;
        mScoreList[15] = s15;
        mScoreList[16] = s16;
        mScoreList[17] = s17;
        mScoreList[18] = s18;
    }

    public int getScore(int pos){
        if(mScoreList != null){
            if(1 <= pos && pos <= 18){
                return mScoreList[pos];
            }
        }
        return NON_DATA;
    }
    public int getCourseId(){ return mCourseid; }
}
