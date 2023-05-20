package io.github.airdaydreamers.samplebackswipelibrary.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.airdaydreamers.backswipelibrary.BackSwipeState;
import io.github.airdaydreamers.backswipelibrary.EdgeSizeLevel;
import io.github.airdaydreamers.backswipelibrary.fragment.BackSwipeFragment;
import com.airdaydreamers.samplebackswipelibrary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends BackSwipeFragment {


    public FirstFragment() {
        // Required empty public constructor
    }

    public static FirstFragment newInstance() {
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
        setEdgeOrientation(BackSwipeState.EdgeOrientation.LEFT);

        view.findViewById(R.id.tv_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddFragmentListener != null) {
                    mAddFragmentListener.onFragmentAdded(FirstFragment.this, SecondFragment.newInstance());
                }
            }
        });

        // return attachToSwipeBack(view, SwipeBackLayout.EdgeSizeLevel.MED);
        return attachToBackSwipe(view,  new EdgeSizeLevel.MIN());
    }

}
