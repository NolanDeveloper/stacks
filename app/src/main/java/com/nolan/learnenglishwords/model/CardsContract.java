package com.nolan.learnenglishwords.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class CardsContract {
    static final String CONTENT_AUTHORITY = "com.nolan.learnenglishwords.provider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    interface DictionariesColumns {
        String DICTIONARY_ID = BaseColumns._ID;
        String DICTIONARY_TITLE = "DICTIONARY_TITLE";
        String DICTIONARY_DESCRIPTION = "DICTIONARY_DESCRIPTION";
    }

    interface CardsColumns {
        String CARD_ID = BaseColumns._ID;
        String CARD_FRONT = "CARD_FRONT";
        String CARD_BACK = "CARD_BACK";
        String CARD_SCRUTINY = "CARD_SCRUTINY";
        String CARD_LAST_SEEN = "CARD_LAST_SEEN";
        String CARD_DICTIONARY_ID = "CARD_DICTIONARY_ID";
    }

    public static final String PATH_DICTIONARIES = "DICTIONARIES";
    public static final String PATH_CARDS = "CARDS";

    public static class Dictionary implements DictionariesColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DICTIONARIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".dictionary";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".dictionary";

        public static final String SORT_DEFAULT =
                DictionariesColumns.DICTIONARY_TITLE + " DESC";

        public static final int MAX_TITLE_LEN = 35;
        public static final int MAX_DESCRIPTION_LEN = 100;

        public static boolean checkTitle(String title) {
            return (null != title) && (title.length() <= MAX_TITLE_LEN);
        }
        public static boolean checkDescription(String description) {
            return (null != description) && (description.length() <= MAX_DESCRIPTION_LEN);
        }
    }

    public static class Card implements CardsColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".card";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".card";

        public static final String SORT_FRONT = CardsColumns.CARD_FRONT  + " DESC";
        public static final String SORT_BACK = CardsColumns.CARD_BACK  + " DESC";
        public static final String SORT_LAST_SEEN = CardsColumns.CARD_LAST_SEEN + " ASC";

        public static final int MAX_FRONT_LEN = 60;
        public static final int MAX_BACK_LEN = 60;

        public static boolean checkFront(String front) {
            return (null != front) && (front.length() <= MAX_FRONT_LEN);
        }
        public static boolean checkBack(String back) {
            return (null != back) && (back.length() <= MAX_BACK_LEN);
        }
        public static Uri buildUriToCardsOfDictionary(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
