/*
 * Copyright Notice:
 *
 * Copyright (C) Vladislav Smirnov, 2018.
 * All Rights Reserved.
 *
 * The reproduction, transmission or use of this document or its contents is
 * not permitted without express written authority.
 * Offenders will be liable for damages. All rights, including rights created
 * by patent grant or registration of a utility model or design, are reserved.
 *
 */
package com.airdaydreamers.backswipelibrary.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.listeners.OnActivityChangeListener;

/**
 * Created by Vladislav Smirnov on 4/24/2018.
 */
public class BackSwipeActivity extends AppCompatActivity implements OnActivityChangeListener {

    private BackSwipeViewGroup mBackSwipeViewGroup;
    private ImageView mImageViewShadow;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        mBackSwipeViewGroup.addView(view);
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        mBackSwipeViewGroup = new BackSwipeViewGroup(this);
        mBackSwipeViewGroup.setEdgeOrientation(BackSwipeHelper.EdgeOrientation.LEFT);
        mBackSwipeViewGroup.setOnSwipeBackListener(this);

        mImageViewShadow = new ImageView(this);
        mImageViewShadow.setBackgroundColor(Color.BLACK);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        container.addView(mImageViewShadow, params);
        container.addView(mBackSwipeViewGroup);
        return container;
    }

    public void setEnableSwipe(boolean enableSwipe) {
        mBackSwipeViewGroup.setEnabledBackSwipeGestures(enableSwipe);
    }

    public void setEdgeOrientation(BackSwipeHelper.EdgeOrientation edgeOrientation) {
        mBackSwipeViewGroup.setEdgeOrientation(edgeOrientation);
    }


    public void setEdgeSizeLevel(BackSwipeHelper.EdgeSizeLevel level) {
        mBackSwipeViewGroup.setEdgeSizeLevel(level);
    }

    public void setTouchSlopThreshold(float touchSlopThreshold) {
        mBackSwipeViewGroup.setTouchSlopThreshold(touchSlopThreshold);
    }

    public void setScrollChildView(View view)
    {
        mBackSwipeViewGroup.setScrollChildView(view);
    }
    public void setTouchSlopThreshold(int threshold){
        mBackSwipeViewGroup.setTouchSlopThreshold(threshold);
    }
    public float getTouchSlopThreshold(){
        return mBackSwipeViewGroup.getTouchSlopThreshold();
    }

    public BackSwipeViewGroup getBackSwipeViewGroup() {
        return mBackSwipeViewGroup;
    }

    @Override
    public void onViewPositionChanged(float groupThreshold, float groupScreen) {
        mImageViewShadow.setAlpha(1 - groupScreen);
    }

}
