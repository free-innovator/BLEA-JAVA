package com.practice.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.practice.myapplication.R;
import com.practice.myapplication.data.ClubData;
import com.practice.myapplication.manager.MyDBManager;

import java.util.ArrayList;

/**
 * Created by hagtfms on 2016-04-26.
 */
public class ClubFragment extends Fragment {
    private ListView mListView;
    private CustomAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_club, container, false);

        /**
         * reference : http://kd3302.tistory.com/85
         */
        mAdapter = new CustomAdapter(MyDBManager.getClubList(getActivity()));
        mListView = (ListView) v.findViewById(R.id.lv_fclub_list);
        mListView.setAdapter(mAdapter);

        return v;
    }

    /**
     * reference : http://berabue.blogspot.kr/2014/05/android-listview.html
     */
    private class CustomAdapter extends BaseAdapter {
        private ArrayList<ClubData> mList;

        private CustomAdapter(){  mList = new ArrayList<ClubData>();  }
        private CustomAdapter(ArrayList<ClubData> list){
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

            TextView leftTextView = null;
            TextView rightTextView = null;
            CustomHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_club, parent, false);

                leftTextView = (TextView)convertView.findViewById(R.id.tv_lic_left);
                rightTextView = (TextView)convertView.findViewById(R.id.tv_lic_right);

                holder = new CustomHolder();
                holder.mLeftTextView = leftTextView;
                holder.mRightTextView = rightTextView;
                convertView.setTag(holder);
            }
            else {
                holder = (CustomHolder) convertView.getTag();
                leftTextView = holder.mLeftTextView;
                rightTextView = holder.mRightTextView;
            }

            ClubData clubData = mList.get(pos);
            leftTextView.setText(clubData.getName());
            rightTextView.setText(String.valueOf(clubData.getMeter()));

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

        private class CustomHolder{
            TextView mLeftTextView;
            TextView mRightTextView;
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