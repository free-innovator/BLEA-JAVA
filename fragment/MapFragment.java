package com.practice.myapplication.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.myapplication.R;

/**
 * Created by hagtfms on 2016-04-25.
 */
public class MapFragment extends Fragment {
    public static String ARG_POSITION = "abc";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_map, container, false);
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
