package com.nolane.stacks.utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * This class contains functions to convert one metric to another.
 */
public class MetricsUtils {
    /**
     * Converts dps to pixels.
     */
    public static float convertDpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * Converts dps to pixels.
     */
    public static int convertDpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
