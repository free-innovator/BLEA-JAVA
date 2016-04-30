package com.practice.myapplication.activity;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.manager.MyGPSManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hagtfms on 2016-04-26.
 */
public class TestActivity extends Activity {
    private Handler mHandler;
    private Timer mScanTimer;

    private Button mTCupButton;
    private Button mHCupButton;
    private TextView mTvTCup;
    private TextView mTvHCup;
    private TextView mTvLaditude;
    private TextView mTvLongitude;
    private TextView mTvProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mHandler = new Handler();

        mTCupButton = (Button)findViewById(R.id.btn_aplay_tcup);
        mHCupButton = (Button)findViewById(R.id.btn_aplay_hcup);
        mTvTCup = (TextView)findViewById(R.id.tv_aplay_tcup);
        mTvHCup = (TextView)findViewById(R.id.tv_aplay_hcup);
        mTvLaditude = (TextView)findViewById(R.id.tv_aplay_laditude);
        mTvLongitude = (TextView)findViewById(R.id.tv_aplay_longitude);
        mTvProvider = (TextView)findViewById(R.id.tv_aplay_provider);

        mTCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TestActivity", "mTCupButton Click");
                Location location = MyGPSManager.getLocation();
                if(location != null){
                    mTvTCup.setText(location.getLatitude() + ", " + location.getLongitude());
                }
            }
        });
        mHCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TestActivity", "mHCupButton Click");
                Location location = MyGPSManager.getLocation();
                if(location != null){
                    mTvHCup.setText(location.getLatitude() + ", " + location.getLongitude());
                }
            }
        });

        MyGPSManager.setListener(true);
        mScanTimer = new Timer(true);
        if(mScanTimer != null){
            mScanTimer.schedule(new MyTimerTask(), 0, 500);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mScanTimer != null) {
            mScanTimer.cancel();
            mScanTimer = null;
        }
        MyGPSManager.setListener(false);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        mScanTimer = new Timer(true);
        if(mScanTimer != null){
            mScanTimer.schedule(new MyTimerTask(), 0, 500);
        }
        MyGPSManager.setListener(true);
    }

    @Override
    public void finish(){
        if(mScanTimer != null) {
            mScanTimer.cancel();
            mScanTimer = null;
        }
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
                                if(mTvLaditude != null) mTvLaditude.setText(String.valueOf(prevLocation.getLatitude()));
                                if(mTvLongitude != null) mTvLongitude.setText(String.valueOf(prevLocation.getLongitude()));
                                if(mTvProvider != null){
                                    if(prevLocation.getProvider().equals(LocationManager.GPS_PROVIDER))
                                        mTvProvider.setText("GPS");
                                    else if(prevLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER))
                                        mTvProvider.setText("Network");
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
