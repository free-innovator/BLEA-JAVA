package com.practice.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.practice.myapplication.R;

/**
 * Created by hagtfms on 2016-04-26.
 */
public class PlayActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        /**
         * http://ilililililililililili.blogspot.kr/2013/07/android-gps_18.html
         */
        Button latitudeButton = (Button)findViewById(R.id.btn_aplay_latitude);
        Button longitudeButton = (Button)findViewById(R.id.btn_aplay_longitude);
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
