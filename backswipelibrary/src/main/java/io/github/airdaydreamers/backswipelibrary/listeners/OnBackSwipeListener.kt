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
package io.github.airdaydreamers.backswipelibrary.listeners
/*
  Created by Vladislav Smirnov on 5/4/2018.
  sivdead@gmail.com
 */ /**
 * Interface definition for a callback to be invoked when any back swipe event is happened.
 */
interface OnBackSwipeListener {
    /**
     * Invoke when state change
     *
     * @param state flag to describe scroll state
     * // * @see #STATE_IDLE
     * // * @see #STATE_DRAGGING
     * // * @see #STATE_SETTLING
     */
    fun onDragStateChange(state: Int)

    /**
     * Invoke when edge touched
     *
     * @param oritentationEdgeFlag edge flag describing the edge being touched
     * // * @see #EDGE_LEFT
     * // * @see #EDGE_RIGHT
     */
    fun onEdgeTouch(oritentationEdgeFlag: Int)

    /**
     * Invoke when scroll percent over the threshold for the first time
     *
     * @param scrollPercent scroll percent of this view
     */
    fun onDragScrolled(scrollPercent: Float)
}