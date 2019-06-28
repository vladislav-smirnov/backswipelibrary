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
package com.airdaydreamers.backswipelibrary;

import androidx.customview.widget.ViewDragHelper;

/*
  Created by Vladislav Smirnov on 4/25/2018.
  sivdead@gmail.com
 */
public class BackSwipeHelper {

    public static final String TAG = "BackSwipeLibrary";

    /**
     * Edge size enum indicating that which size should be applied for detect gesture.
     */
    public enum EdgeSizeLevel {
        MIN(0),
        MED(1),
        MAX(2);

        EdgeSizeLevel(int i) {
            this.value = i;
        }

        private int value;

        public int getValue() {
            return value;
        }

        public static EdgeSizeLevel convertEdgeSizeLevel(int value) {
            switch (value) {
                case 0:
                    return MIN;
                case 1:
                    return MED;
                case 2:
                    return MAX;

            }
            return MAX;
        }
    }

    /**
     * Edge flag indicating that which edge should be affected.
     */
    public enum EdgeOrientation {
        ALL(ViewDragHelper.EDGE_LEFT | ViewDragHelper.EDGE_RIGHT),
        LEFT(ViewDragHelper.EDGE_LEFT),
        TOP(ViewDragHelper.EDGE_TOP),
        RIGHT(ViewDragHelper.EDGE_RIGHT),
        BOTTOM(ViewDragHelper.EDGE_BOTTOM);

        EdgeOrientation(int i) {
            this.type = i;
        }

        private int type;

        public int getValue() {
            return type;
        }

        public static EdgeOrientation convertEdgeOrientation(int value) {
            switch (value) {
                case ViewDragHelper.EDGE_LEFT | ViewDragHelper.EDGE_RIGHT:
                    return ALL;
                case ViewDragHelper.EDGE_LEFT:
                    return LEFT;
                case ViewDragHelper.EDGE_TOP:
                    return TOP;
                case ViewDragHelper.EDGE_RIGHT:
                    return RIGHT;
                case ViewDragHelper.EDGE_BOTTOM:
                    return BOTTOM;
            }
            return LEFT;
        }
    }
    //region States of ViewDragHelper
    /**
     * A view is not currently being dragged or animating as a result of a
     * fling/snap.
     */
    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;

    /**
     * A view is currently being dragged. The position is currently changing as
     * a result of user input or simulated user input.
     */
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;

    /**
     * A view is currently settling into place as a result of a fling or
     * predefined non-interactive motion.
     */
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;
    //endregion
}
