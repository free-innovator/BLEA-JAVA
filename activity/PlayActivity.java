package com.practice.myapplication.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.data.ClubData;
import com.practice.myapplication.data.GroundData;
import com.practice.myapplication.data.GroundMapData;
import com.practice.myapplication.data.IBeaconData;
import com.practice.myapplication.manager.MyBluetoothManager;
import com.practice.myapplication.manager.MyDBManager;
import com.practice.myapplication.manager.MyGPSManager;
import com.practice.myapplication.manager.MyInternetManager;
import com.practice.myapplication.widget.DrawView;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by hagtfms on 2016-04-30.
 */
public class PlayActivity extends Activity {
    private final String TAG = "PlayActivity";
    private final boolean DEB = true;
    private final int MIN_ACCURACY = 10;
    private final long PERIOD = 100;

    private static int mWidthPixels = 0, mHeightPixels = 0;

    private Handler mHandler;
    private Timer mScanTimer;
    private static PopupWindow mPopupWindow = null;

    private boolean mIsScorePopup = false;
    private boolean mIsStart = true;

    private View mTacTicLayout, mScoreLayout;

    //private ImageView mTacticImageView;
    //private ImageView mStageImageView;
    //private Button mStateButton;

    private TextView mTvRssi;
    private TextView mTvAcc;
    private TextView mTvClub;

    private static GroundData mGroundData;
    private GroundMapData mGroundMapData;

    private double mTcupLaditude, mTcupLongitude;
    private double mHcupLaditude, mHcupLongitude;
    private double mDistance;

    private double s = 0, t = 0, u = 0, v = 0; // for calculate crood

    DrawView dmDrawView = null;

    public void startStateActivity(){
        Intent i = new Intent(PlayActivity.this, StateActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Point realSize = new Point();
        try{
            WindowManager w = getWindowManager();
            Display d = w.getDefaultDisplay();
            Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
            mWidthPixels = realSize.x;
            mHeightPixels = realSize.y;
        }catch (Exception ignored){
        }

        View view = (View)getLayoutInflater().inflate(R.layout.activity_play, null);
        if(view != null){
            FrameLayout frameLayout = (FrameLayout)view.findViewById(R.id.fl_aplay_main);
            dmDrawView = new DrawView(this);
            dmDrawView.setLayoutParams(new ActionBar.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            frameLayout.addView(dmDrawView);

            setContentView(frameLayout);
        }
        else
            setContentView(R.layout.activity_play);




        //mStateButton = (Button)findViewById(R.id.btn_aplay_state);
        mTacTicLayout = getLayoutInflater().inflate(R.layout.popup_tactic, null);
        mScoreLayout = getLayoutInflater().inflate(R.layout.popup_score, null);
        mTvRssi = (TextView)findViewById(R.id.tv_aplay_rssi);
        mTvAcc = (TextView)findViewById(R.id.tv_aplay_acc);
        mTvClub = (TextView)findViewById(R.id.tv_aplay_club);

        final SharedPreferences prefs =
                getSharedPreferences(TestActivity.STR_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if(prefs != null){
            mTcupLaditude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LADITUDE, 0.0f);
            mTcupLongitude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LONGITUDE, 0.0f);
            mHcupLaditude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LADITUDE, 0.0f);
            mHcupLongitude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LONGITUDE, 0.0f);

            /*
                근사오차 제외... 즉, 두 좌표의 점이 매우 근사해야 오차가 적어짐
                경도 x 위도 y라 한다면

                반지름 r = 63781000m
                y 0~180, x 0~180이라 가정하면

                위도 1도당 2*pi*r*cos(abs(y-90))/360
                경도 1도당 2*pi*r/360

                y가 -90~90이면 90을 빼지 않아도 됨.
             */
            if(dmDrawView != null){
                int w = dmDrawView.getWindowWidth(), h = dmDrawView.getWindowHeight();
                double a = mHcupLongitude, b = mHcupLaditude, c = mTcupLongitude, d = mTcupLaditude;
                double div = a*d-b*c;
                s = (DrawView.BLUE_X*w*d-DrawView.RED_X*w*b)/div;
                t = (DrawView.BLUE_Y*h*d - DrawView.RED_Y*h*b)/div;
                u = (DrawView.RED_X*w*a-DrawView.BLUE_X*w*c)/div;
                v = (DrawView.RED_Y*h*a - DrawView.BLUE_Y*h*c)/div;

                double x1 = a, x2 = c, y1 = b, y2 = d;
                double r = 6378100.0; // meter
                double longiPer = 2*Math.PI*r*Math.cos(mHcupLaditude)/360.0;
                double latiPer = 2*Math.PI*r/360.0;

                mDistance = Math.sqrt(Math.pow((x1-x2)*longiPer, 2.0) + Math.pow((y1-y2)*latiPer, 2.0));
                if(DEB) Log.d(TAG, "longiPer = "+longiPer + " latiPer = " + latiPer);
                if(DEB) Log.d(TAG, "mDistance = " + mDistance);
            }
        }

