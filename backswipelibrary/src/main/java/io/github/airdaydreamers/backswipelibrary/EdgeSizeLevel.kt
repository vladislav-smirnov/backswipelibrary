package io.github.airdaydreamers.backswipelibrary

/**
 * Edge size enum indicating that which size should be applied for detect gesture.
 */
sealed class EdgeSizeLevel @JvmOverloads constructor(val value: Int = 0) {
    companion object {
        @JvmStatic
        fun convertEdgeSizeLevel(value: Int): EdgeSizeLevel {
            when (value) {
                0 -> return MIN
                1 -> return MED
                2 -> return MAX
            }
            return MAX
        }


    }
    object MIN : EdgeSizeLevel(0)
    object MED : EdgeSizeLevel(1)
    object MAX : EdgeSizeLevel(2)
}