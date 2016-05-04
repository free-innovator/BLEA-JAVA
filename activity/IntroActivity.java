package com.practice.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.practice.myapplication.R;
import com.practice.myapplication.manager.MyBluetoothManager;
import com.practice.myapplication.manager.MyDBManager;
import com.practice.myapplication.manager.MyGPSManager;

/**
 * Created by hagtfms on 2016-04-17.
 */
public class IntroActivity extends Activity {
    private Handler mAnimHandler = null;
    private Runnable mAnimRun = null;
    private final Class mNextActivity = MainActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        MyBluetoothManager.initSetting(this);
        MyGPSManager.initSetting(this);
        MyDBManager.initSetting(this);

        mAnimRun = new Runnable(){
            @Override
            public void run(){
                Intent i = new Intent(IntroActivity.this, mNextActivity);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        };
        mAnimHandler = new Handler();
        mAnimHandler.postDelayed(mAnimRun, 2000);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mAnimHandler.removeCallbacks(mAnimRun);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Intent i = new Intent(IntroActivity.this, mNextActivity);
        startActivity(i);
        finish();
    }

    @Override
    public void finish(){
        super.finish();
        mAnimHandler.removeCallbacks(mAnimRun);
    }

    @Override
    public void onBackPressed(){
//        mAnimHandler.removeCallbacks(mAnimRun);
//        super.onBackPressed();
    }
}