       // mTacticImageView = (ImageView)findViewById(R.id.iv_aplay_tactic);
       // mStageImageView = (ImageView)findViewById(R.id.iv_aplay_stage);

        /*
        mTacticImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, TacticActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        */
        /*
        mStageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, StageActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        */

        mGroundData = MyDBManager.getGroundData(20000, 3);
        mGroundMapData = MyDBManager.getGroundMapData(20000, 3);
        if(mGroundMapData == null) {
            if (mGroundData != null) {
                Bitmap bitmap = MyInternetManager.getBitmapFromURL("/map/" + mGroundData.getImageURL());
                MyDBManager.insertGroundMap(bitmap, 20000, 3);
                mGroundMapData = MyDBManager.getGroundMapData(20000, 3);
            }
        }
        /*
        try{
            MessageDigest md = MessageDigest.getInstance("SHA3-512");
        }
        catch(Exception e){
            e.printStackTrace();
        }*/

        if(mGroundMapData != null){
            ImageView imageView = (ImageView)findViewById(R.id.iv_apaly_groundmap);
            //imageView.setBackground(new BitmapDrawable(getResources(), mGroundMapData.getBitmap()));
            imageView.setImageBitmap(mGroundMapData.getBitmap());
        }

        mHandler = new Handler();
        MyGPSManager.setListener(true);
        MyBluetoothManager.startScanForIBeacon();
        mScanTimer = new Timer(true);
        if(mScanTimer != null){
            mScanTimer.schedule(new MyTimerTask(), 0, PERIOD);
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
        private boolean isCalculation = false;
        @Override
        public void run(){
            isCalculation = false;
            final IBeaconData iBeaconData = MyBluetoothManager.getIBeaconData();
            if(iBeaconData != null){
                if(MyBluetoothManager.isPowerOn()){
                    if(mIsStart){
                        mIsStart = false;
                        if(iBeaconData.getMajor() == 20000 && iBeaconData.getMinor() == 3) { // Hcup
                            mIsScorePopup = true;
                        }
                        if(iBeaconData.getMajor() == 20000 && iBeaconData.getMinor() == 4) { // Tcup
                            Log.d(TAG, "popup TacTic");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showTacTic();
                                }
                            });
                        }
                    }

                    if(!iBeaconData.equals(prevIBeaconData)){
                        if(iBeaconData.getMajor() == 20000 && iBeaconData.getMinor() == 3) { // Hcup
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    isCalculation = true;
                                    recommendClub(calculateAccuracy(-77, (double)iBeaconData.getRssi()));
                                }
                            });
                        }
                    }
                }
                if(!MyBluetoothManager.isPowerOn()){
                    if(!mIsStart) mIsStart = true;
                    if(mIsScorePopup) {
                        mIsScorePopup = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showScore();
                            }
                        });
                    }
                }
                prevIBeaconData = iBeaconData;
            }

            Location location = MyGPSManager.getLocation();
            if(location != null){
                if(!location.equals(prevLocation)){
                    prevLocation = location;

                    if(prevLocation != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mTvAcc != null) mTvAcc.setText(String.valueOf(prevLocation.getAccuracy()));
                                if(prevLocation.getAccuracy() < MIN_ACCURACY){
                                    if(dmDrawView != null){
                                        double lati = prevLocation.getLatitude(), longi = prevLocation.getLongitude();
                                        double x = longi*s + lati*u;
                                        double y = longi*t + lati*v;

                                        dmDrawView.setPos((float)x, (float)y);
                                        dmDrawView.invalidate();
                                        Log.d(TAG, "draw "+ x +", " + y);

                                        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
                                        int width = dm.widthPixels;
                                        int height = dm.heightPixels;

                                        if(!isCalculation && !MyBluetoothManager.isPowerOn()){
                                            recommendClub(calculateDistance(x/width, y/height));
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }


        }
    }

    private void recommendClub(double meter){
        if(mTvRssi != null) mTvRssi.setText(
                String.valueOf((int)meter)+"."+(((int)(meter*10))%10)+"m");

        ArrayList<ClubData> list = MyDBManager.getClubList();
        if(list != null){
            int i;
            for(i=list.size()-1; i>=0; i--){
                ClubData data = list.get(i);
                if(meter < data.getMeter()){
                    if(mTvClub != null)
                        mTvClub.setText(data.getName());
                    break;
                }
            }
            if(i==-1){
                mTvClub.setText((list.get(0)).getName());
            }
        }
    }

    private double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    private double calculateDistance(double x, double y){

        double z = Math.abs(DrawView.BLUE_X - DrawView.RED_X);
        double v = Math.abs(DrawView.BLUE_X - x);

        return mDistance / z * v;
    }


    private void showTacTic(){
        if(mTacTicLayout != null){
            if(mPopupWindow == null || !mPopupWindow.isShowing()){
                if(mPopupWindow != null) mPopupWindow.dismiss();
                mPopupWindow = new PopupWindow(mTacTicLayout, (int)(mWidthPixels*0.9), (int)(mHeightPixels*0.5), true);
                mPopupWindow.showAtLocation(mTacTicLayout, Gravity.CENTER, 0, 0);

                mTacTicLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mPopupWindow != null) {
                            mPopupWindow.dismiss();
                            mPopupWindow = null;
                        }
                    }
                });
                if(mGroundData != null) {
                    TextView textView = (TextView) mTacTicLayout.findViewById(R.id.tv_ptac_tactic);
                    textView.setText(mGroundData.getTactic());
                }
            }
        }
    }

    private void showScore(){
        if(mTacTicLayout != null){
            if(mPopupWindow == null || !mPopupWindow.isShowing()){
                if(mPopupWindow != null) mPopupWindow.dismiss();
                mPopupWindow = new PopupWindow(mScoreLayout, (int)(mWidthPixels*0.9), (int)(mHeightPixels*0.5), true);
                mPopupWindow.showAtLocation(mScoreLayout, Gravity.CENTER, 0, 0);

                mScoreLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mPopupWindow != null) mPopupWindow.dismiss();
                        mPopupWindow.showAtLocation(mScoreLayout, Gravity.CENTER, 0, 0);
                    }
                });


                final RadioGroup topGroup = (RadioGroup)mScoreLayout.findViewById(R.id.rg_psco_top);
                final RadioGroup bottomGroup = (RadioGroup)mScoreLayout.findViewById(R.id.rg_psco_bottom);

                topGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    private int prevId;
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(prevId == checkedId) return;
                        prevId = checkedId;
                        Log.d(TAG, "top checkedId = "+checkedId);
                        if(checkedId != -1){
                            bottomGroup.check(-1);
                        }
                    }
                });
                bottomGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    private int prevId;
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(prevId == checkedId) return;
                        prevId = checkedId;
                        Log.d(TAG, "bottom checkedId = "+checkedId);
                        if(checkedId != -1){
                            topGroup.check(-1);
                        }
                    }
                });

                final Button button = (Button)mScoreLayout.findViewById(R.id.btn_psco_input);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            int id = topGroup.getCheckedRadioButtonId();
                            if(id == -1)
                                id = bottomGroup.getCheckedRadioButtonId();
                            if(id == -1) throw new Exception();

                            int input = 0;
                            switch(id){
                                case R.id.rb_psco_1:
                                    input = 1;
                                    break;
                                case R.id.rb_psco_2:
                                    input = 2;
                                    break;
                                case R.id.rb_psco_3:
                                    input = 3;
                                    break;
                                case R.id.rb_psco_4:
                                    input = 4;
                                    break;
                                case R.id.rb_psco_5:
                                    input = 5;
                                    break;
                                case R.id.rb_psco_6:
                                    input = 6;
                                    break;
                                case R.id.rb_psco_7:
                                    input = 7;
                                    break;
                                case R.id.rb_psco_8:
                                    input = 8;
                                    break;
                                case R.id.rb_psco_9:
                                    input = 9;
                                    break;
                                case R.id.rb_psco_10:
                                    input = 10;
                                    break;
                            }

                            if(input != 0){
                                MyDBManager.setScore(input);
                                if(mPopupWindow != null) {
                                    mPopupWindow.dismiss();
                                    mPopupWindow = null;
                                }
                            }
                        }catch (Exception ignore){
                        }
                    }
                });
            }
        }
    }
}
