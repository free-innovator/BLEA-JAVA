package com.practice.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.practice.myapplication.R;
import com.practice.myapplication.manager.MyBluetoothManager;
import com.practice.myapplication.manager.MyGPSManager;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class ConsentActivity extends Activity {
    private final Activity thisActivity = this;

    private Switch mSwitchBluetooth = null;
    private Switch mSwitchGPS = null;
    private Switch mSwitchPermission = null;

    private interface REQUEST{int BLUETOOTH=0, GPS =1, PERMISSION=2; }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        mSwitchBluetooth = (Switch)findViewById(R.id.swicth_bluetooth);
        mSwitchGPS = (Switch)findViewById(R.id.swicth_gps);
        mSwitchPermission = (Switch)findViewById(R.id.swicth_permission);

        MyBluetoothManager.initSetting(thisActivity);
        MyGPSManager.initSetting(thisActivity);
        mSwitchBluetooth.setChecked(MyBluetoothManager.isEnabled());
        mSwitchGPS.setChecked(MyGPSManager.isEnabled());
        mSwitchPermission.setChecked(
                ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
        if(mSwitchBluetooth.isChecked()) mSwitchBluetooth.setEnabled(false);
        if(mSwitchGPS.isChecked()) mSwitchGPS.setEnabled(false);
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
            case REQUEST.PERMISSION:{
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSwitchPermission.setChecked(true);
                    mSwitchPermission.setEnabled(false);
                    Log.d("ConsentActivity", "onRequestPermissionsResult - setChecked(true)");
                }else{
                    mSwitchPermission.setChecked(false);
                    Log.d("ConsentActivity", "onRequestPermissionsResult - setChecked(false)");
                }
                checkSwitch();
                break;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode){
            case REQUEST.BLUETOOTH:
                switch(resultCode){
                    case Activity.RESULT_OK:
                        mSwitchBluetooth.setChecked(true);
                        mSwitchBluetooth.setEnabled(false);
                        break;
                    case Activity.RESULT_CANCELED:
                        mSwitchBluetooth.setChecked(false);
                        break;
                }
                break;
            case REQUEST.GPS:
                if(MyGPSManager.isEnabled()){
                    mSwitchGPS.setChecked(true);
                    mSwitchGPS.setEnabled(false);
                }
                else{
                    mSwitchGPS.setChecked(false);
                }
                break;
        }
    }

    private void checkSwitch(){
        if(mSwitchBluetooth.isChecked() && mSwitchGPS.isChecked() && mSwitchPermission.isChecked()){
            Intent i = new Intent(ConsentActivity.this, TestActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
    private class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch(buttonView.getId()){
                case R.id.swicth_bluetooth:
                    Log.i("onCheckedChanged", "bluetooth check = " + isChecked);
                    if(!MyBluetoothManager.isEnabled()){
                        MyBluetoothManager.initSetting(thisActivity);
                    }

                    if (isChecked) {
                        if (!MyBluetoothManager.isEnabled()) {
                            buttonView.setChecked(false);
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, REQUEST.BLUETOOTH);
                        }
                    }
                    break;

                case R.id.swicth_gps:
                    Log.i("onCheckedChanged", "GPS check = " + isChecked);
                    if(isChecked){
                        if(!MyGPSManager.isEnabled()){
                            buttonView.setChecked(false);
                            Intent enableIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(enableIntent, REQUEST.GPS);
                        }
                    }
                    break;

                case R.id.swicth_permission:
                    Log.i("onCheckedChanged", "Permission check = " + isChecked);

                    if(isChecked){
                        if(ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED){
                            mSwitchPermission.setChecked(false);
                            if(ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION)){
                                Toast.makeText(thisActivity, "권한을 설정하지 않으면 앱을 사용할 수 없습니다.",
                                        Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(
                                        thisActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST.PERMISSION);
                            }
                            else{
                                ActivityCompat.requestPermissions(
                                        thisActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST.PERMISSION);
                            }
                        }
                    }
                    break;
            }

            checkSwitch();
        }
    }
}

