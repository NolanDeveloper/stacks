package com.nolane.stacks.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.nolane.stacks.R;

import java.util.Arrays;

public class GeneralUtils {
    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static int hash(Object... objects) {
        return Arrays.hashCode(objects);
    }

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
        return interpolateColor(
                context.getResources().getColor(R.color.bad_progress),
                context.getResources().getColor(R.color.good_progress),
                proportion);
    }

    /**
     * Shortens names of languages. <br>
     * Examples: <br>
     *     Russian -> Rus <br>
     *     russian -> Rus <br>
     *     rUssian -> Rus <br>
     *     Ru -> Ru <br>
     *     ru -> Ru <br>
     *     r -> [empty string]
     * @param language Full language name.
     * @return <li>({@code language}.length < 2) returns empty string.
     * <li>({@code language}.length == 2) returns first two characters: first in upper case, second
     * in lower case.
     * <li>Otherwise returns first 3 characters: first in upper case, other in lower case.
     */
    public static String shortenLanguage(@NonNull String language) {
        if (language.length() < 2) {
            return "";
        }
        if (language.length() == 2) {
            return String.valueOf(Character.toUpperCase(language.charAt(0))) + Character.toLowerCase(language.charAt(1));
        }
        return Character.toUpperCase(language.charAt(0)) + language.substring(1, 3).toLowerCase();
    }
}
