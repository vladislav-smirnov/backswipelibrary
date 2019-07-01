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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.listeners.OnBackSwipeListener;
import com.airdaydreamers.backswipelibrary.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.airdaydreamers.backswipelibrary.BackSwipeHelper.*;

/**
 * Created by Vladislav Smirnov on 4/24/2018.
 */
class BackSwipeLayout extends FrameLayout {
    private final String TAG = BackSwipeHelper.TAG + "-" + this.getClass().getSimpleName();

    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;
    private static final int FULL_ALPHA = 255;
    //private static final float DEFAULT_SCROLL_THRESHOLD = 0.1f;
    private static final int OVERSCROLL_DISTANCE = 10;

    /**
     * The mTouchSlopThresholdInPercent of calling finish Activity.
     */
    private float mTouchSlopThresholdInPercent = 0.1f;
    private double mAutoFinishedVelocityThreshold = 1500.0;

    private ViewDragHelper mViewDragHelper;

    private float mDraggingOffsetInPercent;
    private float mScrimOpacity;

    private FragmentActivity mActivity;
    private View mContentView;
    private BackSwipeFragment mFragment;
    private Fragment mPreFragment;

    private Drawable mShadowLeft;
    private Drawable mShadowRight;
    private Rect mTmpRect = new Rect();

    private EdgeOrientation mEdgeOrientation;
    //private EdgeOrientation mCurrentSwipeOrientation;

    private boolean mEnabledBackSwipeGestures = true;

    private Context mContext;
    private EdgeSizeLevel mEdgeSizeLevel;


    /**
     * The set of listeners to be sent events through.
     */
    private List<OnBackSwipeListener> mListeners;

    public BackSwipeLayout(Context context) {
        this(context, null);
    }

