package com.practice.myapplication.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class MyGPSManager {
    private static LocationManager mLocationManager = null;
    private static Location mLocation = null;
    private static Activity curActivity = null;

    public static boolean initSetting(Activity context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        curActivity = context;

        if(mLocationManager != null){
            curActivity = context;
            return true;
        }
        return false;
    }
    public static Location getLocation(){
        return mLocation;
    }
    public static boolean setListener(boolean isSetting){
        if(isSetting){
            if(curActivity != null){
                if(ContextCompat.checkSelfPermission(curActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ){
                    long minTime = 100;
                    float minDistance = 0.f;

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            minTime, minDistance, new MyLocationListener());
                    return true;
                }
            }
        }
        else{
        }

        return false;
    }

    public static boolean isEnabled(){
        if(mLocationManager != null && ContextCompat.checkSelfPermission(
                curActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ){
            return true;
        }
        return false;
    }

    private static class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
