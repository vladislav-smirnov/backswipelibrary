/*
 *  Copyright (c)  Vladislav Smirnov, 2020.
 *  All Rights Reserved.
 *
 *  The reproduction, transmission or use of this document or its contents is
 *  not permitted without express written authority.
 *  Offenders will be liable for damages. All rights, including rights created
 *  by patent grant or registration of a utility model or design, are reserved.
 */

package io.github.airdaydreamers.backswipelibrary

import androidx.customview.widget.ViewDragHelper

sealed class SwipeState(val value: Int) {

    companion object {
        @JvmStatic
        fun convert(value: Int): SwipeState =
                when (value) {
                    ViewDragHelper.STATE_IDLE -> STATE_IDLE
                    ViewDragHelper.STATE_DRAGGING -> STATE_DRAGGING
                    ViewDragHelper.STATE_SETTLING -> STATE_SETTLING
                    else -> STATE_IDLE
                }
    }

    object STATE_IDLE : SwipeState(ViewDragHelper.STATE_IDLE)
    object STATE_DRAGGING : SwipeState(ViewDragHelper.STATE_DRAGGING)
    object STATE_SETTLING : SwipeState(ViewDragHelper.STATE_SETTLING)
}