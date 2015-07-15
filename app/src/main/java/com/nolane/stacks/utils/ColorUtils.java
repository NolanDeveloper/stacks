package com.nolane.stacks.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.nolane.stacks.R;

/**
 * This class contains some useful function for working with colors.
 */
public class ColorUtils {
    // Help function that interpolates values.
    private static float interpolate(float a, float b, float proportion) {
        return a + (b - a) * proportion;
    }

    /**
     * Returns an interpolated color, between <code>a</code> and <code>b</code>.
     * Colors are interpolated in hsv so interpolation will be smooth.
     * Thanks to <a href="http://stackoverflow.com/a/7871291/4626533">Mark Renouf</a>
     * and StackOverflow.
     *
     * @param colorZero  The color where interpolation hav start. If proportion == 0 this
     *                   color will be returned.
     * @param colorOne   The color where interpolation has finish. If proportion == 1 this
     *                   color will be returned.
     * @param proportion Proportion in which to mix colors.
     * @return Interpolated color.
     */
    public static int interpolateColor(int colorZero, int colorOne, float proportion) {
        if (proportion < 0 || 1 < proportion) {
            throw new IllegalArgumentException("Proportion is out of limits. Proportion = " + proportion + ", but must be in [0, 1].");
        }
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(colorZero, hsva);
        Color.colorToHSV(colorOne, hsvb);
        for (int i = 0; i < 3; i++) {
            hsvb[i] = interpolate(hsva[i], hsvb[i], proportion);
        }
        return Color.HSVToColor(hsvb);
    }

    /**
     * Returns color which represents current progress.
     *
     * @param cardProgress Progress value of some card.
     * @return Color which represents current progress.
     */
    public static int getColorForProgress(@NonNull Context context, int cardProgress) {
        int minProgress = context.getResources().getInteger(R.integer.default_min_progress);
        int maxProgress = context.getResources().getInteger(R.integer.default_max_progress);
        if (cardProgress < minProgress || maxProgress < cardProgress) {
            throw new IllegalArgumentException("The progress of card is out of limits.");
        }
        if (maxProgress <= minProgress) {
            throw new IllegalStateException("Maximum progress <= minimal progress of card.");
        }
        float proportion = (float) (cardProgress - minProgress) / (maxProgress - minProgress);
        return ColorUtils.interpolateColor(
                context.getResources().getColor(R.color.bad_progress),
                context.getResources().getColor(R.color.good_progress),
                proportion);
    }
}
