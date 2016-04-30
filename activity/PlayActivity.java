package com.practice.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.manager.MyGPSManager;

import java.util.TimerTask;

/**
 * Created by hagtfms on 2016-04-26.
 */
public class PlayActivity extends Activity {
    private Handler mHandler;

    private Button mTCupButton;
    private Button mHCupButton;
    private TextView mTvTCup;
    private TextView mTvHCup;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mHandler = new Handler();

        mTCupButton = (Button)findViewById(R.id.btn_aplay_tcup);
        mHCupButton = (Button)findViewById(R.id.btn_aplay_hcup);
        mTvTCup = (TextView)findViewById(R.id.tv_aplay_tcup);
        mTvHCup = (TextView)findViewById(R.id.tv_aplay_hcup);

        MyGPSManager.initSetting(this);

        mTCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PlayActivity", "mTCupButton Click");
                Location location = MyGPSManager.getLocation();
                if(location != null){
                    mTvTCup.setText(location.getLatitude() + ", " + location.getLongitude());
                }
            }
        });
        mHCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PlayActivity", "mHCupButton Click");
                Location location = MyGPSManager.getLocation();
                if(location != null){
                    mTvHCup.setText(location.getLatitude() + ", " + location.getLongitude());
                }
            }
        });

        MyGPSManager.setListener(true);
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
        //if(mScanTimer != null) mScanTimer.cancel();
        MyGPSManager.setListener(false);
        super.finish();
    }


    private  class MyTimerTask extends TimerTask {
        private Location prevLocation = null;
        @Override
        public void run(){
            Location location = MyGPSManager.getLocation();
            if(location != null){
                if(!location.equals(prevLocation)){
                    prevLocation = location;

                    if(prevLocation != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mTvTCup != null) mTvTCup.setText("" + prevLocation.getLatitude() + ", " + prevLocation.getLongitude());
                                if(mTvHCup != null) mTvHCup.setText("" + prevLocation.getLatitude() + ", " + prevLocation.getLongitude());
                            }
                        });
                    }
                }
            }
        }
    }
}
