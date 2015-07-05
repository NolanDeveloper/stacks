package com.nolan.learnenglishwords;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;

import com.nolan.learnenglishwords.core.BusinessLogic;
import com.nolan.learnenglishwords.database.DBOpenHelper;
import com.nolan.learnenglishwords.model.Card;
import com.nolan.learnenglishwords.model.Dictionary;

import java.sql.SQLException;

public class TestBusinessLogic extends AndroidTestCase {
    private BusinessLogic businessLogic;

    public TestBusinessLogic() {
        super();
        setContext(new IsolatedContext(null, getContext()));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DBOpenHelper.GetInstance(getContext()).getReadableDatabase().close();
        getContext().deleteDatabase(DBOpenHelper.NAME);
        businessLogic = BusinessLogic.GetInstance(getContext());
    }

    public void testAddDictionary() {
        Cursor c = null;
        try {
            long id = businessLogic.addDictionary("test", "description");
            c = businessLogic.queryDictionaries();
            assertEquals(1, c.getCount());
            c.moveToFirst();
            Dictionary dictionary = new Dictionary(c);
            assertEquals(id, dictionary.id);
            assertEquals("test", dictionary.title);
            assertEquals("description", dictionary.description);
            c.close();
            businessLogic.addDictionary("one", null);
            businessLogic.addDictionary("two", null);
            businessLogic.addDictionary("three", null);
            c = businessLogic.queryDictionaries();
            assertEquals(4, c.getCount());
            c.close();
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        } finally {
            if ((null != c) && !c.isClosed())
                c.close();
        }
    }

    public void testAddCard() {
        Cursor c = null;
        try {
            long id = businessLogic.addDictionary("Test", null);
            businessLogic.addCard(id, "hello", "привет");
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            Card card = new Card(c);
            c.close();
            assertEquals("hello", card.front);
            assertEquals("привет", card.back);
            businessLogic.addCard(id, "one", "один");
            businessLogic.addCard(id, "two", "два");
            businessLogic.addCard(id, "three", "три");
            businessLogic.addCard(id, "four", "четыре");
            c = businessLogic.queryCards(id);
            assertEquals(5, c.getCount());
            c.close();
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        } finally {
            if ((null != c) && !c.isClosed())
                c.close();
        }
    }

    public void testUpdateDictionary() {
        Cursor c = null;
        boolean allRight;
        try {
            long id = businessLogic.addDictionary("test", "description");
            c = businessLogic.queryDictionaries();
            assertEquals(1, c.getCount());
            c.moveToFirst();
            Dictionary dictionary = new Dictionary(c);
            assertEquals(id, dictionary.id);
            assertEquals("test", dictionary.title);
            assertEquals("description", dictionary.description);
            c.close();
            businessLogic.updateDictionary(id, "title", null);
            c = businessLogic.queryDictionaries();
            assertEquals(1, c.getCount());
            c.moveToFirst();
            dictionary = new Dictionary(c);
            assertEquals(id, dictionary.id);
            assertEquals("title", dictionary.title);
            assertEquals("description", dictionary.description);
            c.close();
            businessLogic.updateDictionary(id, null, "empty");
            c = businessLogic.queryDictionaries();
            assertEquals(1, c.getCount());
            c.moveToFirst();
            dictionary = new Dictionary(c);
            assertEquals(id, dictionary.id);
            assertEquals("title", dictionary.title);
            assertEquals("empty", dictionary.description);
            c.close();
            try {
                allRight = false;
                businessLogic.updateDictionary(id, null, null);
            } catch (IllegalArgumentException e) { allRight = true; }
            assertTrue("updateDictionary() with both title and description equal null must lead to IllegalArgumentException.", allRight);
            try {
                allRight = false;
                String tooLongTitle = new String(new char[BusinessLogic.MAX_DICTIONARY_TITLE_LEN + 1]).replace('\0', ' ');
                businessLogic.updateDictionary(id, tooLongTitle, null);
            } catch (IllegalArgumentException e) { allRight = true; }
            assertTrue("updateDictionary() with too long title must lead to IllegalArgumentException.", allRight);
            try {
                allRight = false;
                String tooLongDescription = new String(new char[BusinessLogic.MAX_DICTIONARY_DESCRIPTION_LEN + 1]).replace('\0', ' ');
                businessLogic.updateDictionary(id, tooLongDescription, null);
            } catch (IllegalArgumentException e) { allRight = true; }
            assertTrue("updateDictionary() with too long description must lead to IllegalArgumentException.", allRight);
            c = businessLogic.queryDictionaries();
            assertEquals(1, c.getCount());
            c.moveToFirst();
            dictionary = new Dictionary(c);
            assertEquals(id, dictionary.id);
            assertEquals("title", dictionary.title);
            assertEquals("empty", dictionary.description);
            c.close();
            businessLogic.updateDictionary(id, "", "");
            c = businessLogic.queryDictionaries();
            assertEquals(1, c.getCount());
            c.moveToFirst();
            dictionary = new Dictionary(c);
            assertEquals(id, dictionary.id);
            assertEquals("", dictionary.title);
            assertEquals("", dictionary.description);
            c.close();
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        } finally {
            if ((null != c) && !c.isClosed())
                c.close();
        }
    }

