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
package io.github.airdaydreamers.backswipelibrary

import androidx.customview.widget.ViewDragHelper

/*
  Created by Vladislav Smirnov on 4/25/2018.
  sivdead@gmail.com
 */
object BackSwipeState {
    const val TAG = "BackSwipeLibrary"
    //region States of ViewDragHelper
    /**
     * A view is not currently being dragged or animating as a result of a
     * fling/snap.
     */
    const val STATE_IDLE = ViewDragHelper.STATE_IDLE

    /**
     * A view is currently being dragged. The position is currently changing as
     * a result of user input or simulated user input.
     */
    const val STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING

    /**
     * A view is currently settling into place as a result of a fling or
     * predefined non-interactive motion.
     */
    const val STATE_SETTLING = ViewDragHelper.STATE_SETTLING
    //endregion
}