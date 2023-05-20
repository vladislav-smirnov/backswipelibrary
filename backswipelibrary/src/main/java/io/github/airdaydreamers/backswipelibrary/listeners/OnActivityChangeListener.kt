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
 */
/**
 * Interface definition for a callback to be invoked when Activity is changed.
 */
fun interface OnActivityChangeListener {
    /**
     * Return scrolled fraction of the layout.
     *
     * @param groupThreshold relative to the anchor.
     * @param groupScreen    relative to the screen.
     */
    fun onViewPositionChanged(groupThreshold: Float, groupScreen: Float)
}