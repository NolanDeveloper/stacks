package com.nolan.learnenglishwords.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * This class is public interface of CardsProvider. You can access it using
 * field of this class.
 * */
public class CardsContract {
    static final String CONTENT_AUTHORITY = "com.nolan.learnenglishwords.provider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Columns for creating table and making queries.
    interface DictionariesColumns {
        // Id column.
        String DICTIONARY_ID = BaseColumns._ID;
        // Title.
        String DICTIONARY_TITLE = "DICTIONARY_TITLE";
        // Brief description.
        String DICTIONARY_DESCRIPTION = "DICTIONARY_DESCRIPTION";
    }
    interface CardsColumns {
        // Id column.
        String CARD_ID = BaseColumns._ID;
        // Front text.
        String CARD_FRONT = "CARD_FRONT";
        // Back text.
        String CARD_BACK = "CARD_BACK";
        // This values symbolizes the degree of knowing of card.
        String CARD_SCRUTINY = "CARD_SCRUTINY";
        // Unix time when this card was answered last time.
        // Format is integer meaning the number of seconds since 1970-01-01 00:00:00 UTC.
        String CARD_LAST_SEEN = "CARD_LAST_SEEN";
        // Id of the dictionary this card belongs to.
        String CARD_DICTIONARY_ID = "CARD_DICTIONARY_ID";
    }

    // Paths for uris. They are also table names.
    public static final String PATH_DICTIONARIES = "DICTIONARIES";
    public static final String PATH_CARDS = "CARDS";

    /**
     * This class holds fields and methods to easily make requests to dictionaries table of  CardsProvider.
     * */
    public static class Dictionary implements DictionariesColumns {
        // Uri pointing to the table of dictionaries.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DICTIONARIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".dictionary";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".dictionary";

        // Default sort order for this table. If no order is specified this will be used.
        public static final String SORT_DEFAULT =
                DictionariesColumns.DICTIONARY_TITLE + " DESC";

        // Maximum length of title.
        public static final int MAX_TITLE_LEN = 35;
        // Maximum length of description.
        public static final int MAX_DESCRIPTION_LEN = 100;

        public static boolean checkTitle(String title) {
            return (null != title) && (title.length() <= MAX_TITLE_LEN);
        }
        public static boolean checkDescription(String description) {
            return (null != description) && (description.length() <= MAX_DESCRIPTION_LEN);
        }
    }

    /**
     * This class holds fields and methods to easily make requests to cards table of CardsProvider.
     * */
    public static class Card implements CardsColumns {
        // Uri pointing to the table of cards.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();
        /**
         * Creates uri for all cards that belongs to the {@code id}.
         * @param id Id of the dictionary.
         * @return uri for all cards that belong to the {@code id}.
         * */
        public static Uri buildUriToCardsOfDictionary(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".card";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".card";

        // Sorting orders.
        public static final String SORT_FRONT = CardsColumns.CARD_FRONT  + " DESC";
        public static final String SORT_BACK = CardsColumns.CARD_BACK  + " DESC";
        public static final String SORT_LAST_SEEN = CardsColumns.CARD_LAST_SEEN + " ASC";

        // Maximum length of front text.
        public static final int MAX_FRONT_LEN = 60;
        // Maximum length of back text.
        public static final int MAX_BACK_LEN = MAX_FRONT_LEN;

        public static boolean checkFront(String front) {
            return (null != front) && (front.length() <= MAX_FRONT_LEN);
        }
        public static boolean checkBack(String back) {
            return (null != back) && (back.length() <= MAX_BACK_LEN);
        }
    }
}
