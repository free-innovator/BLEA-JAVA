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
        ScoreData scoreData = MyDBManager.getScoreData(4);

        if(scoreData != null){
            int score = 0;
            TextView[] textViewList = new TextView[19];

            textViewList[1] = (TextView)v.findViewById(R.id.tv_fscore_1);
            textViewList[2] = (TextView)v.findViewById(R.id.tv_fscore_2);
            textViewList[3] = (TextView)v.findViewById(R.id.tv_fscore_3);
            textViewList[4] = (TextView)v.findViewById(R.id.tv_fscore_4);
            textViewList[5] = (TextView)v.findViewById(R.id.tv_fscore_5);
            textViewList[6] = (TextView)v.findViewById(R.id.tv_fscore_6);
            textViewList[7] = (TextView)v.findViewById(R.id.tv_fscore_7);
            textViewList[8] = (TextView)v.findViewById(R.id.tv_fscore_8);
            textViewList[9] = (TextView)v.findViewById(R.id.tv_fscore_9);
            textViewList[10] = (TextView)v.findViewById(R.id.tv_fscore_10);
            textViewList[11] = (TextView)v.findViewById(R.id.tv_fscore_11);
            textViewList[12] = (TextView)v.findViewById(R.id.tv_fscore_12);
            textViewList[13] = (TextView)v.findViewById(R.id.tv_fscore_13);
            textViewList[14] = (TextView)v.findViewById(R.id.tv_fscore_14);
            textViewList[15] = (TextView)v.findViewById(R.id.tv_fscore_15);
            textViewList[16] = (TextView)v.findViewById(R.id.tv_fscore_16);
            textViewList[17] = (TextView)v.findViewById(R.id.tv_fscore_17);
            textViewList[18] = (TextView)v.findViewById(R.id.tv_fscore_18);

            for(int i=1; i<=18; i++){
                score = scoreData.getScore(i);
                if(score != scoreData.NON_DATA)
                    textViewList[i].setText(String.valueOf(score));
            }

            /*
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=1; i<=18; i++){
                stringBuilder.append(i+"홀 : ");
                stringBuilder.append(scoreData.getScore(i));
                if(i != 18) stringBuilder.append('\n');
            }
            textView.setText(stringBuilder.toString());*/
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
