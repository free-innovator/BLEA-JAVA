package com.practice.myapplication.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.data.IBeaconData;
import com.practice.myapplication.manager.MyBluetoothManager;
import com.practice.myapplication.manager.MyGPSManager;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hagtfms on 2016-04-28.
 * No used
 * No used
 * No used
 * No used
 * No used X 100
 */

public class TestFragment extends Fragment {
    private Handler mHandler;
    private Timer mScanTimer;

    private TextView mTvUuid;
    private TextView mTvMajor;
    private TextView mTvMinor;
    private TextView mTvRssi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mHandler = new Handler();

        mTvUuid = (TextView)v.findViewById(R.id.tv_uuid);
        mTvMajor = (TextView)v.findViewById(R.id.tv_major);
        mTvMinor = (TextView)v.findViewById(R.id.tv_minor);
        mTvRssi = (TextView)v.findViewById(R.id.tv_rssi);
        final Button btnScan = (Button)v.findViewById(R.id.btn_scan);

        if(btnScan != null){
            btnScan.setOnClickListener(new View.OnClickListener() {
                private boolean isPlayingTimer = false;
                @Override
                public void onClick(View v) {
                    if(isPlayingTimer == false) {
                        isPlayingTimer = true;
                        MyBluetoothManager.startScanForIBeacon();
                        btnScan.setText("Scanning");

                        mScanTimer = new Timer(true);
                        if(mScanTimer != null)
                            mScanTimer.schedule(new MyTimerTask(), 0, 200);
                    }
                    else{
                        MyBluetoothManager.stopScanForIBeacon();
                        btnScan.setText("Scan");
                        if(mScanTimer != null)
                            mScanTimer.cancel();
                        isPlayingTimer = false;
                    }
                }
            });
        }

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroyView(){
        if(mScanTimer != null) mScanTimer.cancel();
        MyBluetoothManager.stopScanForIBeacon();
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private  class MyTimerTask extends TimerTask {
        private IBeaconData prevIBeaconData = null;
        @Override
        public void run(){
            IBeaconData iBeaconData = MyBluetoothManager.getIBeaconData();
            if(iBeaconData != null){
                if(!iBeaconData.equals(prevIBeaconData)){
                    prevIBeaconData = iBeaconData;

                    if(prevIBeaconData != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mTvUuid != null) mTvUuid.setText("UUID = " + prevIBeaconData.getUuid());
                                if(mTvMajor != null) mTvMajor.setText("Major = " + prevIBeaconData.getMajor());
                                if(mTvMinor != null) mTvMinor.setText("Minor = " + prevIBeaconData.getMinor());
                                if(mTvRssi != null) mTvRssi.setText("RSSI = " + prevIBeaconData.getRssi());
                            }
                        });
                    }
                }
            }
        }
    }
}
