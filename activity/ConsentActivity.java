package com.practice.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.practice.myapplication.R;
import com.practice.myapplication.fragment.MainFragment;
import com.practice.myapplication.manager.MyBluetoothManager;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class ConsentActivity extends Activity {
    private final Activity thisActivity = this;

    private Switch mSwitchBluetooth = null;
    private Switch mSwitchGPS = null;
    private Switch mSwitchPermission = null;

    private final int MY_PERMISSIONS_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        mSwitchBluetooth = (Switch)findViewById(R.id.swicth_bluetooth);
        mSwitchGPS = (Switch)findViewById(R.id.swicth_gps);
        mSwitchPermission = (Switch)findViewById(R.id.swicth_permission);

        MyBluetoothManager.initSetting(thisActivity);
        mSwitchBluetooth.setChecked(MyBluetoothManager.isEnabled());
        mSwitchGPS.setChecked(true);
        mSwitchPermission.setChecked(
                ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
        if(mSwitchPermission.isChecked()) mSwitchPermission.setEnabled(false);

        checkSwitch();

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new MyOnCheckedChangeListener();
        mSwitchBluetooth.setOnCheckedChangeListener(onCheckedChangeListener);
        mSwitchGPS.setOnCheckedChangeListener(onCheckedChangeListener);
        mSwitchPermission.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST:{
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSwitchPermission.setChecked(true);
                    mSwitchPermission.setEnabled(false);
                }else{
                    mSwitchPermission.setChecked(false);
                }
                return;
            }
        }
    }

    private void checkSwitch(){
        if(mSwitchBluetooth.isChecked() && mSwitchGPS.isChecked() && mSwitchPermission.isChecked()){
            Intent i = new Intent(ConsentActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
    private class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.equals(findViewById(R.id.swicth_bluetooth))) {
                Log.i("onCheckedChanged", "bluetooth check = " + isChecked);

                if(!MyBluetoothManager.isEnabled()){
                    MyBluetoothManager.initSetting(thisActivity);
                }

                if (isChecked) {
                    if (!MyBluetoothManager.enable())
                        buttonView.setChecked(false);
                } else {
                    if (!MyBluetoothManager.disable())
                        buttonView.setChecked(true);
                }
            }

            if(buttonView.equals(findViewById(R.id.swicth_gps))) {
                Log.i("onCheckedChanged", "GPS check = " + isChecked);
            }

            if(buttonView.equals(findViewById(R.id.swicth_permission))){
                Log.i("onCheckedChanged", "Permission check = " + isChecked);

                if(isChecked){
                    if(ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED){

                        if(ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION)){
                            Toast.makeText(thisActivity, "권한을 설정하지 않으면 앱을 사용할 수 없습니다.",
                                    Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(
                                    thisActivity,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST);
                        }
                        else{
                            ActivityCompat.requestPermissions(
                                    thisActivity,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST);
                        }
                    }
                }
            }

            checkSwitch();
        }
    }
}

