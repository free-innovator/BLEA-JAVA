package com.practice.myapplication.manager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.myapplication.R;
import com.practice.myapplication.activity.PlayActivity;
import com.practice.myapplication.data.IBeaconData;

import java.util.List;

/*
    startLeScan과 stopLeScan을 사용한 이유는 API 21 Level -> API 18 Level로 떨어트리기 위함입니다.
    주석을 따로 적은 이유는 javadoc과 겹치기 않게 하기 위함입니다. (어차피 내용도 별로 없지만요 ㅠ)
 */
/**
 * Created by hagtfms on 2016-04-18.
 *
 */
public class MyBluetoothManager {
    private static final String TAG = "MyBluetoothManager";

    private static BluetoothAdapter mBluetoothAdapter = null;
    private static IBeaconData mIBeaconData = null;
    private static boolean mIsIBeaconScanning = false;

    private static Activity curActivity = null;

    private static boolean mPowerOn = false;
    private static final int POWER_DELAY = 5000;

    private static Handler mPowerHandler = null;
    private static Runnable mPowerRun = null;

    public static boolean initSetting(@NonNull Activity context) {
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null) mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter != null){
            curActivity = context;
            mPowerRun = new Runnable(){
                @Override
                public void run(){
                    mPowerOn = false;
                }
            };
            mPowerHandler = new Handler();
            return true;
        }

        return false;
    }

    public static boolean isEnabled(){
        if(curActivity != null && mBluetoothAdapter != null) {
            if (ContextCompat.checkSelfPermission(
                    curActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return mBluetoothAdapter.isEnabled();
            }
        }
        return false;
    }

    /**
     * not used
     * @return
     */
    public static boolean enable(){
        if(mBluetoothAdapter != null) {
            if(!mBluetoothAdapter.isEnabled())
                return mBluetoothAdapter.enable();
            else
                return true;
        }
        return false;
    }
    /**
     * not used
     * @return
     */
    public static boolean disable(){
        if(mBluetoothAdapter != null) {
            if(mBluetoothAdapter.isEnabled())
                return mBluetoothAdapter.disable();
            else
                return true;
        }
        return false;
    }
    public static boolean startScanForIBeacon() {
        if (MyBluetoothManager.isEnabled()) {
            mBluetoothAdapter.startLeScan(null, mIBeaconScanCallback);
            mIsIBeaconScanning = true;
            return true;
        }
        return false;
    }
    public static void stopScanForIBeacon(){
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.stopLeScan(mIBeaconScanCallback);
            mIsIBeaconScanning = false;
        }
    }

    public static IBeaconData getIBeaconData(){
        return mIBeaconData;
    }
    public static boolean isPowerOn() { return mPowerOn; }

    /**
     * bytesToHex method
     * Found on the internet
     * http://stackoverflow.com/a/9855338
     */

    private static final char[] mHexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = mHexArray[v >>> 4];
            hexChars[j * 2 + 1] = mHexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private static final BluetoothAdapter.LeScanCallback mIBeaconScanCallback =
        new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                //Log.d(TAG, "onLeScan : " + device.getAddress());

                /**
                 * http://blog.conjure.co.uk/2014/08/ibeacons-and-android-parsing-the-uuid-major-and-minor-values/
                 */
                int startByte = 2;
                boolean patternFound = false;
                while (startByte <= 5) {
                    if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                            ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                        patternFound = true;
                        break;
                    }
                    startByte++;
                }

                if(patternFound) {
                    String uuid = null;
                    int major = 0, minor = 0;
                    if (patternFound) {
                        //Convert to hex String
                        byte[] uuidBytes = new byte[16];
                        System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                        String hexString = bytesToHex(uuidBytes);

                        //Here is your UUID
                        uuid = hexString.substring(0, 8) + "-" +
                                hexString.substring(8, 12) + "-" +
                                hexString.substring(12, 16) + "-" +
                                hexString.substring(16, 20) + "-" +
                                hexString.substring(20, 32);

                        //Here is your Major value
                        major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

                        //Here is your Minor value
                        minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
                    }
                    /* end copy **/

                    mIBeaconData = new IBeaconData(uuid, major, minor, rssi);
                    Log.i(TAG, "uuid = "+uuid+ " major = "+major+ " minor = "+minor+ " rssi = "+ rssi);
                    if((major == 20000 && minor == 3) || (major == 20000 && minor == 4)) {
                        if(!mPowerOn){
                            mPowerOn = true;
                            mPowerHandler.postDelayed(mPowerRun, POWER_DELAY);
                        }
                        else{
                            mPowerHandler.removeCallbacks(mPowerRun);
                            mPowerHandler.postDelayed(mPowerRun, POWER_DELAY);
                        }
                    }
                }
                else
                    mIBeaconData = null;
            }
        };
}