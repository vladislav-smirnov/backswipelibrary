package com.airdaydreamers.samplebackswipelibrary.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.fragment.BackSwipeFragment;
import com.airdaydreamers.samplebackswipelibrary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends BackSwipeFragment {


    public FirstFragment() {
        // Required empty public constructor
    }

    public static FirstFragment newInstance()
    {
        Bundle args = new Bundle();

        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first, container, false);

        setFragmentBackground(R.color.colorPrimary);
        setEdgeOrientation(BackSwipeHelper.EdgeOrientation.LEFT);

        view.findViewById(R.id.tv_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddFragmentListener != null) {
                    mAddFragmentListener.onFragmentAdded(FirstFragment.this, SecondFragment.newInstance());
                }
            }
        });

       // return attachToSwipeBack(view, SwipeBackLayout.EdgeSizeLevel.MED);
        return attachToBackSwipe(view, BackSwipeHelper.EdgeSizeLevel.MIN);
    }

}
