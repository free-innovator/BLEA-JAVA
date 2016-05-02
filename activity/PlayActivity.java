package com.practice.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.practice.myapplication.R;

/**
 * Created by hagtfms on 2016-04-30.
 */
public class PlayActivity extends Activity {
    private Button mTacticButton;
    private Button mStageButton;

    private double mTcupLaditude, mTcupLongitude;
    private double mHcupLaditude, mHcupLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        final SharedPreferences prefs =
                getSharedPreferences(TestActivity.STR_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if(prefs != null){
            mTcupLaditude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LADITUDE, 0.0f);
            mTcupLongitude = prefs.getFloat(TestActivity.STR_TCUP_LOC_LONGITUDE, 0.0f);
            mHcupLaditude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LADITUDE, 0.0f);
            mHcupLongitude = prefs.getFloat(TestActivity.STR_HCUP_LOC_LONGITUDE, 0.0f);
        }

        mTacticButton = (Button)findViewById(R.id.btn_aplay_tactic);
        mStageButton = (Button)findViewById(R.id.btn_aplay_stage);

        mTacticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, TacticActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        mStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlayActivity.this, StageActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
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
        super.finish();
    }
}
