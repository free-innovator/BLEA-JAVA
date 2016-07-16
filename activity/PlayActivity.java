package com.practice.myapplication.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.data.GroundData;
import com.practice.myapplication.data.GroundMapData;
import com.practice.myapplication.data.IBeaconData;
import com.practice.myapplication.manager.MyBluetoothManager;
import com.practice.myapplication.manager.MyDBManager;
import com.practice.myapplication.manager.MyGPSManager;
import com.practice.myapplication.manager.MyInternetManager;
import com.practice.myapplication.widget.DrawView;

import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hagtfms on 2016-04-30.
 */
public class PlayActivity extends Activity {
    private final String TAG = "PalyActivity";
    private final int MIN_ACCURACY = 99999;
    private final long PERIOD = 200;

    private Handler mHandler;
    private Timer mScanTimer;

    private long mDelayTime;
    private boolean mPowerOn = false;

    //private ImageView mTacticImageView;
    //private ImageView mStageImageView;
    private Button mTacticButton;

    private TextView mTvRssi;
    private TextView mTvAcc;

    private GroundData mGroundData;
    private GroundMapData mGroundMapData;

    private double mTcupLaditude, mTcupLongitude;
    private double mHcupLaditude, mHcupLongitude;

    private double s = 0, t = 0, u = 0, v = 0; // for calculate crood

    DrawView dmDrawView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = (View)getLayoutInflater().inflate(R.layout.activity_play, null);

        if(view != null){
            FrameLayout frameLayout = (FrameLayout)view.findViewById(R.id.fl_aplay_main);
            dmDrawView = new DrawView(this);
            dmDrawView.setLayoutParams(new ActionBar.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            frameLayout.addView(dmDrawView);

            setContentView(frameLayout);
        }
        else
            setContentView(R.layout.activity_play);

        mTvRssi = (TextView)findViewById(R.id.tv_aplay_rssi);
        mTvAcc = (TextView)findViewById(R.id.tv_aplay_acc);

        final SharedPreferences prefs =
                getSharedPreferences(TestActivity.STR_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if(prefs != null){
            mTcupLaditude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LADITUDE, 0.0f);
            mTcupLongitude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LONGITUDE, 0.0f);
            mHcupLaditude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LADITUDE, 0.0f);
            mHcupLongitude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LONGITUDE, 0.0f);

            if(dmDrawView != null){
                int w = dmDrawView.getWindowWidth(), h = dmDrawView.getWindowHeight();
                double a = mHcupLongitude, b = mHcupLaditude, c = mTcupLongitude, d = mTcupLaditude;
                double div = a*d-b*c;
                s = (0.3*w*d-0.7*w*b)/div;
                t = (0.15*h*d - 0.85*h*b)/div;
                u = (0.7*w*a-0.3*w*c)/div;
                v = (0.85*h*a - 0.15*h*c)/div;
            }
        }

       // mTacticImageView = (ImageView)findViewById(R.id.iv_aplay_tactic);
       // mStageImageView = (ImageView)findViewById(R.id.iv_aplay_stage);

        /*
        mTacticImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, TacticActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        */
        /*
        mStageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, StageActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        */

        mGroundData = MyDBManager.getGroundData(20000, 3);
        mGroundMapData = MyDBManager.getGroundMapData(20000, 3);
        if(mGroundMapData == null) {
            if (mGroundData != null) {
                Bitmap bitmap = MyInternetManager.getBitmapFromURL("/map/" + mGroundData.getImageURL());
                MyDBManager.insertGroundMap(bitmap, 20000, 3);
                mGroundMapData = MyDBManager.getGroundMapData(20000, 3);
            }
        }
        /*
        try{
            MessageDigest md = MessageDigest.getInstance("SHA3-512");
        }
        catch(Exception e){
            e.printStackTrace();
        }*/

        if(mGroundMapData != null){
            ImageView imageView = (ImageView)findViewById(R.id.iv_apaly_groundmap);
            //imageView.setBackground(new BitmapDrawable(getResources(), mGroundMapData.getBitmap()));
            imageView.setImageBitmap(mGroundMapData.getBitmap());
        }

        mHandler = new Handler();
        MyGPSManager.setListener(true);
        MyBluetoothManager.startScanForIBeacon();
        mScanTimer = new Timer(true);
        if(mScanTimer != null){
            mScanTimer.schedule(new MyTimerTask(), 0, PERIOD);
            mPowerOn = false;
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
        MyBluetoothManager.stopScanForIBeacon();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        mScanTimer = new Timer(true);
        if(mScanTimer != null){
            mScanTimer.schedule(new MyTimerTask(), 0, 500);
        }
        MyGPSManager.setListener(true);
        MyBluetoothManager.startScanForIBeacon();
    }

    @Override
    public void finish(){
        if(mScanTimer != null) {
            mScanTimer.cancel();
            mScanTimer = null;
        }
        MyGPSManager.setListener(false);
        MyBluetoothManager.stopScanForIBeacon();
        super.finish();
    }


    private  class MyTimerTask extends TimerTask {
        private Location prevLocation = null;
        private IBeaconData prevIBeaconData = null;
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
                                if(mTvAcc != null) mTvAcc.setText(String.valueOf(prevLocation.getAccuracy()));
                                if(prevLocation.getAccuracy() < MIN_ACCURACY){
                                    if(dmDrawView != null){
                                        double lati = prevLocation.getLatitude(), longi = prevLocation.getLongitude();
                                        double x = longi*s + lati*u;
                                        double y = longi*t + lati*v;

                                        dmDrawView.setPos((float)x, (float)y);
                                        dmDrawView.invalidate();
                                        Log.d(TAG, "draw "+ x +", " + y);
                                    }
                                }
                            }
                        });
                    }
                }
            }

            IBeaconData iBeaconData = MyBluetoothManager.getIBeaconData();
            if(iBeaconData != null){
                if(!iBeaconData.equals(prevIBeaconData)){
                    mDelayTime = 0;
                    if(mPowerOn){
                        // tactic
                    }
                    mPowerOn = true;
                    prevIBeaconData = iBeaconData;

                    if(prevIBeaconData != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mTvRssi != null) mTvRssi.setText(String.valueOf(prevIBeaconData.getRssi()));
                            }
                        });
                    }
                }
                else{
                    mDelayTime += PERIOD;
                    if(mPowerOn && mDelayTime >= 3000){
                        mPowerOn = false;
                        // score
                    }
                }
            }
        }
    }
}
