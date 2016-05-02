package com.practice.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.manager.MyDBManager;

/**
 * Created by hagtfms on 2016-04-30.
 */
public class TacticActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tactic);

        TextView textView = (TextView)findViewById(R.id.tv_atactic_text);
        textView.setText(MyDBManager.getTactic(1));
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
