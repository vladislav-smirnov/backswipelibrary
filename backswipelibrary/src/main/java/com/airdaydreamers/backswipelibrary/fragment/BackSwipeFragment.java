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
package com.airdaydreamers.backswipelibrary.fragment;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.listeners.OnFragmentChangeListener;
import com.airdaydreamers.backswipelibrary.R;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.Objects;

/**
 * Created by Vladislav Smirnov on 4/24/2018.
 */
public class BackSwipeFragment extends Fragment {

    private final String TAG = BackSwipeHelper.TAG + "-" + this.getClass().getSimpleName();

    protected OnFragmentChangeListener mAddFragmentListener;

    private static final String BACKSWIPEFRAGMENT_STATE_HIDDEN = "BACKSWIPEFRAGMENT_STATE_HIDDEN";
    private BackSwipeLayout mBackSwipeLayout;
    private Animation mNoAnim;
    boolean mLocking = false;

    private int mFragmentBackground = 0;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mActivity = activity;//don't do this
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChangeListener) {
            mAddFragmentListener = (OnFragmentChangeListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(BACKSWIPEFRAGMENT_STATE_HIDDEN);

            if (getFragmentManager() != null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (isSupportHidden) {
                    ft.hide(this);
                } else {
                    ft.show(this);
                }
                ft.commit();
            }
        }

        mNoAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.no_anim);
        onFragmentCreate();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BACKSWIPEFRAGMENT_STATE_HIDDEN, isHidden());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddFragmentListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentActivity activity = getActivity();
        if (activity != null && activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
            activity.finish();
            activity.overridePendingTransition(0, 0);
        }
    }

    private void onFragmentCreate() {
        mBackSwipeLayout = new BackSwipeLayout(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBackSwipeLayout.setLayoutParams(params);
        mBackSwipeLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    protected View attachToBackSwipe(View view) {
        mBackSwipeLayout.attachToFragment(this, view);
        return mBackSwipeLayout;
    }

    protected View attachToBackSwipe(View view, BackSwipeHelper.EdgeSizeLevel edgeSizeLevel) {
        mBackSwipeLayout.attachToFragment(this, view);
        mBackSwipeLayout.setEdgeSizeLevel(edgeSizeLevel);
        return mBackSwipeLayout;
    }

//    protected void setEdgeLevel(BackSwipeHelper.EdgeSizeLevel edgeSizeLevel) {
//        mBackSwipeLayout.setEdgeSizeLevel(edgeSizeLevel);
//    }

//    protected void setEdgeLevel(int widthPixel) {
//        mBackSwipeLayout.setEdgeLevel(widthPixel);
//    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden && mBackSwipeLayout != null) {
            mBackSwipeLayout.hiddenFragment();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        initFragmentBackground(view);
        if (view != null) {
            view.setClickable(true);
        }
    }

    private void initFragmentBackground(View view) {
        if (view instanceof BackSwipeLayout) {
            View childView = ((BackSwipeLayout) view).getChildAt(0);
            setBackground(childView);
        } else {
            setBackground(view);
        }
    }

    private void setBackground(View view) {
        if (view != null && view.getBackground() == null) {
            int defaultBg = getFragmentBackground();
//            if (getActivity() instanceof FragmentActivity) { //TODO: change it
//                defaultBg = getFragmentBackground();
//            }
            if (defaultBg == 0) {
                Log.w(TAG, "Fragment doesn't have background. Please set it in XML");
                try {
                    Drawable d = getBackgroundParentView(view);
                    view.setBackground(d);
                } catch (MalformedParameterizedTypeException ex) {
                    Log.i(TAG, "Fragment doesn't have parent view");
                    int background = getWindowBackground();
                    view.setBackgroundResource(background);
                }
            } else {
                view.setBackgroundResource(defaultBg);
            }
        }
    }

    protected void setFragmentBackground(@DrawableRes int backgroundRes) {
        mFragmentBackground = backgroundRes;
    }

    protected int getFragmentBackground() {
        return mFragmentBackground;
    }

    private Drawable getBackgroundParentView(View v) throws MalformedParameterizedTypeException {
        if (!(v.getParent() instanceof ViewGroup)) {
            throw new MalformedParameterizedTypeException();
        }
        ViewGroup parent = (ViewGroup) v.getParent();
        return parent.getBackground();
    }

    private int getWindowBackground() {
        TypedArray a = Objects.requireNonNull(getActivity()).getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();
        return background;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (mLocking) {
            return mNoAnim;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    public BackSwipeLayout getBackSwipeLayout() {
        return mBackSwipeLayout;
    }

    public void setEnableBackSwipeGesture(boolean enable) {
        mBackSwipeLayout.setEnableBackSwipeGesture(enable);
    }

    public boolean getEnableBackSwipeGesture() {
        return mBackSwipeLayout.getEnableBackSwipeGesture();
    }

    public void setEdgeOrientation(BackSwipeHelper.EdgeOrientation edgeOrientation) {
        mBackSwipeLayout.setEdgeOrientation(edgeOrientation);
    }

    public BackSwipeHelper.EdgeOrientation getEdgeOrientation() {
        return mBackSwipeLayout.getEdgeOrientation();
    }

    public void setEdgeSizeLevel(BackSwipeHelper.EdgeSizeLevel edgeSizeLevel) {
        mBackSwipeLayout.setEdgeSizeLevel(edgeSizeLevel);
    }

    public void setTouchSlopThreshold(float threshold) {
        mBackSwipeLayout.setTouchSlopThreshold(threshold);
    }

    public float getTouchSlopThreshold() {
        return mBackSwipeLayout.getTouchSlopThreshold();
    }
}
