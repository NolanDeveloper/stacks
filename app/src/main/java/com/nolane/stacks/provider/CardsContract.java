package com.nolane.stacks.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * This class is public interface of CardsProvider. You can access it using
 * field of this class.
 */
public class CardsContract {
    public static final String CONTENT_AUTHORITY = "com.nolane.stacks.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Columns for creating table and making queries.
    interface StacksColumns {
        // The id of column.
        String STACK_ID = "STACK_ID";
        // The Title.
        String STACK_TITLE = "STACK_TITLE";
        // Maximum of cards in "in learning" state.
        String STACK_MAX_IN_LEARNING = "STACK_MAX_IN_LEARNING";
        // The amount of cards in this stack.
        String STACK_COUNT_CARDS = "STACK_COUNT_CARDS";
        // The language of this stack.
        String STACK_LANGUAGE = "STACK_LANGUAGE";
        // The color associated with this stack. Integer value.
        String STACK_COLOR = "STACK_COLOR";
    }

    interface CardsColumns {
        // Id column.
        String CARD_ID = "CARD_ID";
        // Front text.
        String CARD_FRONT = "CARD_FRONT";
        // Back text.
        String CARD_BACK = "CARD_BACK";
        // This values symbolizes the degree of knowing of card.
        String CARD_PROGRESS = "CARD_PROGRESS";
        // Unix time when this card was answered last time.
        // Format is integer that means the number of milliseconds since 1970-01-01 00:00:00 UTC.
        String CARD_LAST_SEEN = "CARD_LAST_SEEN";
        // Id of the stack this card belongs to.
        String CARD_STACK_ID = "CARD_STACK_ID";
        // Flag which shows if card is in learning.
        String CARD_IN_LEARNING = "CARD_IN_LEARNING";
    }

    interface AnswersColumns {
        // Id columns.
        String ANSWER_ID = "ANSWER_ID";
        // Id of card which was answered..
        String ANSWER_CARD_ID = "ANSWER_CARD_ID";
        // The time and date of the answer.
        String ANSWER_TIMESTAMP = "ANSWER_TIMESTAMP";
        // User's assumption.
        String ANSWER_ASSUMPTION = "ANSWER_ASSUMPTION";
    }

    // Paths for uris. They are also table names.
    public static final String PATH_STACKS = "STACKS";
    public static final String PATH_CARDS = "CARDS";
    public static final String PATH_ANSWERS = "ANSWERS";

    /**
     * This class holds fields and methods to easily make requests to stacks table of CardsProvider.
     */
    public static class Stacks implements StacksColumns {
        // Uri pointing to the table of stacks.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STACKS).build();

        /**
         * Creates uri to partial stack that has id equal {@code stackId}.
         * @param id Id of stack to create uri to.
         * @return Uri that points to stack with id equal {@code stackId}.
         */
        public static Uri uriToStack(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".stack";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".stack";

        // Default sort order for this table. If no order is specified this will be used.
        public static final String SORT_DEFAULT = StacksColumns.STACK_TITLE + " DESC";

        // Maximum length of title.
        public static final int MAX_TITLE_LEN = 35;
        // Maximum length of language.
        public static final int MAX_LANGUAGE_LEN = 20;

        public static boolean checkTitle(String title) {
            return (null != title) && (title.length() <= MAX_TITLE_LEN);
        }

        public static boolean checkLanguage(String language) {
            return (null != language) && (language.length() <= MAX_LANGUAGE_LEN);
        }
    }

    /**
     * This class holds fields and methods to easily make requests to cards table of CardsProvider.
     */
    public static class Cards implements CardsColumns {
        // Uri pointing to the table of cards.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();

        /**
         * Creates uri for all cards that belongs to the {@code id}.
         * @param id Id of the stack.
         * @return uri for all cards that belong to the {@code id}.
         */
        public static Uri uriToCardsOfStack(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Creates uri to the card with id equal to {@code cardId} of stack
         * with id equal to {@code stackId}.
         * @param stackId Id of stack.
         * @param cardId  Id of card.
         * @return Uri to the card with id equal to {@code cardId} of stack
         */
        public static Uri uriToCard(long stackId, long cardId) {
            return ContentUris.withAppendedId(uriToCardsOfStack(stackId), cardId);
        }

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".card";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".card";

        // Sorting orders.
        public static final String SORT_FRONT = CardsColumns.CARD_FRONT + " DESC";
        public static final String SORT_BACK = CardsColumns.CARD_BACK + " DESC";
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

    /**
     * This class holds fields and methods to easily make requests to answers table of CardsProvider.
     */
    public static class Answers implements AnswersColumns {
        // Uri pointing to the table of answers.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ANSWERS).build();

        /**
         * Creates uri for answer with id equal to {@code id}.
         * @param id Id of the answer.
         * @return uri Uri for answer with id equal to {@code id}.
         */
        public static Uri uriToAnswer(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".answer";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY + ".answer";
    }
}
