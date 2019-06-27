package com.airdaydreamers.backswipelibrary.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.R;
import com.airdaydreamers.backswipelibrary.activity.BackSwipeViewGroup;
import com.airdaydreamers.backswipelibrary.listeners.OnBackSwipeListener;

import java.util.List;

import static com.airdaydreamers.backswipelibrary.BackSwipeHelper.STATE_DRAGGING;
import static com.airdaydreamers.backswipelibrary.BackSwipeHelper.STATE_IDLE;
import static com.airdaydreamers.backswipelibrary.BackSwipeHelper.STATE_SETTLING;

/**
 * Created by Vladislav Smirnov on 24.06.19.
 * sivdead@gmail.com
 */
public class BackSwipeLayoutView extends BackSwipeViewGroup {
    private final String TAG = BackSwipeHelper.TAG + "-" + this.getClass().getSimpleName();

    private BackSwipeFragment mFragment;
    private FragmentActivity mActivity;
    private Fragment mPreFragment;

    public BackSwipeLayoutView(Context context) {
        this(context, null);
    }

    public BackSwipeLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackSwipeLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new FrameLayoutViewDragHelperCallBack());
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
        decor.addView(this);
    }

    public void attachToFragment(BackSwipeFragment backSwipeFragment, View view) {
        addView(view);
        setFragment(backSwipeFragment);
    }

    public void setFragment(BackSwipeFragment fragment) {
        mFragment = fragment;
    }

    public void hiddenFragment() {
        if (mPreFragment != null && mPreFragment.getView() != null) {
            mPreFragment.getView().setVisibility(GONE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnabledBackSwipeGestures) return super.onInterceptTouchEvent(ev);
        ensureTarget();
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnabledBackSwipeGestures) return super.onTouchEvent(event);
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private class FrameLayoutViewDragHelperCallBack extends BackSwipeViewGroup.ViewDragHelperCallBack {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
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
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal");

            int result = 0;
            if (mEdgeOrientation == BackSwipeHelper.EdgeOrientation.LEFT) {
                result = Math.min(child.getWidth(), Math.max(left, 0));
            } else if (mEdgeOrientation == BackSwipeHelper.EdgeOrientation.RIGHT) {
                result = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return result;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState) return;

            if (((mDraggingState == STATE_DRAGGING) || (mDraggingState == STATE_SETTLING)) &&
                    (state == STATE_IDLE) && (mDraggingOffset == mHorizontalDragRange)) {
                if (mListeners != null && !mListeners.isEmpty()) {
                    for (OnBackSwipeListener listener : mListeners) {
                        listener.onDragStateChange(state);
                    }
                }
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (mDraggingOffsetInPercent >= 1) {
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
    }
}