    public void testUpdateCard() {
        Cursor c = null;
        boolean allRight;
        try {
            long id = businessLogic.addDictionary("test", "");
            long cardId = businessLogic.addCard(id, "front", "back");
            businessLogic.updateCard(id, cardId, "newFront", null);
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            Card card = new Card(c);
            assertEquals(cardId, card.id);
            assertEquals("newFront", card.front);
            assertEquals("back", card.back);
            c.close();
            businessLogic.updateCard(id, cardId, null, "newBack");
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            card = new Card(c);
            assertEquals(cardId, card.id);
            assertEquals("newFront", card.front);
            assertEquals("newBack", card.back);
            c.close();
            try {
                allRight = false;
                businessLogic.updateCard(id, cardId, null, null);
            } catch (IllegalArgumentException e) { allRight = true; }
            assertTrue("updateCard() with both front and back equal null must lead to IllegalArgumentException.", allRight);
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            card = new Card(c);
            assertEquals(cardId, card.id);
            assertEquals("newFront", card.front);
            assertEquals("newBack", card.back);
            c.close();
            try {
                allRight = false;
                String tooLongFront = new String(new char[BusinessLogic.MAX_FRONT_LEN + 1]).replace('\0', ' ');
                businessLogic.updateCard(id, cardId, tooLongFront, null);
            } catch (IllegalArgumentException e) { allRight = true; }
            assertTrue("updateCard() with too long front must lead to IllegalArgumentException.", allRight);
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            card = new Card(c);
            assertEquals(cardId, card.id);
            assertEquals("newFront", card.front);
            assertEquals("newBack", card.back);
            c.close();
            try {
                allRight = false;
                String tooLongBack = new String(new char[BusinessLogic.MAX_BACK_LEN + 1]).replace('\0', ' ');
                businessLogic.updateCard(id, cardId, null, tooLongBack);
            } catch (IllegalArgumentException e) { allRight = true; }
            assertTrue("updateCard() with too long back must lead to IllegalArgumentException.", allRight);
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            card = new Card(c);
            assertEquals(cardId, card.id);
            assertEquals("newFront", card.front);
            assertEquals("newBack", card.back);
            c.close();
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        } finally {
            if ((null != c) && !c.isClosed()) {
                c.close();
            }
        }
    }

    public void testRemoveDictionary() {
        Cursor c = null;
        try {
            long id = businessLogic.addDictionary("test", "description");
            c = businessLogic.queryDictionaries();
            assertEquals(1, c.getCount());
            c.close();
            businessLogic.removeDictionary(id);
            c = businessLogic.queryDictionaries();
            assertEquals(0, c.getCount());
            c.close();
            long[] ids = new long[]{
                    businessLogic.addDictionary("test1", ""),
                    businessLogic.addDictionary("test2", ""),
                    businessLogic.addDictionary("test3", ""),
            };
            c = businessLogic.queryDictionaries();
            assertEquals(3, c.getCount());
            c.close();
            for (long i : ids)
                businessLogic.removeDictionary(i);
            c = businessLogic.queryDictionaries();
            assertEquals(0, c.getCount());
            c.close();
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        } finally {
            if ((null != c) && !c.isClosed()) {
                c.close();
            }
        }
    }

    public void testRemoveCard() {
        Cursor c = null;
        try {
            long id = businessLogic.addDictionary("test", "");
            long cardId = businessLogic.addCard(id, "front", "back");
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.close();
            businessLogic.removeCard(id, cardId);
            c = businessLogic.queryCards(id);
            assertEquals(0, c.getCount());
            c.close();
            long[] ids = new long[] {
                    businessLogic.addCard(id, "front1", "back1"),
                    businessLogic.addCard(id, "front2", "back2"),
                    businessLogic.addCard(id, "front3", "back3")
            };
            c = businessLogic.queryCards(id);
            assertEquals(3, c.getCount());
            c.close();
            for (long i : ids)
                businessLogic.removeCard(id, i);
            c = businessLogic.queryCards(id);
            assertEquals(0, c.getCount());
            c.close();
        } catch (SQLException e) {
            assertTrue(e.getMessage(), false);
        } finally {
            if ((null != c) && !c.isClosed()) {
                c.close();
            }
        }
    }

    public void testTraining() {
        Cursor c = null;
        boolean allRight;
        try {
            long id = businessLogic.addDictionary("test", "");
            try {
                allRight = false;
                businessLogic.startCardTraining(id);
            } catch (BusinessLogic.NoCardsException e) { allRight = true; }
            assertTrue("Call startCardTraining() with empty dictionary must lead to NoCardsException.", allRight);
            long cardId = businessLogic.addCard(id, "front", "back");
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            Card card = new Card(c);
            assertEquals(cardId, card.id);
            assertEquals("front", card.front);
            assertEquals("back", card.back);
            assertEquals(0, card.scrutiny);
            c.close();
            businessLogic.startCardTraining(id);
            businessLogic.nextCard();
            assertTrue(businessLogic.guess("back"));
            businessLogic.nextCard();
            assertTrue(businessLogic.guess("back"));
            try {
                allRight = false;
                businessLogic.guess("back");
            } catch (IllegalStateException e) { allRight = true; }
            assertTrue("Call guess() without prior nextCard() must lead to IllegalStateException.", allRight);
            businessLogic.stopCardTraining();
            c = businessLogic.queryCards(id);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            card = new Card(c);
            assertEquals(cardId, card.id);
            assertEquals("front", card.front);
            assertEquals("back", card.back);
            assertEquals(0, card.scrutiny);
            c.close();
        } catch (SQLException | BusinessLogic.NoCardsException e) {
            assertTrue(e.getMessage(), false);
        } finally {
            if ((null != c) && !c.isClosed()) {
                c.close();
            }
        }
    }
}
