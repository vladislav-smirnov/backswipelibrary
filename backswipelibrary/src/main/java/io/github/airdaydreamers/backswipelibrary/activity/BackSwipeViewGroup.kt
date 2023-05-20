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
package io.github.airdaydreamers.backswipelibrary.activity

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.viewpager.widget.ViewPager
import io.github.airdaydreamers.backswipelibrary.*
import io.github.airdaydreamers.backswipelibrary.EdgeOrientation.Companion.convertEdgeOrientation
import io.github.airdaydreamers.backswipelibrary.EdgeSizeLevel.Companion.convertEdgeSizeLevel
import io.github.airdaydreamers.backswipelibrary.listeners.OnActivityChangeListener
import io.github.airdaydreamers.backswipelibrary.listeners.OnBackSwipeListener
import java.util.*

/*
  Created by Vladislav Smirnov on 5/4/2018.
  sivdead@gmail.com
 */
open class BackSwipeViewGroup(private var mContext: Context, attrs: AttributeSet?) : ViewGroup(mContext, attrs) {
    private val TAG = BackSwipeState.TAG + "-" + this.javaClass.simpleName

    @JvmField
    protected var mEdgeOrientation = EdgeOrientation.LEFT
    var backgroundColor = DEFAULT_BACKGROUND_COLOR
        private set
    var enabledBackSwipeGesture = true
    var autoFinishedVelocityThreshold = 1500.0

    //private static final float BACK_FACTOR = 0.5f;
    @JvmField
    protected var mViewDragHelper: ViewDragHelper
    private var mTargetView: View? = null
    private var mScrollChildView: View? = null

    @JvmField
    protected var mHorizontalDragRange = 1

    @JvmField
    protected var mDraggingState = 0

    @JvmField
    protected var mDraggingOffset = 0

    /**
     * The threshold of calling finish Activity.
     */
    protected var mTouchSlopThreshold = 0f

    /**
     * Set the threshold of calling finish.
     *
     * @param threshold
     */
    var touchSlopThreshold = 0f
        set(threshold) {
            mTouchSlopThreshold = threshold
        }

    @JvmField
    protected var mDraggingOffsetInPercent = 0f
    protected var isPercentEnabled = false

    /**
     * The set of listeners to be sent events through.
     */
    @JvmField
    protected var mListeners: MutableList<OnBackSwipeListener>? = null
    protected var mOnActivityChangeListener: OnActivityChangeListener? = null

    constructor(context: Context) : this(context, null) {
        mContext = context
    }

