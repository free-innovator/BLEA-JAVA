package com.practice.myapplication.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.data.ScoreData;
import com.practice.myapplication.manager.MyDBManager;

/**
 * Created by hagtfms on 2016-04-26.
 */
public class ScoreCardFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_scorecard, container, false);

        TextView textView = (TextView)v.findViewById(R.id.tv_fscore_text);
        ScoreData scoreData = MyDBManager.getScore(4);

        if(scoreData != null){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=1; i<=16; i++){
                stringBuilder.append(i+"홀 : ");
                stringBuilder.append(scoreData.getScore(i));
                if(i != 16) stringBuilder.append('\n');
            }
            textView.setText(stringBuilder.toString());
        }

        return v;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
