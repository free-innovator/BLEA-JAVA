package com.practice.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.practice.myapplication.R;
import com.practice.myapplication.data.GroundData;
import com.practice.myapplication.data.GroundMapData;
import com.practice.myapplication.manager.MyDBManager;
import com.practice.myapplication.manager.MyInternetManager;

/**
 * Created by hagtfms on 2016-04-30.
 */
public class PlayActivity extends Activity {
    private ImageView mTacticImageView;
    private ImageView mStageImageView;

    private GroundData mGroundData;
    private GroundMapData mGroundMapData;

    private double mTcupLaditude, mTcupLongitude;
    private double mHcupLaditude, mHcupLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        final SharedPreferences prefs =
                getSharedPreferences(TestActivity.STR_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if(prefs != null){
            mTcupLaditude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LADITUDE, 0.0f);
            mTcupLongitude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LONGITUDE, 0.0f);
            mHcupLaditude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LADITUDE, 0.0f);
            mHcupLongitude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LONGITUDE, 0.0f);
        }

        mTacticImageView = (ImageView)findViewById(R.id.iv_aplay_tactic);
        mStageImageView = (ImageView)findViewById(R.id.iv_aplay_stage);

        mTacticImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, TacticActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        mStageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, StageActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        mGroundData = MyDBManager.getGroundData(20000, 3);
        mGroundMapData = MyDBManager.getGroundMapData(20000, 3);
        if(mGroundMapData == null) {
            if (mGroundData != null) {
                Bitmap bitmap = MyInternetManager.getBitmapFromURL("/map/" + mGroundData.getImageURL());
                MyDBManager.insertGroundMap(bitmap, 20000, 3);
                mGroundMapData = MyDBManager.getGroundMapData(20000, 3);
            }
        }

        if(mGroundMapData != null){
            ImageView imageView = (ImageView)findViewById(R.id.iv_apaly_groundmap);
            imageView.setBackground(new BitmapDrawable(getResources(), mGroundMapData.getBitmap()));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    public void finish(){
        super.finish();
    }
}
