package com.nolan.learnenglishwords;

import com.nolan.learnenglishwords.utils.DateStringConverter;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TestDateStringConverter extends TestCase {
    public void testDateToString() {
        GregorianCalendar calendar = new GregorianCalendar(2015, 0, 1, 20, 43, 54);
        calendar.add(Calendar.MILLISECOND, 432);
        Date d = calendar.getTime();
        assertEquals("2015-01-01 20:43:54.432", DateStringConverter.toString(d));
    }

    public void testStringToDate() {
        GregorianCalendar calendar = new GregorianCalendar(2015, 0, 1, 20, 43, 54);
        calendar.add(Calendar.MILLISECOND, 432);
        Date d = calendar.getTime();
        assertEquals(d, DateStringConverter.parse("2015-01-01 20:43:54.432"));
    }
}