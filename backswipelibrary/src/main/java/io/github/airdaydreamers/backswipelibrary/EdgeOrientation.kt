package io.github.airdaydreamers.backswipelibrary

import androidx.customview.widget.ViewDragHelper

/**
 * Edge flag indicating that which edge should be affected.
 */
enum class EdgeOrientation(val value: Int) {
    ALL(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_RIGHT),
    LEFT(ViewDragHelper.EDGE_LEFT),
    TOP(ViewDragHelper.EDGE_TOP),
    RIGHT(ViewDragHelper.EDGE_RIGHT),
    BOTTOM(ViewDragHelper.EDGE_BOTTOM);

    companion object {
        @JvmStatic
        fun convertEdgeOrientation(value: Int): EdgeOrientation {
            when (value) {
                ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_RIGHT -> return ALL
                ViewDragHelper.EDGE_LEFT -> return LEFT
                ViewDragHelper.EDGE_TOP -> return TOP
                ViewDragHelper.EDGE_RIGHT -> return RIGHT
                ViewDragHelper.EDGE_BOTTOM -> return BOTTOM
            }
            return LEFT
        }
    }
}