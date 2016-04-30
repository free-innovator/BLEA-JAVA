package com.practice.myapplication.manager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.myapplication.R;
import com.practice.myapplication.data.IBeaconData;

import java.util.List;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class MyBluetoothManager {
    private static BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothLeScanner mBLEScanner = null;

    private static IBeaconData mIBeaconData = null;
    private static boolean mIsIBeaconScaiing = false;

    private static Activity curActivity = null;

    public static boolean initSetting(Activity context) {
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null) mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter != null){
            curActivity = context;
            return true;
        }

        return false;
    }

    public static boolean isEnabled(){
        if(mBluetoothAdapter != null && ContextCompat.checkSelfPermission(
                curActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }
    public static boolean enable(){
        if(mBluetoothAdapter != null) {
            if(!mBluetoothAdapter.isEnabled())
                return mBluetoothAdapter.enable();
            else
                return true;
        }
        return false;
    }
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
        if (curActivity != null){
            if (ContextCompat.checkSelfPermission(curActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && MyBluetoothManager.isEnabled() && !mIsIBeaconScaiing) {
                if (mBLEScanner == null) {
                    mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    if (mBLEScanner == null) return false;
                }

                ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                mBLEScanner.startScan(null, scanSettings, mIBeaconScanCallback);
                mIsIBeaconScaiing = true;
                return true;
            }
        }
        return false;
    }
    public static void stopScanForIBeacon(){
        if(mBLEScanner != null){
            mBLEScanner.stopScan(mIBeaconScanCallback);
            mIsIBeaconScaiing = false;
        }
    }

    public static IBeaconData getIBeaconData(){
        return mIBeaconData;
    }

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
    private static final ScanCallback mIBeaconScanCallback = new ScanCallback(){
        @Override
        public void onScanResult(int callbackType, ScanResult result){
            super.onScanResult(callbackType, result);
            Log.i("onScanResult", "callbackType : " + callbackType);

//            mBLEScanner.stopScan(this);
//            mIsIBeaconScaiing = false;

            /**
             * http://blog.conjure.co.uk/2014/08/ibeacons-and-android-parsing-the-uuid-major-and-minor-values/
             */
            if(result.getScanRecord() != null){
                byte[] scanRecord = result.getScanRecord().getBytes();

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

                    mIBeaconData = new IBeaconData(uuid, major, minor, result.getRssi());
                }
                else
                    mIBeaconData = null;
            }
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results){
            super.onBatchScanResults(results);
            Log.i("onBatchScanResults", "onBatchScanResults");
        }
        @Override
        public void onScanFailed(int errorCode){
            super.onScanFailed(errorCode);
            Log.i("onScanFailed", "errorCode : " + errorCode);
        }
    };
}