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

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.customview.widget.ViewDragHelper;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.listeners.OnActivityChangeListener;
import com.airdaydreamers.backswipelibrary.listeners.OnBackSwipeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.airdaydreamers.backswipelibrary.BackSwipeHelper.*;

/*
  Created by Vladislav Smirnov on 5/4/2018.
  sivdead@gmail.com
 */
public class BackSwipeViewGroup extends ViewGroup {
    private final String TAG = BackSwipeHelper.TAG + "-" + this.getClass().getSimpleName();

    private Context mContext;

    protected EdgeOrientation mEdgeOrientation = EdgeOrientation.LEFT;

    protected boolean mEnabledBackSwipeGestures = true;

    protected double mAutoFinishedVelocityThreshold = 1500.0;
    //private static final float BACK_FACTOR = 0.5f;

    protected ViewDragHelper mViewDragHelper;

    private View mTargetView;
    private View mScrollChildView;

    protected int mHorizontalDragRange = 1;
    protected int mDraggingState = 0;
    protected int mDraggingOffset;

    /**
     * The threshold of calling finish Activity.
     */
    protected float mTouchSlopThreshold = 0;
    protected float mTouchSlopThresholdInPercent = 0;
    protected float mDraggingOffsetInPercent;

    protected boolean isPercentEnabled = false;
    /**
     * The set of listeners to be sent events through.
     */
    protected List<OnBackSwipeListener> mListeners;
    protected OnActivityChangeListener mOnActivityChangeListener;


    public BackSwipeViewGroup(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public BackSwipeViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
    }

    public void setAutoFinishedVelocityThreshold(double velocity) {
        mAutoFinishedVelocityThreshold = velocity;
    }

    public double getAutoFinishedVelocityThreshold() {
        return mAutoFinishedVelocityThreshold;
    }

    public void setEdgeOrientation(EdgeOrientation edgeOrientation) {
        this.mEdgeOrientation = edgeOrientation;

        mViewDragHelper.setEdgeTrackingEnabled(edgeOrientation.getValue());
    }

    public EdgeOrientation getEdgeOrientation() {
        return mEdgeOrientation;
    }

