package com.airdaydreamers.samplebackswipelibrary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.fragment.BackSwipeFragment;
import com.airdaydreamers.samplebackswipelibrary.R;

public class SecondFragment extends BackSwipeFragment {


    public SecondFragment() {
        // Required empty public constructor
    }

    public static SecondFragment newInstance() {

        Bundle args = new Bundle();

        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_second, container, false);
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        return attachToBackSwipe(view, BackSwipeHelper.EdgeSizeLevel.MAX);
    }

}