    public BackSwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackSwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
        setShadow(R.drawable.bsl_shadow_left, EdgeOrientation.LEFT);
        setEdgeOrientation(BackSwipeHelper.EdgeOrientation.LEFT);
    }

    public void setEnableBackSwipeGesture(boolean enable) {
        mEnabledBackSwipeGestures = enable;
    }

    public boolean getEnableBackSwipeGesture() {
        return mEnabledBackSwipeGestures;
    }

    public void setAutoFinishedVelocityThreshold(double velocity) {
        mAutoFinishedVelocityThreshold = velocity;
    }

    public double getAutoFinishedVelocityThreshold() {
        return mAutoFinishedVelocityThreshold;
    }

    /**
     * Set scroll threshold, we will close the activity, when scrollPercent over
     * this value
     *
     * @param threshold
     */
    public void setTouchSlopThreshold(float threshold) throws IllegalArgumentException {
        if (threshold >= 1.0f || threshold <= 0) {
            throw new IllegalArgumentException("Threshold value should be between 0 and 1.0");
        }
        mTouchSlopThresholdInPercent = threshold;
    }

    public float getTouchSlopThreshold() {
        return mTouchSlopThresholdInPercent;
    }

    /**
     * Enable edge tracking for the selected edges of the parent view.
     * The callback's {@link ViewDragHelper.Callback#onEdgeTouched(int, int)} and
     * {@link ViewDragHelper.Callback#onEdgeDragStarted(int, int)} methods will only be invoked
     * for edges for which edge tracking has been enabled.
     *
     * @param edgeOrientation Combination of edge flags describing the edges to watch
     *                        // * @see #EDGE_LEFT
     *                        // * @see #EDGE_RIGHT
     */
    public void setEdgeOrientation(EdgeOrientation edgeOrientation) {
        this.mEdgeOrientation = edgeOrientation;
        mViewDragHelper.setEdgeTrackingEnabled(edgeOrientation.getValue());

        if (edgeOrientation == EdgeOrientation.RIGHT || edgeOrientation == EdgeOrientation.ALL) {
            setShadow(R.drawable.bsl_shadow_right, EdgeOrientation.RIGHT);
        }
    }

    public EdgeOrientation getEdgeOrientation() {
        return mEdgeOrientation;
    }

    public void setEdgeSizeLevel(EdgeSizeLevel edgeSizeLevel) {
        mEdgeSizeLevel = edgeSizeLevel;
        //validateEdgeLevel(0, mEdgeSizeLevel); //TODO: validate EdgeLevel size
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

    public EdgeSizeLevel getEdgeSizeLevel() {
        return mEdgeSizeLevel;
    }

    /**
     * Set a drawable used for edge shadow.
     */
    private void setShadow(Drawable shadow, EdgeOrientation edgeFlag) {
        if (edgeFlag == EdgeOrientation.LEFT) {
            mShadowLeft = shadow;
        } else if (edgeFlag == EdgeOrientation.RIGHT) {
            mShadowRight = shadow;
        }
        invalidate();
    }

    /**
     * Set a drawable used for edge shadow.
     */
    private void setShadow(int resId, EdgeOrientation edgeFlag) {
        setShadow(getResources().getDrawable(resId), edgeFlag);
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


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean isDrawView = child == mContentView;
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        if (isDrawView && mScrimOpacity > 0 && mViewDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }
        return drawChild;
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);

        if (mEdgeOrientation == EdgeOrientation.LEFT) {
            mShadowLeft.setBounds(childRect.left - mShadowLeft.getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
            mShadowLeft.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowLeft.draw(canvas);
        } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
            mShadowRight.setBounds(childRect.right, childRect.top, childRect.right + mShadowRight.getIntrinsicWidth(), childRect.bottom);
            mShadowRight.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowRight.draw(canvas);
        }
    }

    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (DEFAULT_SCRIM_COLOR & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity);
        final int color = alpha << 24;

        if (mEdgeOrientation == EdgeOrientation.LEFT) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        }
        canvas.drawColor(color);
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = 1 - mDraggingOffsetInPercent;
        if (mScrimOpacity >= 0) {
            if (mViewDragHelper.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    public void setFragment(BackSwipeFragment fragment, View view) {
        this.mFragment = fragment;
        mContentView = view;
    }

    public void hiddenFragment() {
        if (mPreFragment != null && mPreFragment.getView() != null) {
            mPreFragment.getView().setVisibility(GONE);
        }
    }

    public void attachToActivity(FragmentActivity activity) {
        mActivity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    public void attachToFragment(BackSwipeFragment backSwipeFragment, View view) {
        addView(view);
        setFragment(backSwipeFragment, view);
    }

    private void setContentView(View view) {
        mContentView = view;
    }

    class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            boolean dragEnable = mViewDragHelper.isEdgeTouched(mEdgeOrientation.getValue(), pointerId);
            if (dragEnable) {
                if (mPreFragment == null) {
                    if (mFragment != null) {
                        List<Fragment> fragmentList = mFragment.getFragmentManager().getFragments();
                        if (fragmentList != null && fragmentList.size() > 1) {
                            int index = fragmentList.indexOf(mFragment);
                            for (int i = index - 1; i >= 0; i--) {
                                Fragment fragment = fragmentList.get(i);
                                if (fragment != null && fragment.getView() != null) {
                                    fragment.getView().setVisibility(VISIBLE);
                                    mPreFragment = fragment;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    View preView = mPreFragment.getView();
                    if (preView != null && preView.getVisibility() != VISIBLE) {
                        preView.setVisibility(VISIBLE);
                    }
                }
            }
            return dragEnable;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int ret = 0;
            if (mEdgeOrientation == EdgeOrientation.LEFT) {
                ret = Math.min(child.getWidth(), Math.max(left, 0));
            } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
                ret = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return ret;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (mEdgeOrientation == EdgeOrientation.LEFT) {
                mDraggingOffsetInPercent = Math.abs((float) left / (getWidth() + mShadowLeft.getIntrinsicWidth()));
            } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
                mDraggingOffsetInPercent = Math.abs((float) left / (mContentView.getWidth() + mShadowRight.getIntrinsicWidth()));
            }
            invalidate();

            if (mListeners != null && !mListeners.isEmpty()
                    && mViewDragHelper.getViewDragState() == STATE_DRAGGING && mDraggingOffsetInPercent <= 1 && mDraggingOffsetInPercent > 0) {
                for (OnBackSwipeListener listener : mListeners) {
                    listener.onDragScrolled(mDraggingOffsetInPercent);
                }
            }

            if (mDraggingOffsetInPercent > 1) {
                if (mFragment != null) {
                    if (mPreFragment instanceof BackSwipeFragment) {
                        ((BackSwipeFragment) mPreFragment).mLocking = true;
                    }
                    if (!mFragment.isDetached()) {
                        mFragment.mLocking = true;
                        mFragment.getFragmentManager().popBackStackImmediate();
                        mFragment.mLocking = false;
                    }
                    if (mPreFragment instanceof BackSwipeFragment) {
                        ((BackSwipeFragment) mPreFragment).mLocking = false;
                    }
                } else {
                    if (!mActivity.isFinishing()) {
                        mActivity.finish();
                        mActivity.overridePendingTransition(0, 0);
                    }
                }
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            if (mFragment != null) {
                return 1;
            } else if (mActivity != null && (mActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1)) {
                return 1;
            }
            return 0;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // if ((mDraggingOffset == 0) | (mDraggingOffset == mHorizontalDragRange)) return;

            final int childWidth = releasedChild.getWidth();
            boolean isBackSwipe = false;

            int finalLeft = 0;
            Log.d(TAG, "mDraggingOffset = " + mDraggingOffsetInPercent + " mTouchSlopThresholdInPercent =" + mTouchSlopThresholdInPercent);
            if (closeByVelocity(xvel)) {
                isBackSwipe = true;
                Log.d(TAG, "true");
            } else if (mDraggingOffsetInPercent >= mTouchSlopThresholdInPercent) {
                isBackSwipe = true;
            } else if (mDraggingOffsetInPercent < mTouchSlopThresholdInPercent) {
                isBackSwipe = false;
            }

            if (mEdgeOrientation == EdgeOrientation.LEFT) {
                finalLeft = childWidth
                        + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE;
            } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
                finalLeft = -(childWidth
                        + mShadowRight.getIntrinsicWidth() + OVERSCROLL_DISTANCE);
            }
            //TODO:
//            int left = 0;
//            if (mEdgeOrientation == EdgeOrientation.LEFT) {
//                if (xvel > 0 || xvel == 0 && mDraggingOffsetInPercent > mScrollFinishThreshold)
//                    left = childWidth
//                            + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE;
//                else left = 0;
//            } else if (mEdgeOrientation == EdgeOrientation.RIGHT) {
//                if (xvel < 0 || xvel == 0 && mDraggingOffsetInPercent > mScrollFinishThreshold)
//                    left = -(childWidth
//                            + mShadowRight.getIntrinsicWidth() + OVERSCROLL_DISTANCE);
//                else left = 0;
//            }

            finalLeft = isBackSwipe ? finalLeft : 0;
            Log.d(TAG, "finalLeft == " + finalLeft);
            smoothSlideViewTo(finalLeft);
            //mViewDragHelper.settleCapturedViewAt(finalLeft, 0);
            //invalidate();
        }

        private boolean closeByVelocity(float xvel) {
            Log.d(TAG, "xvel == " + xvel);
            if (xvel > 0 && mEdgeOrientation == EdgeOrientation.LEFT && Math.abs(xvel) > mAutoFinishedVelocityThreshold) {
                return true;
            } else if (xvel < 0 && mEdgeOrientation == EdgeOrientation.RIGHT && Math.abs(xvel) > mAutoFinishedVelocityThreshold) {
                return true;
            }
            return false;
        }

        private void smoothSlideViewTo(int finalLeft) {
            if (mViewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(BackSwipeLayout.this);
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListeners != null && !mListeners.isEmpty()) {
                for (OnBackSwipeListener listener : mListeners) {
                    listener.onDragStateChange(state);
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnabledBackSwipeGestures) return super.onInterceptTouchEvent(ev);
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnabledBackSwipeGestures) return super.onTouchEvent(event);
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}