    public void setEdgeSizeLevel(EdgeSizeLevel edgeSizeLevel) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) return;
            windowManager.getDefaultDisplay().getMetrics(metrics);

            Field mEdgeSize = mViewDragHelper.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);

            if (edgeSizeLevel == EdgeSizeLevel.MAX)
                mEdgeSize.setInt(mViewDragHelper, metrics.widthPixels);
            else if (edgeSizeLevel == EdgeSizeLevel.MED)
                mEdgeSize.setInt(mViewDragHelper, metrics.widthPixels / 2);
            else if (edgeSizeLevel == EdgeSizeLevel.MIN)
                mEdgeSize.setInt(mViewDragHelper, ((int) (20 * metrics.density + 0.5f)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setEnabledBackSwipeGesture(boolean enable) {
        mEnabledBackSwipeGestures = enable;
    }

    public boolean getEnabledBackSwipeGesture() {
        return mEnabledBackSwipeGestures;
    }

    public void setScrollChildView(View view) {
        mScrollChildView = view;
    }

    /**
     * Set the threshold of calling finish.
     *
     * @param threshold
     */
    public void setTouchSlopThreshold(float threshold) {
        mTouchSlopThreshold = threshold;
    }

    public void setTouchSlopThreshold(int threshold) throws IllegalArgumentException {
        threshold = threshold / 100;
        if (threshold >= 1.0f || threshold <= 0) {
            throw new IllegalArgumentException("Threshold value should be between 0 and 1.0");
        }
        mTouchSlopThresholdInPercent = threshold;
    }

    public float getTouchSlopThreshold() {
        return mTouchSlopThresholdInPercent;
    }


    public void setOnSwipeBackListener(OnActivityChangeListener listener) {
        mOnActivityChangeListener = listener;
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener the swipe listener to attach to this view
     */
    public void addSwipeListener(OnBackSwipeListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }

    /**
     * Removes a listener from the set of listeners
     *
     * @param listener
     */
    public void removeSwipeListener(OnBackSwipeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }

    protected void ensureTarget() {
        if (mTargetView == null) {
            if (getChildCount() > 1) {
                IllegalStateException ex = new IllegalStateException("BackSwipeView must contains only one direct child");
                Log.e(TAG, ex.getMessage());
                throw ex;
            }
            mTargetView = getChildAt(0);

            if (mScrollChildView == null && mTargetView != null) {
                if (mTargetView instanceof ViewGroup) findScrollView((ViewGroup) mTargetView);
                else mScrollChildView = mTargetView;
            }
        }
    }

    /**
     * Find out the scrollable child view from a ViewGroup.
     *
     * @param viewGroup
     */
    private void findScrollView(ViewGroup viewGroup) {
        mScrollChildView = viewGroup;
        if (viewGroup.getChildCount() > 0) {
            int count = viewGroup.getChildCount();
            View child;
            for (int i = 0; i < count; i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof AbsListView || child instanceof ScrollView || child instanceof ViewPager || child instanceof WebView) {
                    mScrollChildView = child;
                    return;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() == 0) return;

        View child = getChildAt(0);

        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childRight = childLeft + childWidth;
        int childBottom = childTop + childHeight;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 1) {
            throw new IllegalStateException("BackSwipeView must contains only one direct child.");
        }

        if (getChildCount() > 0) {
            int measureWidth = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
            int measureHeight = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
            getChildAt(0).measure(measureWidth, measureHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHorizontalDragRange = w;
        mTouchSlopThreshold = mTouchSlopThreshold > 0 ? mTouchSlopThreshold : mHorizontalDragRange * 0.5f;
        mTouchSlopThresholdInPercent = mTouchSlopThresholdInPercent > 0 ? mTouchSlopThresholdInPercent : 0.5f;

        if (mTouchSlopThresholdInPercent > 0)
            isPercentEnabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnabledBackSwipeGestures) return super.onInterceptTouchEvent(ev);
        boolean isHandled = false;
        ensureTarget();
        if (isEnabled()) isHandled = mViewDragHelper.shouldInterceptTouchEvent(ev);
        else mViewDragHelper.cancel();
        return !isHandled ? super.onInterceptTouchEvent(ev) : isHandled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnabledBackSwipeGestures) return super.onTouchEvent(event);
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected boolean canChildScrollRight() {
        return mScrollChildView.canScrollHorizontally(-1);
    }

    protected boolean canChildScrollLeft() {
        return mScrollChildView.canScrollHorizontally(1);
    }

    private void finish() {
        Activity act = (Activity) getContext();
        act.finish();
        act.overridePendingTransition(0, android.R.anim.fade_out);
    }

    protected boolean closeByVelocity(float xvel) {
        Log.d(TAG, "xvel == " + xvel);
        if (xvel > 0 && mEdgeOrientation == EdgeOrientation.LEFT && Math.abs(xvel) > mAutoFinishedVelocityThreshold) {
            return mEdgeOrientation == EdgeOrientation.LEFT ? !canChildScrollLeft() : !canChildScrollRight();

        } else if (xvel < 0 && mEdgeOrientation == EdgeOrientation.RIGHT && Math.abs(xvel) > mAutoFinishedVelocityThreshold) {
            return mEdgeOrientation == EdgeOrientation.RIGHT ? !canChildScrollLeft() : !canChildScrollRight();
        }
        return false;
    }

    protected void smoothSlideViewTo(int finalLeft, View view) {
        Log.d(TAG, "finalLeft == " + finalLeft + "width =" + getWidth());
        if (mViewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(view);
        }
    }

    public class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            boolean dragEnable = mViewDragHelper.isEdgeTouched(mEdgeOrientation.getValue(), pointerId);

            if (dragEnable) return child == mTargetView;
            else return dragEnable;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return mHorizontalDragRange;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal");
            int result = 0;

            if (mEdgeOrientation == EdgeOrientation.LEFT && !canChildScrollRight() && left > 0) {
                final int leftBound = getPaddingLeft();
                final int rightBound = mHorizontalDragRange;
                result = Math.min(Math.max(left, leftBound), rightBound);
            } else if (mEdgeOrientation == EdgeOrientation.RIGHT && !canChildScrollLeft() && left < 0) {
                final int leftBound = -mHorizontalDragRange;
                final int rightBound = getPaddingLeft();
                result = Math.min(Math.max(left, leftBound), rightBound);
            }

            return result;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState) return;

            if (((mDraggingState == STATE_DRAGGING) || (mDraggingState == STATE_SETTLING)) &&
                    (state == STATE_IDLE) && (mDraggingOffset == mHorizontalDragRange)) finish();

            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            mDraggingOffset = Math.abs(left);

            if (mEdgeOrientation == EdgeOrientation.LEFT) {
                mDraggingOffsetInPercent = Math.abs((float) left / getWidth());
            } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
                mDraggingOffsetInPercent = Math.abs((float) left / getWidth());
            }

            //The proportion of the sliding.
            float groupThreshold = (float) mDraggingOffset / (!isPercentEnabled ? mTouchSlopThreshold : (mTouchSlopThresholdInPercent * getWidth()));
            if (groupThreshold >= 1) groupThreshold = 1;

            float groupScreen = (float) mDraggingOffset / (float) mHorizontalDragRange;
            if (groupScreen >= 1) groupScreen = 1;

            if (mOnActivityChangeListener != null)
                mOnActivityChangeListener.onViewPositionChanged(groupThreshold, groupScreen);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if ((mDraggingOffset == 0) | (mDraggingOffset == mHorizontalDragRange)) return;

            boolean isBackSwipe = false;

            if (closeByVelocity(xvel)) {
                isBackSwipe = !(canChildScrollLeft() /*| canChildScrollRight()*/);
            } else if (isPercentEnabled)
                isBackSwipe = mDraggingOffsetInPercent >= mTouchSlopThresholdInPercent;
            else
                isBackSwipe = mDraggingOffset >= mTouchSlopThreshold;

            int finalLeft = 0;


            if (mEdgeOrientation == EdgeOrientation.LEFT) {
                finalLeft = isBackSwipe ? mHorizontalDragRange : 0;
            } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
                finalLeft = isBackSwipe ? -mHorizontalDragRange : 0;
            }
            smoothSlideViewTo(finalLeft, BackSwipeViewGroup.this);
        }
    }
}
