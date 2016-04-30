package com.practice.myapplication.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class MyGPSManager {
    private static LocationManager mLocationManager = null;
    private static Location mLocation = null;
    private static Activity curActivity = null;

    private static MyLocationListener mLocationListener = null;

    public static boolean initSetting(@NonNull Activity context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

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
                    long minTime = 500;
                    float minDistance = 0.0f;

                    if(mLocationListener != null){
                        mLocationManager.removeUpdates(mLocationListener);
                    }
                    mLocationListener = new MyLocationListener();
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            minTime, minDistance, mLocationListener);
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            minTime, minDistance, mLocationListener);
                    Log.d("MyGPSManager", "setListener : true");
                    return true;
                }
            }
        }
        else{
            if(mLocationListener != null){
                mLocationManager.removeUpdates(mLocationListener);
                mLocationListener = null;
            }
            return true;
        }

        Log.d("MyGPSManager", "setListener : false");
        return false;
    }

    public static boolean isEnabled(){
        if(curActivity != null && mLocationManager != null){
            if(ContextCompat.checkSelfPermission(
                    curActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                    mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                return true;
            }
        }
        return false;
    }

    private static class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
                Log.d("MyLocationListener", "onLocationChanged - GPS");
            }
            else if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
                Log.d("MyLocationListener", "onLocationChanged - NETWORK");
            }
            mLocation = location;
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("MyLocationListener", "onStatusChanged");
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("MyLocationListener", "onProviderEnabled");
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("MyLocationListener", "onProviderDisabled");
        }
    }
}
