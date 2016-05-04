package com.practice.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.data.IBeaconData;
import com.practice.myapplication.manager.MyBluetoothManager;
import com.practice.myapplication.manager.MyGPSManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hagtfms on 2016-04-26.
 */
public class TestActivity extends Activity {
    public final static String STR_PREFERENCES_NAME = "Test";
    public final static String STR_TCUP_LOC_LADITUDE = "TCUP_LOC_LADITUDE";
    public final static String STR_TCUP_LOC_LONGITUDE = "TCUP_LOC_LONGITUDE";
    public final static String STR_HCUP_LOC_LADITUDE = "STR_HCUP_LOC_LADITUDE";
    public final static String STR_HCUP_LOC_LONGITUDE = "STR_HCUP_LOC_LONGITUDE";

    private final String TAG = "TestActivity";

    private Handler mHandler;
    private Timer mScanTimer;

    private Button mTCupButton;
    private Button mHCupButton;
    private TextView mTvTCup;
    private TextView mTvHCup;
    private TextView mTvLaditude;
    private TextView mTvLongitude;
    private TextView mTvProvider;
    private TextView mTvAccuracy;

    private TextView mTvRssi;
    private TextView mTvMajor;
    private TextView mTvMinor;
    private TextView mTvUuid;
    private TextView mTvOnoff;

    private boolean isTCupSetting = false;
    private boolean isHCupSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mHandler = new Handler();

        mTCupButton = (Button)findViewById(R.id.btn_atest_tcup);
        mHCupButton = (Button)findViewById(R.id.btn_atest_hcup);

        mTvTCup = (TextView)findViewById(R.id.tv_atest_tcup);
        mTvHCup = (TextView)findViewById(R.id.tv_atest_hcup);
        mTvLaditude = (TextView)findViewById(R.id.tv_atest_laditude);
        mTvLongitude = (TextView)findViewById(R.id.tv_atest_longitude);
        mTvProvider = (TextView)findViewById(R.id.tv_atest_provider);
        mTvAccuracy = (TextView)findViewById(R.id.tv_atest_accuracy);

        mTvRssi = (TextView)findViewById(R.id.tv_atest_rssi);
        mTvMajor = (TextView)findViewById(R.id.tv_atest_major);
        mTvMinor = (TextView)findViewById(R.id.tv_atest_minor);
        mTvUuid = (TextView)findViewById(R.id.tv_atest_uuid);
        mTvOnoff = (TextView)findViewById(R.id.tv_atest_onoff);

        final SharedPreferences prefs =
                getSharedPreferences(STR_PREFERENCES_NAME, Context.MODE_PRIVATE);

        mTCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mTCupButton Click");
                Location location = MyGPSManager.getLocation();
                if(location != null){
                    mTvTCup.setText(location.getLatitude() + ", " + location.getLongitude());

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat(STR_TCUP_LOC_LADITUDE, (float)location.getLatitude());
                    editor.putFloat(STR_TCUP_LOC_LONGITUDE, (float)location.getLongitude());
                    editor.commit();

                    isTCupSetting = true;
                }
            }
        });
        mHCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mHCupButton Click");
                Location location = MyGPSManager.getLocation();
                if(location != null){
                    mTvHCup.setText(location.getLatitude() + ", " + location.getLongitude());

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat(STR_HCUP_LOC_LADITUDE, (float)location.getLatitude());
                    editor.putFloat(STR_HCUP_LOC_LONGITUDE, (float)location.getLongitude());
                    editor.commit();

                    isHCupSetting = true;
                }
            }
        });

        MyGPSManager.setListener(true);
        MyBluetoothManager.startScanForIBeacon();
        mScanTimer = new Timer(true);
        if(mScanTimer != null){
            mScanTimer.schedule(new MyTimerTask(), 0, 300);
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
                                if(mTvLaditude != null) mTvLaditude.setText(String.valueOf(prevLocation.getLatitude()));
                                if(mTvLongitude != null) mTvLongitude.setText(String.valueOf(prevLocation.getLongitude()));
                                if(mTvProvider != null){
                                    if(prevLocation.getProvider().equals(LocationManager.GPS_PROVIDER))
                                        mTvProvider.setText("GPS");
                                    else if(prevLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER))
                                        mTvProvider.setText("Network");
                                }
                                if(mTvAccuracy != null) mTvAccuracy.setText(String.valueOf(prevLocation.getAccuracy()));
                            }
                        });
                    }
                }
            }

            IBeaconData iBeaconData = MyBluetoothManager.getIBeaconData();
            if(iBeaconData != null){
                if(!iBeaconData.equals(prevIBeaconData)){
                    prevIBeaconData = iBeaconData;

                    if(prevIBeaconData != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mTvRssi != null) mTvRssi.setText("RSSI = " + prevIBeaconData.getRssi());
                                if(mTvMajor != null) mTvMajor.setText("Major = " + prevIBeaconData.getMajor());
                                if(mTvMinor != null) mTvMinor.setText("Minor = " + prevIBeaconData.getMinor());
                                if(mTvUuid != null) mTvUuid.setText(prevIBeaconData.getUuid());
                            }
                        });
                    }
                }
            }
        }
    }
}
