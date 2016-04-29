package com.practice.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.practice.myapplication.R;
import com.practice.myapplication.data.ClubData;
import com.practice.myapplication.manager.MyDBManager;

import java.util.ArrayList;

/**
 * Created by hagtfms on 2016-04-26.
 */
public class SettingFragment extends Fragment {
    private CustomAdapter mAdapter1;
    private CustomAdapter mAdapter2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        final Spinner spinner1 = (Spinner)v.findViewById(R.id.sp_fs_spinner1);
        final Spinner spinner2 = (Spinner)v.findViewById(R.id.sp_fs_spinner2);

        mAdapter1 = new CustomAdapter(getResources().getStringArray(R.array.meter_yard));
        mAdapter2 = new CustomAdapter(getResources().getStringArray(R.array.db_save_location));
        spinner1.setAdapter(mAdapter1);
        spinner2.setAdapter(mAdapter2);

        /*
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner1.setPrompt((String)spinner1.getAdapter().getItem(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner2.setPrompt((String)spinner2.getAdapter().getItem(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        */

        return v;
    }

    /**
     * reference : http://berabue.blogspot.kr/2014/05/android-listview.html
     */
    private class CustomAdapter extends BaseAdapter {
        private String[] mList;
        private CustomAdapter(String[] list){
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.length;
        }

        @Override
        public Object getItem(int position) {  return mList[position]; }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            TextView textView = null;
            CustomHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_item_setting, parent, false);

                textView = (TextView)convertView.findViewById(R.id.tv_sis_text);

                holder = new CustomHolder();
                holder.mTextView = textView;
                convertView.setTag(holder);
            }
            else {
                holder = (CustomHolder) convertView.getTag();
                textView = holder.mTextView;
            }

            textView.setText(mList[position]);
            return convertView;
        }

        private class CustomHolder{
            TextView mTextView;
        }
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
