package com.jzxiang.pickerview.wheel;

/**
 * Wheel clicked listener interfaces.
 * <p>The onItemClicked() method is called whenever a wheel item is clicked
 *  New Wheel position is get
 *  Wheel view is scrolled
 */
public interface OnWheelClickedListener {
    /**
     * Callback method to be invoked when current item clicked
     *
     * @param wheel     the wheel view
     * @param itemIndex the index of clicked item
     */
    void onItemClicked(WheelView wheel, int itemIndex);
}
