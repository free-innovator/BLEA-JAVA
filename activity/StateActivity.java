package com.practice.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.data.ClubData;
import com.practice.myapplication.data.IBeaconData;
import com.practice.myapplication.manager.MyBluetoothManager;
import com.practice.myapplication.manager.MyDBManager;
import com.practice.myapplication.manager.MyGPSManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hagtfms on 16. 7. 21..
 */
public class StateActivity extends Activity{
    private final String TAG = "StateActivity";
    private final long PERIOD = 100;

    private Handler mHandler;
    private Timer mScanTimer;

    private ListView mListView;
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mHandler = new Handler();

        MyGPSManager.setListener(true);
        MyBluetoothManager.startScanForIBeacon();
        mScanTimer = new Timer(true);
        if(mScanTimer != null){
            mScanTimer.schedule(new MyTimerTask(), 0, PERIOD);
        }

        /**
         * reference : http://kd3302.tistory.com/85
         */
        mAdapter = new CustomAdapter();
        mListView = (ListView) findViewById(R.id.lv_astat_list);
        mListView.setAdapter(mAdapter);
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
            mScanTimer.schedule(new MyTimerTask(), 0, PERIOD);
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
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * reference : http://berabue.blogspot.kr/2014/05/android-listview.html
     */
    private class CustomAdapter extends BaseAdapter {
        private boolean mList[][];

        private CustomAdapter(){  mList = new boolean[18][4];  }

        @Override
        public int getCount() {
            return mList.length;
        }

        @Override
        public Object getItem(int position) {  return mList[position];  }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            TextView leftTextView = null;
            TextView rightTextView = null;
            CustomHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_club, parent, false);

                leftTextView = (TextView)convertView.findViewById(R.id.tv_liclub_left);
                rightTextView = (TextView)convertView.findViewById(R.id.tv_liclub_right);

                holder = new CustomHolder();
                holder.mLeftTextView = leftTextView;
                holder.mRightTextView = rightTextView;
                convertView.setTag(holder);
            }
            else {
                holder = (CustomHolder) convertView.getTag();
                leftTextView = holder.mLeftTextView;
                rightTextView = holder.mRightTextView;
            }

            //final ClubData clubData = mList.get(pos);
            //leftTextView.setText(clubData.getName());
            //if(clubData.getMeter() != 0){
            //    rightTextView.setText(String.valueOf(clubData.getMeter()));
            //}
            else{
                rightTextView.setText("미정");
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("convertView", "onClick");
                }
            });

            return convertView;
        }

        private class CustomHolder{
            TextView mLeftTextView;
            TextView mRightTextView;
        }

        private void setOnState(int hole, int a){
            mList[hole][a] = true;
        }
        private void setOffState(int hole, int a){
            mList[hole][a] = false;
        }

        private void refresh(){
            this.notifyDataSetChanged();
        }
    }
}