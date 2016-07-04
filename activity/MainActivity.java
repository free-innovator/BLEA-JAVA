package com.practice.myapplication.activity;

import android.app.ActionBar;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.fragment.ClubFragment;
import com.practice.myapplication.fragment.MainFragment;
import com.practice.myapplication.fragment.ScoreCardFragment;
import com.practice.myapplication.fragment.SettingFragment;
import com.practice.myapplication.fragment.StoreFragment;
import com.practice.myapplication.manager.MyBluetoothManager;

/**
 * Created by qkrtp_000 on 2016-04-25.
 */
public class MainActivity extends FragmentActivity {
    private final String TAG = "MainActivity";
    private interface FRAG_NUM { int MAIN=1, SCORECARD=2, STORE=3, CLUB=4, SETTING=5; };

    private MainFragment mMainFragment;
    private ScoreCardFragment mScoreCardFragment;
    private StoreFragment mStoreFragment;
    private ClubFragment mClubFragment;
    private SettingFragment mSettingFragment;

    private TextView mTitleTextView;
    private FrameLayout mFrameLayout1;
    private FrameLayout mFrameLayout2;
    private FrameLayout mFrameLayout3;
    private FrameLayout mFrameLayout4;
    private FrameLayout mFrameLayout5;

    private int mCurrentFragmentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);

            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.actionbar_main, null);
            mTitleTextView = (TextView)v.findViewById(R.id.tv_abm_main);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(v);
        }
        setContentView(R.layout.activity_main);

        mMainFragment = new MainFragment();
        mScoreCardFragment = new ScoreCardFragment();
        mStoreFragment = new StoreFragment();
        mClubFragment = new ClubFragment();
        mSettingFragment = new SettingFragment();

        if(findViewById(R.id.ll_am_main) != null){
            if(savedInstanceState != null){
                return;
            }

            mFrameLayout1 = (FrameLayout)findViewById(R.id.fl_am_btn1);
            mFrameLayout2 = (FrameLayout)findViewById(R.id.fl_am_btn2);
            mFrameLayout3 = (FrameLayout)findViewById(R.id.fl_am_btn3);
            mFrameLayout4 = (FrameLayout)findViewById(R.id.fl_am_btn4);
            mFrameLayout5 = (FrameLayout)findViewById(R.id.fl_am_btn5);

            View.OnTouchListener onTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();

                    switch(v.getId()){
                        case R.id.fl_am_btn1:
                        case R.id.fl_am_btn2:
                        case R.id.fl_am_btn3:
                        case R.id.fl_am_btn4:
                        case R.id.fl_am_btn5:
                            switch (action){
                                case MotionEvent.ACTION_DOWN:
                                    //v.setBackgroundColor(Color.rgb(0, 0, 0));
                                    switch(v.getId()){
                                        case R.id.fl_am_btn1:
                                            fragmentReplace(FRAG_NUM.MAIN);
                                            break;
                                        case R.id.fl_am_btn2:
                                            fragmentReplace(FRAG_NUM.SCORECARD);
                                            break;
                                        case R.id.fl_am_btn3:
                                            fragmentReplace(FRAG_NUM.STORE);
                                            break;
                                        case R.id.fl_am_btn4:
                                            fragmentReplace(FRAG_NUM.CLUB);
                                            break;
                                        case R.id.fl_am_btn5:
                                            fragmentReplace(FRAG_NUM.SETTING);
                                            break;
                                    }
                                    return true;
                                case MotionEvent.ACTION_UP:
                                    //v.setBackgroundColor(Color.rgb(237, 237, 237));
                                    return true;
                            }
                    }
                    return false;
                }
            };
            mFrameLayout1.setOnTouchListener(onTouchListener);
            mFrameLayout2.setOnTouchListener(onTouchListener);
            mFrameLayout3.setOnTouchListener(onTouchListener);
            mFrameLayout4.setOnTouchListener(onTouchListener);
            mFrameLayout5.setOnTouchListener(onTouchListener);

            fragmentReplace(FRAG_NUM.MAIN);
        }
    }

    /**
     * reference
     * http://muzesong.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-Fragment-%EC%89%BD%EA%B2%8C-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
     */
    private boolean fragmentReplace(int reqNewFragmentIndex){
        Log.d(TAG, "fragmentReplace : " + reqNewFragmentIndex);

        Fragment newFragment = getFragment(reqNewFragmentIndex);
        if(newFragment != null && reqNewFragmentIndex != mCurrentFragmentIndex){
            final FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.ll_am_main, newFragment);
            transaction.commit();

            switch(reqNewFragmentIndex){
                case FRAG_NUM.MAIN:
                    //mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() &~ Paint.FAKE_BOLD_TEXT_FLAG);
                    mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
                    mTitleTextView.setText("메인");
                    break;
                case FRAG_NUM.SCORECARD:
                    mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
                    mTitleTextView.setText("스코어카드");
                    break;
                case FRAG_NUM.STORE:
                    mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
                    mTitleTextView.setText("매장리스트");
                    break;
                case FRAG_NUM.CLUB:
                    mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
                    mTitleTextView.setText("클럽");
                    break;
                case FRAG_NUM.SETTING:
                    mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
                    mTitleTextView.setText("설정");
                    break;
                default:
                    Log.d(TAG, "Unhandle case");
                    break;
            }
            mCurrentFragmentIndex = reqNewFragmentIndex;
            return true;
        }
        else
            return false;
    }
    private Fragment getFragment(int idx){
        Fragment newFragment = null;

        switch(idx){
            case FRAG_NUM.MAIN:
                newFragment = mMainFragment;
                break;
            case FRAG_NUM.SCORECARD:
                newFragment = mScoreCardFragment;
                break;
            case FRAG_NUM.STORE:
                newFragment = mStoreFragment;
                break;
            case FRAG_NUM.CLUB:
                newFragment = mClubFragment;
                break;
            case FRAG_NUM.SETTING:
                newFragment = mSettingFragment;
                break;
            default:
                Log.d(TAG, "Unhandle case");
                break;
        }

        return newFragment;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
    }
    @Override
    public void finish(){
        MyBluetoothManager.stopScanForIBeacon();
        super.finish();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