    protected fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.bsl_BackSwipeViewGroup, 0, 0)
            try {
                enabledBackSwipeGesture = array.getBoolean(R.styleable.bsl_BackSwipeViewGroup_bsl_enableSwipe, true)
                mTouchSlopThreshold = array.getFloat(R.styleable.bsl_BackSwipeViewGroup_bsl_touchSlopThreshold, 500.0f)
                mEdgeOrientation = convertEdgeOrientation(array.getInteger(R.styleable.bsl_BackSwipeViewGroup_bsl_edgeOrientation, EdgeOrientation.LEFT.value))
                EdgeOrientation.valueOf("left")
                setEdgeSizeLevel(convertEdgeSizeLevel(array.getInteger(R.styleable.bsl_BackSwipeViewGroup_bsl_edgeSizeLevel, EdgeSizeLevel.MIN.value)))
                backgroundColor = array.getColor(R.styleable.bsl_BackSwipeViewGroup_bsl_backgroundColor, Color.WHITE)
            } finally {
                array.recycle()
            }
        }
    }

    var edgeOrientation: EdgeOrientation
        get() = mEdgeOrientation
        set(edgeOrientation) {
            mEdgeOrientation = edgeOrientation
            mViewDragHelper.setEdgeTrackingEnabled(edgeOrientation.value)
        }

    fun setEdgeSizeLevel(edgeSizeLevel: EdgeSizeLevel) {
        try {
            val metrics = DisplayMetrics()
            val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    ?: return
            windowManager.defaultDisplay.getMetrics(metrics)
            val mEdgeSize = mViewDragHelper.javaClass.getDeclaredField("mEdgeSize")
            mEdgeSize.isAccessible = true
            when (edgeSizeLevel) {
                EdgeSizeLevel.MAX -> mEdgeSize.setInt(mViewDragHelper, metrics.widthPixels)
                EdgeSizeLevel.MED -> mEdgeSize.setInt(mViewDragHelper, metrics.widthPixels / 2)
                EdgeSizeLevel.MIN -> mEdgeSize.setInt(mViewDragHelper, (20 * metrics.density + 0.5f).toInt())
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun setScrollChildView(view: View?) {
        mScrollChildView = view
    }

    @Throws(IllegalArgumentException::class)
    fun setTouchSlopThreshold(threshold: Int) {
        var threshold = threshold
        threshold = threshold / 100
        require(!(threshold >= 1.0f || threshold <= 0)) { "Threshold value should be between 0 and 1.0" }
        touchSlopThreshold = threshold.toFloat()
    }

    fun setColorForBackground(color: Int) {
        backgroundColor = color
    }

    fun setOnSwipeBackListener(listener: OnActivityChangeListener?) {
        mOnActivityChangeListener = listener
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener the swipe listener to attach to this view
     */
    fun addSwipeListener(listener: OnBackSwipeListener) {
        if (mListeners == null) {
            mListeners = ArrayList()
        }
        mListeners!!.add(listener)
    }

    /**
     * Removes a listener from the set of listeners
     *
     * @param listener
     */
    fun removeSwipeListener(listener: OnBackSwipeListener) {
        if (mListeners == null) {
            return
        }
        mListeners!!.remove(listener)
    }

    protected fun ensureTarget() {
        if (mTargetView == null) {
            if (childCount > 1) {
                val ex = IllegalStateException("BackSwipeView must contains only one direct child")
                Log.e(TAG, ex.message)
                throw ex
            }
            mTargetView = getChildAt(0)
            if (mScrollChildView == null && mTargetView != null) {
                if (mTargetView is ViewGroup) findScrollView(mTargetView as ViewGroup) else mScrollChildView = mTargetView
            }
        }
    }

    /**
     * Find out the scrollable child view from a ViewGroup.
     *
     * @param viewGroup
     */
    private fun findScrollView(viewGroup: ViewGroup) {
        mScrollChildView = viewGroup
        if (viewGroup.childCount > 0) {
            val count = viewGroup.childCount
            var child: View?
            for (i in 0 until count) {
                child = viewGroup.getChildAt(i)
                if (child is AbsListView || child is ScrollView || child is ViewPager || child is WebView) {
                    mScrollChildView = child
                    return
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) return
        val child = getChildAt(0)
        val background = child.background
        if (background != null) {
            if (background is ColorDrawable) {
                backgroundColor = background.color
            }
        }
        child.setBackgroundColor(backgroundColor)
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childRight = childLeft + childWidth
        val childBottom = childTop + childHeight
        child.layout(childLeft, childTop, childRight, childBottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        check(childCount <= 1) { "BackSwipeView must contains only one direct child." }
        if (childCount > 0) {
            val measureWidth = MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, MeasureSpec.EXACTLY)
            val measureHeight = MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, MeasureSpec.EXACTLY)
            getChildAt(0).measure(measureWidth, measureHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHorizontalDragRange = w
        mTouchSlopThreshold = if (mTouchSlopThreshold > 0) mTouchSlopThreshold else mHorizontalDragRange * 0.5f
        touchSlopThreshold = if (touchSlopThreshold > 0) touchSlopThreshold else 0.5f
        if (touchSlopThreshold > 0) isPercentEnabled = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!enabledBackSwipeGesture) return super.onInterceptTouchEvent(ev)
        var isHandled = false
        ensureTarget()
        if (isEnabled) isHandled = mViewDragHelper.shouldInterceptTouchEvent(ev) else mViewDragHelper.cancel()
        return if (!isHandled) super.onInterceptTouchEvent(ev) else isHandled
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!enabledBackSwipeGesture) return super.onTouchEvent(event)
        mViewDragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    protected fun canChildScrollRight(): Boolean {
        return mScrollChildView!!.canScrollHorizontally(-1)
    }

    protected fun canChildScrollLeft(): Boolean {
        return mScrollChildView!!.canScrollHorizontally(1)
    }

    private fun finish() {
        val act = context as Activity
        act.finish()
        act.overridePendingTransition(0, android.R.anim.fade_out)
    }

    protected fun closeByVelocity(xvel: Float): Boolean {
        Log.d(TAG, "xvel == $xvel")
        if (xvel > 0 && mEdgeOrientation === EdgeOrientation.LEFT && Math.abs(xvel) > autoFinishedVelocityThreshold) {
            return if (mEdgeOrientation === EdgeOrientation.LEFT) !canChildScrollLeft() else !canChildScrollRight()
        } else if (xvel < 0 && mEdgeOrientation === EdgeOrientation.RIGHT && Math.abs(xvel) > autoFinishedVelocityThreshold) {
            return if (mEdgeOrientation === EdgeOrientation.RIGHT) !canChildScrollLeft() else !canChildScrollRight()
        }
        return false
    }

    protected fun smoothSlideViewTo(finalLeft: Int, view: View?) {
        Log.d(TAG, "finalLeft == " + finalLeft + "width =" + width)
        if (mViewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(view!!)
        }
    }

    open inner class ViewDragHelperCallBack : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            val dragEnable = mViewDragHelper.isEdgeTouched(mEdgeOrientation.value, pointerId)
            return if (dragEnable) child === mTargetView else dragEnable
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return mHorizontalDragRange
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            Log.d(TAG, "clampViewPositionHorizontal")
            var result = 0
            if (mEdgeOrientation === EdgeOrientation.LEFT && !canChildScrollRight() && left > 0) {
                val leftBound = paddingLeft
                val rightBound = mHorizontalDragRange
                result = Math.min(Math.max(left, leftBound), rightBound)
            } else if (mEdgeOrientation === EdgeOrientation.RIGHT && !canChildScrollLeft() && left < 0) {
                val leftBound = -mHorizontalDragRange
                val rightBound = paddingLeft
                result = Math.min(Math.max(left, leftBound), rightBound)
            }
            return result
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == mDraggingState) return
            if ((mDraggingState == SwipeState.STATE_DRAGGING.value || mDraggingState == STATE_SETTLING) &&
                    state == STATE_IDLE && mDraggingOffset == mHorizontalDragRange) finish()
            mDraggingState = state
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            mDraggingOffset = Math.abs(left)
            if (mEdgeOrientation === EdgeOrientation.LEFT) {
                mDraggingOffsetInPercent = Math.abs(left.toFloat() / width)
            } else if (mEdgeOrientation === EdgeOrientation.RIGHT) {
                mDraggingOffsetInPercent = Math.abs(left.toFloat() / width)
            }

            //The proportion of the sliding.
            var groupThreshold = mDraggingOffset.toFloat() / if (!isPercentEnabled) mTouchSlopThreshold else touchSlopThreshold * width
            if (groupThreshold >= 1) groupThreshold = 1f
            var groupScreen = mDraggingOffset.toFloat() / mHorizontalDragRange.toFloat()
            if (groupScreen >= 1) groupScreen = 1f
            if (mOnActivityChangeListener != null) mOnActivityChangeListener!!.onViewPositionChanged(groupThreshold, groupScreen)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if ((mDraggingOffset == 0) or (mDraggingOffset == mHorizontalDragRange)) return
            var isBackSwipe = false
            if (closeByVelocity(xvel)) {
                isBackSwipe = !canChildScrollLeft() /*| canChildScrollRight()*/
            } else if (isPercentEnabled) isBackSwipe = mDraggingOffsetInPercent >= touchSlopThreshold else isBackSwipe = mDraggingOffset >= mTouchSlopThreshold
            var finalLeft = 0
            if (mEdgeOrientation === EdgeOrientation.LEFT) {
                finalLeft = if (isBackSwipe) mHorizontalDragRange else 0
            } else if (mEdgeOrientation === EdgeOrientation.RIGHT) {
                finalLeft = if (isBackSwipe) -mHorizontalDragRange else 0
            }
            smoothSlideViewTo(finalLeft, this@BackSwipeViewGroup)
        }
    }

    companion object {
        private const val DEFAULT_BACKGROUND_COLOR = Color.WHITE
    }

    init {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, ViewDragHelperCallBack())
        init(mContext, attrs)
    }
}