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

import com.practice.myapplication.R;
import com.practice.myapplication.data.StoreData;
import com.practice.myapplication.manager.MyDBManager;

import java.util.ArrayList;

/**
 * Created by hagtfms on 2016-04-30.
 */
public class StoreFragment extends Fragment {
    private ListView mListView;
    private CustomAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_store, container, false);

        /**
         * reference : http://kd3302.tistory.com/85
         */
        mAdapter = new CustomAdapter(MyDBManager.getStoreList());
        mListView = (ListView) v.findViewById(R.id.lv_fstore_list);
        mListView.setAdapter(mAdapter);

        return v;
    }

    /**
     * reference : http://berabue.blogspot.kr/2014/05/android-listview.html
     */
    private class CustomAdapter extends BaseAdapter {
        private ArrayList<StoreData> mList;

        private CustomAdapter(){  mList = new ArrayList<StoreData>();  }
        private CustomAdapter(ArrayList<StoreData> list){
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

            TextView textView = null;
            CustomHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_store, parent, false);

                textView = (TextView)convertView.findViewById(R.id.tv_listore_text);

                holder = new CustomHolder();
                holder.mTextView = textView;
                convertView.setTag(holder);
            }
            else {
                holder = (CustomHolder) convertView.getTag();
                textView = holder.mTextView;
            }

            final StoreData storeData = mList.get(pos);
            textView.setText(storeData.getName());

            return convertView;
        }

        private class CustomHolder{
            TextView mTextView;
        }

        private void setList(ArrayList<StoreData> list){
            mList = list;
        }

        private void refresh(){
            this.notifyDataSetChanged();
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
