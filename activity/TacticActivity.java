package com.practice.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;

import com.practice.myapplication.R;

/**
 * Created by hagtfms on 2016-04-30.
 */
public class TacticActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tactic);
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
