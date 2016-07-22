package com.practice.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

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

    private boolean mIsStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

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
        mListView = (ListView)findViewById(R.id.lv_astat_list);
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

            final IBeaconData iBeaconData = MyBluetoothManager.getIBeaconData();
            if(iBeaconData != null && mAdapter != null){
                if(MyBluetoothManager.isPowerOn()){
                    if(mIsStart){
                        mIsStart = false;
                        if(iBeaconData.getMajor() == 20000 && iBeaconData.getMinor() == 3) { // Hcup
                            mAdapter.setOnState(3, 2);
                            mAdapter.setOffState(3, 1);
                        }
                        if(iBeaconData.getMajor() == 20000 && iBeaconData.getMinor() == 4) { // Tcup
                            mAdapter.setOnState(3, 0);
                            mAdapter.setOffState(2, 3);
                        }
                        mAdapter.refresh();
                    }
                }
                if(!MyBluetoothManager.isPowerOn()){
                    if(!mIsStart) mIsStart = true;
                    if(iBeaconData.getMajor() == 20000 && iBeaconData.getMinor() == 3) { // Hcup
                        mAdapter.setOnState(3, 3);
                        mAdapter.setOffState(3, 2);
                    }
                    if(iBeaconData.getMajor() == 20000 && iBeaconData.getMinor() == 4) { // Tcup
                        mAdapter.setOnState(3, 1);
                        mAdapter.setOffState(3, 0);
                    }
                    mAdapter.refresh();
                }
                prevIBeaconData = iBeaconData;
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

            ToggleButton button[] = new ToggleButton[4];
            TextView textView = null;
            CustomHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_state, parent, false);

                button[0] = (ToggleButton)convertView.findViewById(R.id.tgbtn_lis_1);
                button[1] = (ToggleButton)convertView.findViewById(R.id.tgbtn_lis_2);
                button[2] = (ToggleButton)convertView.findViewById(R.id.tgbtn_lis_3);
                button[3] = (ToggleButton)convertView.findViewById(R.id.tgbtn_lis_4);
                textView = (TextView)convertView.findViewById(R.id.tv_lis_hole);

                holder = new CustomHolder();
                for(int i=0; i<holder.mToggleButton.length; i++){
                    holder.mToggleButton[i] = button[i];
                }
                holder.mTextView = textView;
                convertView.setTag(holder);
            }
            else {
                holder = (CustomHolder) convertView.getTag();
                for(int i=0; i<holder.mToggleButton.length; i++){
                    button[i] = holder.mToggleButton[i];
                }
                textView = holder.mTextView;
            }

            for(int i=0; i<holder.mToggleButton.length; i++){
                if(button[i] != null) {
                    button[i].setChecked(mList[pos][i]);
                    button[i].setEnabled(false);
                }
            }
            textView.setText("Hole "+(pos+1));

            return convertView;
        }

        private class CustomHolder{
            ToggleButton mToggleButton[] = new ToggleButton[4];
            TextView mTextView = null;
        }

        private void setOnState(int hole, int i){
            mList[hole-1][i] = true;
        }
        private void setOffState(int hole, int i){
            mList[hole-1][i] = false;
        }

        private void refresh(){
            this.notifyDataSetChanged();
        }
    }
}