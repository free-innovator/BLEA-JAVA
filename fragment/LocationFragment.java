package com.practice.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.practice.myapplication.R;

import java.util.ArrayList;

/**
 * Created by hagtfms on 2016-04-24.
 */
public class LocationFragment extends Fragment {
    private ListView mListView;
    private CustomAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_location, container, false);

        /**
         * reference : http://kd3302.tistory.com/85
         */
        mAdapter = new CustomAdapter();
        mListView = (ListView) v.findViewById(R.id.lv_course_state);
        mListView.setAdapter(mAdapter);

        mAdapter.add("aaa");
        mAdapter.add("bbb");
        mAdapter.add("ccc");
        mAdapter.add("ddd");
        mAdapter.add("eee");
        mAdapter.add("fff");
        mAdapter.add("ggg");
        mAdapter.add("hhh");
        mAdapter.add("iii");
        mAdapter.add("jjj");

        return v;
    }

    /**
     * reference : http://berabue.blogspot.kr/2014/05/android-listview.html
     */
    private class CustomAdapter extends BaseAdapter {
        private ArrayList<String> mList;

        private CustomAdapter(){
            mList = new ArrayList<String>();
        }
        private CustomAdapter(ArrayList<String> list){
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            ToggleButton leftButton = null;
            ToggleButton centerButton = null;
            ToggleButton rightButton = null;
            CustomHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_location, parent, false);

                leftButton = (ToggleButton) convertView.findViewById(R.id.tgbtn_lil_left);
                centerButton = (ToggleButton) convertView.findViewById(R.id.tgbtn_lil_center);
                rightButton = (ToggleButton) convertView.findViewById(R.id.tgbtn_lil_right);

                holder = new CustomHolder();
                holder.mLeftButton = leftButton;
                holder.mCenterButton = centerButton;
                holder.mRightButton = rightButton;
                convertView.setTag(holder);
            }
            else {
                holder = (CustomHolder) convertView.getTag();
                leftButton = holder.mLeftButton;
                centerButton = holder.mCenterButton;
                rightButton = holder.mRightButton;
            }

            leftButton.setEnabled(false);
            centerButton.setEnabled(false);
            rightButton.setEnabled(false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "리스트 클릭 : " + mList.get(pos), Toast.LENGTH_SHORT).show();
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(context, "리스트 롱 클릭 : " + mList.get(pos), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            return convertView;
        }

        public void add(String _msg){
            mList.add(_msg);
        }
        public void remove(int _position){
            mList.remove(_position);
        }

        private class CustomHolder{
            ToggleButton mLeftButton;
            ToggleButton mCenterButton;
            ToggleButton mRightButton;
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