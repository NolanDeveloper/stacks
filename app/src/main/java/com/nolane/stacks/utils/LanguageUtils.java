package com.nolane.stacks.utils;

import android.support.annotation.NonNull;

/**
 * One function utils class.
 */
public class LanguageUtils {
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
